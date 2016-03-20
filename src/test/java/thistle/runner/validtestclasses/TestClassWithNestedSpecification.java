package thistle.runner.validtestclasses;

import thistle.Describe;
import thistle.Finally;
import thistle.Then;
import thistle.When;

@Describe("Nested class")
public class TestClassWithNestedSpecification {

    public static int initCalled = 0;
    public static int whenCalled = 0;
    public static int thenCalled = 0;
    public static int initInternalCalled = 0;
    public static int whenInternalCalled = 0;
    public static int thenInternalCalled = 0;
    public static int finallyInternalCalled = 0;
    public static int finallyCalled = 0;

    {
        initCalled = whenCalled = thenCalled = initInternalCalled = whenInternalCalled = thenInternalCalled = finallyInternalCalled = finallyCalled = 0;
        initCalled++;
    }

    @When("When")
    public void v1() {
        whenCalled++;
    }

    @Then("Then")
    public void t1() {
        thenCalled++;
    }

    @Finally("Finally")
    public void f1() {
        finallyCalled++;
    }

    @Describe("Internal class")
    public class NestedClass {
        {
            initInternalCalled++;
        }

        @When("When internal")
        public void wInternal() {
            whenInternalCalled++;
        }

        @Then("Then Internal")
        public void tInternal() {
            thenInternalCalled++;
        }

        @Finally("Finally Internal")
        public void fInternal() {
            finallyInternalCalled++;
        }
    }
}
