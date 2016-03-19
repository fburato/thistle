package thistle.runner.invalidtestclasses;

import thistle.Describe;
import thistle.Finally;

@Describe
public class TestClassWithFinallyParameters {

    @Finally("")
    public void f1(int i){

    }
}
