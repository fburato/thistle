package thistle.runner;

import com.google.common.base.Optional;
import thistle.Describe;
import thistle.core.*;
import thistle.suite.Specification;
import thistle.suite.SpecificationUnwrapper;
import thistle.suite.TestCase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectiveSpecification {

    private static final String MISSING_DEFAULT_CONSTRUCTOR = "The test class '%s' does not have a default constructor";
    private static final String NOT_ANNOTATED_WITH_DESCRIBE = "The root test class '%s' is not annotated with Describe";
    private static final String NEED_PARAMETERLESS_METHOD = "Method '%s' is not parameterless. Then, When and Finally annotation can only be applied to parameterless methods.";
    private final SpecificationUnwrapper specificationUnwrapper;

    public ReflectiveSpecification(SpecificationUnwrapper unwrapper) {
        this.specificationUnwrapper = unwrapper;
    }

    public List<TestCase> parse(Class<?> type) {
        final Specification spec = getSpecification(type);
        return specificationUnwrapper.unwrap(spec);
    }

    private Specification getSpecification(Class<?> type) {
        final String description = getDescribedValueOrThrow(type);
        final StateBlock initBlock = getInitialisationBlockOrThrow(type);
        final List<WhenBlock> whenBlocks = getBlocksOrThrow(type, initBlock.state, WhenBlock.BUILDER, WhenBlock.ANNOTATION_EXTRACTOR);
        final List<ThenBlock> thenBlocks = getBlocksOrThrow(type, initBlock.state, ThenBlock.BUILDER, ThenBlock.ANNOTATION_EXTRACTOR);
        final List<FinallyBlock> finallyBlocks = getBlocksOrThrow(type, initBlock.state, FinallyBlock.BUILDER, FinallyBlock.ANNOTATION_EXTRACTOR);
        final Specification parentSpecification = Specification.builder()
                .description(description)
                .initialisation(initBlock)
                .premises(whenBlocks)
                .cases(thenBlocks)
                .finallyDo(finallyBlocks)
                .build();
        processSubspecifcations(type, parentSpecification, initBlock);
        return parentSpecification;
    }

    private String getDescribedValueOrThrow(Class<?> type) {
        final Optional<String> describableValueOrNothing = getDescriptionIfAnnotated(type);
        if (describableValueOrNothing.isPresent()) {
            return describableValueOrNothing.get();
        } else {
            throw new ThistleException(String.format(NOT_ANNOTATED_WITH_DESCRIBE, type.getCanonicalName()));
        }
    }

    private Optional<String> getDescriptionIfAnnotated(Class innerClass) {
        for (Annotation annotation : innerClass.getAnnotations()) {
            if (annotation instanceof Describe) {
                return Optional.of(((Describe) annotation).value());
            }
        }
        return Optional.absent();
    }

    private StateBlock getInitialisationBlockOrThrow(Class<?> type) {
        try {
            final Constructor contructor = type.getDeclaredConstructor();
            final State state = new State();
            return new StateBlock(state) {
                @Override
                public void execute() throws Exception {
                    state.initialise(contructor.newInstance());
                }
            };
        } catch (NoSuchMethodException exception) {
            throw new ThistleException(String.format(MISSING_DEFAULT_CONSTRUCTOR, type.getCanonicalName()));
        }
    }

    private <T> List<T> getBlocksOrThrow(Class<?> type, final State state, DescribableBlockBuilder<T> builder, AnnotationExtractor<T> extractor) {
        List<T> blocks = new ArrayList<T>();
        for (Method method : type.getMethods()) {
            Optional<String> annotatedDescription = getMethodAnnotatedDescriptionOrNothing(method, extractor);
            if (annotatedDescription.isPresent()) {
                final StateBlock execution = getZeroParameterMethodInvocationClosureOrThrow(method, state);
                blocks.add(builder.build(annotatedDescription.get(), execution));
            }
        }
        return blocks;
    }

    private <T> Optional<String> getMethodAnnotatedDescriptionOrNothing(Method method, AnnotationExtractor<T> extractor) {
        for (Annotation annotation : method.getAnnotations()) {
            final Optional<String> valueOrNothing = extractor.extract(annotation);
            if (valueOrNothing.isPresent()) {
                return valueOrNothing;
            }
        }
        return Optional.absent();
    }

    private StateBlock getZeroParameterMethodInvocationClosureOrThrow(final Method method, State state) {
        if (method.getParameterTypes().length != 0) {
            throw new ThistleException(String.format(NEED_PARAMETERLESS_METHOD, method.getName()));
        }
        return new StateBlock(state) {
            @Override
            public void execute() throws Exception {
                method.invoke(this.state.instance);
            }
        };
    }

    private void processSubspecifcations(Class<?> type, Specification parentSpecification, StateBlock initBlock) {
        Class<?>[] declaredInnerClasses = type.getDeclaredClasses();
        for (Class innerClass : declaredInnerClasses) {
            Optional<String> description = getDescriptionIfAnnotated(innerClass);
            if (description.isPresent()) {
                Specification subSpecification = getSpecification(innerClass, type, description.get(), initBlock);
                parentSpecification.addSubSpecification(subSpecification);
            }
        }
    }

    private Specification getSpecification(Class<?> type, Class<?> superType, String description, StateBlock superInitBlock) {
        final StateBlock initBlock = getInitialisationBlockForSubSpecificationOrThrow(type, superType, superInitBlock);
        final List<WhenBlock> whenBlocks = getBlocksOrThrow(type, initBlock.state, WhenBlock.BUILDER, WhenBlock.ANNOTATION_EXTRACTOR);
        final List<ThenBlock> thenBlocks = getBlocksOrThrow(type, initBlock.state, ThenBlock.BUILDER, ThenBlock.ANNOTATION_EXTRACTOR);
        final List<FinallyBlock> finallyBlocks = getBlocksOrThrow(type, initBlock.state, FinallyBlock.BUILDER, FinallyBlock.ANNOTATION_EXTRACTOR);
        final Specification parentSpecification = Specification.builder()
                .description(description)
                .initialisation(initBlock)
                .premises(whenBlocks)
                .cases(thenBlocks)
                .finallyDo(finallyBlocks)
                .build();
        processSubspecifcations(type, parentSpecification, initBlock);
        return parentSpecification;
    }

    private StateBlock getInitialisationBlockForSubSpecificationOrThrow(Class<?> type, Class<?> superType, final StateBlock superInitBlock) {
        try {
            final Constructor contructor = type.getDeclaredConstructor(superType);
            final State state = new State();
            return new StateBlock(state) {
                @Override
                public void execute() throws Exception {
                    Object superClassInstance = superInitBlock.state.instance;
                    state.initialise(contructor.newInstance(superClassInstance));
                }
            };
        } catch (NoSuchMethodException exception) {
            throw new ThistleException(String.format(MISSING_DEFAULT_CONSTRUCTOR, type.getCanonicalName()));
        }
    }

    private static class State {
        public Object instance;

        public void initialise(Object instance) {
            this.instance = instance;
        }
    }

    private static abstract class StateBlock implements Block {
        public final State state;

        public StateBlock(State state) {
            this.state = state;
        }
    }
}
