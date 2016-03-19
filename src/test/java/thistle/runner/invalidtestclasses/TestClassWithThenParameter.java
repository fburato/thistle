package thistle.runner.invalidtestclasses;

import thistle.Describe;
import thistle.Then;

@Describe
public class TestClassWithThenParameter {

    @Then("")
    public void t1(Object ... s) {

    }
}
