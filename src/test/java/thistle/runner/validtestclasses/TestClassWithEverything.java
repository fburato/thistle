package thistle.runner.validtestclasses;

import thistle.Describe;
import thistle.Finally;
import thistle.Then;
import thistle.When;

@Describe("TestDescription")
public class TestClassWithEverything {

    public static int initCalled = 0;
    public static int when1Called = 0;
    public static int when2Called = 0;
    public static int then1Called = 0;
    public static int then2Called = 0;
    public static int finally1Called = 0;
    public static int finally2Called = 0;

    {
        initCalled = when1Called = when2Called = then1Called = then2Called = finally1Called = finally2Called = 0;
        initCalled++;
    }

    @When("When 1")
    public void w1() {
        when1Called++;
    }

    @When("When 2")
    public void w2() {
        when2Called++;
    }

    @Then("Then 1")
    public void t1() {
        then1Called++;
    }

    @Then("Then 2")
    public void t2() {
        then2Called++;
    }

    @Finally("Finally 1")
    public void f1() {
        finally1Called++;
    }

    @Finally("Finally 2")
    public void f2() {
        finally2Called++;
    }

}
