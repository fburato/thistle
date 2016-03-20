package thistle.runner.validtestclasses;

import thistle.Describe;
import thistle.Finally;

@Describe("TestClassWithFinally")
public class TestClassWithFinally {

    public static boolean init = false;
    public static boolean called = false;

    {
        init = true;
        called = false;
    }

    @Finally("finally1")
    public void f1() {
        called = true;
    }
}
