package thistle.runner;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import thistle.Describe;
import thistle.suite.SpecificationUnwrapper;
import thistle.suite.TestCase;

import java.util.List;

public class ThistleRunner extends ParentRunner<TestCase> {

    private static final SpecificationUnwrapper specificationUnwrapper = new SpecificationUnwrapper();
    private static final ReflectiveSpecification reflectiveSpecification = new ReflectiveSpecification(specificationUnwrapper);
    private List<TestCase> testCases;

    public ThistleRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        try{
            testCases = reflectiveSpecification.parse(testClass);
        } catch (ThistleException e) {
            throw new InitializationError(e.getMessage());
        }
    }

    @Override
    protected List<TestCase> getChildren() {
        return testCases;
    }

    @Override
    protected Description describeChild(TestCase child) {
        return Description.createSuiteDescription(child.testName);
    }

    @Override
    protected void runChild(final TestCase child, RunNotifier notifier) {
        runLeaf(child.testExecution, describeChild(child), notifier);
    }
}
