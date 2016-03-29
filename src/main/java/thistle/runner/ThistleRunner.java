package thistle.runner;


import org.junit.runners.model.InitializationError;

public class ThistleRunner extends ThistleBaseRunner {
    private static final TestDescriptionProcessor testDescriptionProcessor = new DefaultTestDescriptionProcessor();

    public ThistleRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected TestDescriptionProcessor getTestDescriptorProcessor() {
        return testDescriptionProcessor;
    }
}
