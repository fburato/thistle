package thistle.runner.validtestclasses;

import thistle.Describe;
import thistle.Then;

@Describe("TestClassWithTestCase")
public class TestClassWithTestCase {
    public static boolean init = false;
    public static boolean called = false;

    {
        called = false;
        init = true;
    }

    @Then("Method called")
    public void t1() {
        called = true;
    }
}
