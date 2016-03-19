package thistle.suite;

import thistle.core.BlockSequence;

public class TestCase {

    public final TestDescription testDescription;
    public final BlockSequence testExecution;

    public TestCase(TestDescription testDescription, BlockSequence testExecution) {
        this.testDescription = testDescription;
        this.testExecution = testExecution;
    }

    public static TestCase testCase(TestDescription testDescription, BlockSequence testExecution) {
        return new TestCase(testDescription, testExecution);
    }
}
