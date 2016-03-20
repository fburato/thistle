package thistle.suite;

import org.junit.Before;
import org.junit.Test;
import thistle.core.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static thistle.core.FinallyBlock.finallyBlock;
import static thistle.core.ThenBlock.thenBlock;
import static thistle.core.WhenBlock.whenBlock;
import static thistle.suite.TestDescription.testDescription;

public class SpecificationUnwrapperTest {
    Block exec1 = mock(Block.class),
            exec2 = mock(Block.class),
            exec3 = mock(Block.class),
            exec4 = mock(Block.class);

    WhenBlock premise1 = whenBlock("Premise 1", exec1),
            premise2 = whenBlock("Premise 2", exec2);

    ThenBlock case1 = thenBlock("Do 1", exec3),
            case2 = thenBlock("Do 2", exec4);

    FinallyBlock finally1 = finallyBlock("Close 1", exec1),
            finally2 = finallyBlock("Close 2", exec2);

    Specification simpleSpecification;

    Specification nestedSpecification;

    Specification doublyNestedSpecification;

    SpecificationUnwrapper testee = new SpecificationUnwrapper();

    private static void verifyTestCases(List<TestCase> actual, List<TestCase> expected) {
        assertThat(actual.size(), equalTo(expected.size()));
        for (int i = 0; i < actual.size(); i++) {
            TestCase actualTestCase = actual.get(i), expectedTestCase = expected.get(i);
            assertThat(actualTestCase.testDescription.descriptions, equalTo(expectedTestCase.testDescription.descriptions));
            assertThat(actualTestCase.testDescription.conditions, equalTo(expectedTestCase.testDescription.conditions));
            assertThat(actualTestCase.testDescription.testCase, equalTo(expectedTestCase.testDescription.testCase));
            assertThat(actualTestCase.testDescription.after, equalTo(expectedTestCase.testDescription.after));
            assertThat(actualTestCase.testExecution.sequence.size(), equalTo(expectedTestCase.testExecution.sequence.size()));
            for (int j = 0; j < actualTestCase.testExecution.sequence.size(); j++) {
                Block actualBlock = actualTestCase.testExecution.sequence.get(j),
                        expectedBlock = expectedTestCase.testExecution.sequence.get(j);
                assertThat(actualBlock, equalTo(expectedBlock));
            }
        }
    }

    private static TestCase testCase(TestDescription descriptor, Block... expectedSequence) {
        return new TestCase(descriptor, new BlockSequence(Arrays.asList(expectedSequence)));
    }

    @Before
    public void setUp() {
        simpleSpecification = Specification.builder()
                .description("TestDescription 1")
                .premises(Arrays.asList(premise1, premise2))
                .cases(Arrays.asList(case1, case2))
                .build();
        nestedSpecification = Specification.builder()
                .initialisation(exec1)
                .description("TestDescription 2")
                .premises(Arrays.asList(premise1))
                .cases(Arrays.asList(case2))
                .finallyDo(Arrays.asList(finally1, finally2))
                .build();
        nestedSpecification.addSubSpecification(simpleSpecification);
        doublyNestedSpecification = Specification.builder()
                .initialisation(exec2)
                .description("TestDescription 3")
                .premises(Arrays.asList(premise2))
                .cases(Arrays.asList(case1))
                .build();
        doublyNestedSpecification.addSubSpecification(nestedSpecification);
        doublyNestedSpecification.addSubSpecification(simpleSpecification);
    }

    @Test
    public void shouldProcessSimpleSpecificationAsExpected() {
        List<TestCase> actual = testee.unwrap(simpleSpecification);
        List<TestCase> expected = Arrays.asList(
                testCase(testDescription(asList("TestDescription 1"), asList("Premise 1", "Premise 2"), "Do 1", Collections.<String>emptyList()), Block.NOP, premise1, premise2, case1),
                testCase(testDescription(asList("TestDescription 1"), asList("Premise 1", "Premise 2"), "Do 2", Collections.<String>emptyList()), Block.NOP, premise1, premise2, case2)
        );
        verifyTestCases(actual, expected);
    }

    @Test
    public void shouldProcessSingleNestedSpecificationAsExpected() {
        List<TestCase> actual = testee.unwrap(nestedSpecification);
        List<TestCase> expected = Arrays.asList(testCase(testDescription(asList("TestDescription 2"), asList("Premise 1"), "Do 2", asList("Close 1", "Close 2")), exec1, premise1, case2, finally1, finally2),
                testCase(testDescription(asList("TestDescription 2", "TestDescription 1"), asList("Premise 1", "Premise 1", "Premise 2"), "Do 1", asList("Close 1", "Close 2")), exec1, premise1, Block.NOP, premise1, premise2, case1, finally1, finally2),
                testCase(testDescription(asList("TestDescription 2", "TestDescription 1"), asList("Premise 1", "Premise 1", "Premise 2"), "Do 2", asList("Close 1", "Close 2")), exec1, premise1, Block.NOP, premise1, premise2, case2, finally1, finally2)
        );
        verifyTestCases(actual, expected);
    }

    @Test
    public void shouldProcessDoubleNestedSpecificationAsExpected() {
        List<TestCase> actual = testee.unwrap(doublyNestedSpecification);
        List<TestCase> expected = Arrays.asList(
                testCase(testDescription(asList("TestDescription 3"), asList("Premise 2"), "Do 1", Collections.<String>emptyList()), exec2, premise2, case1),
                testCase(testDescription(asList("TestDescription 3", "TestDescription 2"), asList("Premise 2", "Premise 1"), "Do 2", asList("Close 1", "Close 2")), exec2, premise2, exec1, premise1, case2, finally1, finally2),
                testCase(testDescription(asList("TestDescription 3", "TestDescription 2", "TestDescription 1"), asList("Premise 2", "Premise 1", "Premise 1", "Premise 2"), "Do 1", asList("Close 1", "Close 2")), exec2, premise2, exec1, premise1, Block.NOP, premise1, premise2, case1, finally1, finally2),
                testCase(testDescription(asList("TestDescription 3", "TestDescription 2", "TestDescription 1"), asList("Premise 2", "Premise 1", "Premise 1", "Premise 2"), "Do 2", asList("Close 1", "Close 2")), exec2, premise2, exec1, premise1, Block.NOP, premise1, premise2, case2, finally1, finally2),
                testCase(testDescription(asList("TestDescription 3", "TestDescription 1"), asList("Premise 2", "Premise 1", "Premise 2"), "Do 1", Collections.<String>emptyList()), exec2, premise2, Block.NOP, premise1, premise2, case1),
                testCase(testDescription(asList("TestDescription 3", "TestDescription 1"), asList("Premise 2", "Premise 1", "Premise 2"), "Do 2", Collections.<String>emptyList()), exec2, premise2, Block.NOP, premise1, premise2, case2)
        );
        verifyTestCases(actual, expected);
    }
}
