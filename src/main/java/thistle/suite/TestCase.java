package thistle.suite;

import thistle.core.BlockSequence;

public class TestCase {

    public final String testName;
    public final BlockSequence testExecution;

    public TestCase(String testName, BlockSequence testExecution) {
        this.testName = testName;
        this.testExecution = testExecution;
    }

    public static TestCase testCase(String testName, BlockSequence testExecution) {
        return new TestCase(testName, testExecution);
    }
}
