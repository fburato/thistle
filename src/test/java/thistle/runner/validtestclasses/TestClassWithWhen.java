package thistle.runner.validtestclasses;

import thistle.Describe;
import thistle.When;

@Describe("something")
public class TestClassWithWhen {

    public static boolean init = false;
    public static boolean whenCalled = false;

    {
        whenCalled = false;
        init = true;
    }

    @When("")
    public void w1() {
        whenCalled = true;
    }

}
