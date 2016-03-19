package thistle.runner;

import org.junit.Test;
import thistle.runner.invalidtestclasses.*;
import thistle.runner.validtestclasses.*;
import thistle.suite.SpecificationUnwrapper;
import thistle.suite.TestCase;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReflectiveSpecificationTest {
    SpecificationUnwrapper specificationUnwrapper = new SpecificationUnwrapper();
    ReflectiveSpecification testee = new ReflectiveSpecification(specificationUnwrapper);

    @Test
    public void shouldExecuteInitAndThen() throws Exception {
        List<TestCase> testCases = testee.parse(TestClassWithTestCase.class);

        assertThat(testCases.size(), equalTo(1));

        executeTestCases(testCases);

        assertThat(TestClassWithTestCase.init, is(true));
        assertThat(TestClassWithTestCase.called, is(true));
    }


    @Test
    public void shouldReturnExpectedTestCasesForNormalSpecification() throws Exception {
        List<TestCase> testCases = testee.parse(TestClassWithEverything.class);

        assertThat(testCases.size(), equalTo(2));

        testCases.get(0).testExecution.execute();

        assertThat(TestClassWithEverything.initCalled, equalTo(1));
        assertThat(TestClassWithEverything.when1Called, equalTo(1));
        assertThat(TestClassWithEverything.when2Called, equalTo(1));
        assertThat(TestClassWithEverything.then1Called, equalTo(1));
        assertThat(TestClassWithEverything.then2Called, equalTo(0));
        assertThat(TestClassWithEverything.finally1Called, equalTo(1));
        assertThat(TestClassWithEverything.finally2Called, equalTo(1));

        testCases.get(1).testExecution.execute();

        assertThat(TestClassWithEverything.initCalled, equalTo(1));
        assertThat(TestClassWithEverything.when1Called, equalTo(1));
        assertThat(TestClassWithEverything.when2Called, equalTo(1));
        assertThat(TestClassWithEverything.then1Called, equalTo(0));
        assertThat(TestClassWithEverything.then2Called, equalTo(1));
        assertThat(TestClassWithEverything.finally1Called, equalTo(1));
        assertThat(TestClassWithEverything.finally2Called, equalTo(1));
    }

    @Test
    public void shouldExecuteExpectedTestCasesForNestedSpecification() throws Exception {
        List<TestCase> testCases = testee.parse(TestClassWithNestedSpecification.class);

        assertThat(testCases.size(), equalTo(2));

        testCases.get(0).testExecution.execute();

        assertThat(TestClassWithNestedSpecification.initCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.whenCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.thenCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.initInternalCalled, equalTo(0));
        assertThat(TestClassWithNestedSpecification.whenInternalCalled, equalTo(0));
        assertThat(TestClassWithNestedSpecification.thenInternalCalled, equalTo(0));
        assertThat(TestClassWithNestedSpecification.finallyInternalCalled, equalTo(0));
        assertThat(TestClassWithNestedSpecification.finallyCalled, equalTo(1));

        testCases.get(1).testExecution.execute();

        assertThat(TestClassWithNestedSpecification.initCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.whenCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.thenCalled, equalTo(0));
        assertThat(TestClassWithNestedSpecification.initInternalCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.whenInternalCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.thenInternalCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.finallyInternalCalled, equalTo(1));
        assertThat(TestClassWithNestedSpecification.finallyCalled, equalTo(1));
    }
    private void executeTestCases(List<TestCase> testCases) throws Exception {
        for (TestCase testCase : testCases) {
            testCase.testExecution.execute();
        }
    }

    @Test(expected = ThistleException.class)
    public void shouldThrowThistleExceptionIfClassNotAnnotatedWithDescribe() {
        testee.parse(TestClassWithoutDescribe.class);
    }

    @Test(expected = ThistleException.class)
    public void shouldThrowThistleExceptionIfDefaultConstructorNotAvailable() {
       testee.parse(TestClassWithNonEmptyConstructor.class);
    }

    @Test(expected = ThistleException.class)
    public void shouldThrowThistleExceptionIfWhenAsParameters() {
        testee.parse(TestClassWithWhenParameters.class);
    }

    @Test(expected = ThistleException.class)
    public void shouldThrowThistleExceptionIfThenAsParameters() {
        testee.parse(TestClassWithThenParameter.class);
    }

    @Test(expected = ThistleException.class)
    public void shouldThrowThistleExceptionIfFinallyAsParameters() {
        testee.parse(TestClassWithFinallyParameters.class);
    }
}
