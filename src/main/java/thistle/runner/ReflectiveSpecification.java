package thistle.runner;

import com.google.common.base.Optional;
import thistle.Describe;
import thistle.Finally;
import thistle.Then;
import thistle.When;
import thistle.core.Block;
import thistle.core.FinallyBlock;
import thistle.core.ThenBlock;
import thistle.core.WhenBlock;
import thistle.suite.Specification;
import thistle.suite.SpecificationUnwrapper;
import thistle.suite.TestCase;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static thistle.core.FinallyBlock.finallyBlock;
import static thistle.core.ThenBlock.thenBlock;
import static thistle.core.WhenBlock.whenBlock;

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
        String description = getDescribedValueOrThrow(type);
        StateBlock initBlock = getInitialisationBlockOrThrow(type);
        List<WhenBlock> whenBlocks = getWhenBlocksOrThrow(type, initBlock.state);
        List<ThenBlock> thenBlocks = getThenBlocksOrThrow(type, initBlock.state);
        List<FinallyBlock> finallyBlocks = getFinallyBlocksOrThrow(type, initBlock.state);
        Specification parentSpecification = Specification.builder()
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
        for (Annotation annotation : type.getAnnotations()) {
            if (annotation instanceof Describe) {
                return ((Describe) annotation).value();
            }
        }
        throw new ThistleException(String.format(NOT_ANNOTATED_WITH_DESCRIBE, type.getCanonicalName()));
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

    private List<WhenBlock> getWhenBlocksOrThrow(Class<?> type, final State state) {
        List<WhenBlock> whenBlocks = new ArrayList<WhenBlock>();
        for (Method method : type.getMethods()) {
            Optional<String> whenValue = getWhenMethodDescription(method);
            if (whenValue.isPresent()) {
                StateBlock execution = getZeroParameterMethodInvocationClosureOrThrow(method, state);
                whenBlocks.add(whenBlock(whenValue.get(), execution));
            }
        }
        return whenBlocks;
    }

    private List<ThenBlock> getThenBlocksOrThrow(Class<?> type, final State state) {
        List<ThenBlock> whenBlocks = new ArrayList<ThenBlock>();
        for (Method method : type.getMethods()) {
            Optional<String> thenValue = getThenMethodDescription(method);
            if (thenValue.isPresent()) {
                StateBlock execution = getZeroParameterMethodInvocationClosureOrThrow(method, state);
                whenBlocks.add(thenBlock(thenValue.get(), execution));
            }
        }
        return whenBlocks;
    }

    private List<FinallyBlock> getFinallyBlocksOrThrow(Class<?> type, State state) {
        List<FinallyBlock> whenBlocks = new ArrayList<FinallyBlock>();
        for (Method method : type.getMethods()) {
            Optional<String> thenValue = getFinallyMethodDescription(method);
            if (thenValue.isPresent()) {
                StateBlock execution = getZeroParameterMethodInvocationClosureOrThrow(method, state);
                whenBlocks.add(finallyBlock(thenValue.get(), execution));
            }
        }
        return whenBlocks;
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

    private Optional<String> getWhenMethodDescription(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof When) {
                return Optional.of(((When) annotation).value());
            }
        }
        return Optional.absent();
    }

    private Optional<String> getThenMethodDescription(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof Then) {
                return Optional.of(((Then) annotation).value());
            }
        }
        return Optional.absent();
    }

    private Optional<String> getFinallyMethodDescription(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof Finally) {
                return Optional.of(((Finally) annotation).value());
            }
        }
        return Optional.absent();
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

    private Optional<String> getDescriptionIfAnnotated(Class innerClass) {
        for (Annotation annotation : innerClass.getAnnotations()) {
            if (annotation instanceof Describe) {
                return Optional.of(((Describe) annotation).value());
            }
        }
        return Optional.absent();
    }

    private Specification getSpecification(Class<?> type, Class<?> superType, String description, StateBlock superInitBlock) {
        StateBlock initBlock = getInitialisationBlockForSubSpecificationOrThrow(type, superType, superInitBlock);
        List<WhenBlock> whenBlocks = getWhenBlocksOrThrow(type, initBlock.state);
        List<ThenBlock> thenBlocks = getThenBlocksOrThrow(type, initBlock.state);
        List<FinallyBlock> finallyBlocks = getFinallyBlocksOrThrow(type, initBlock.state);
        Specification parentSpecification = Specification.builder()
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
