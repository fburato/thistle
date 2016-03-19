package thistle.runner.validtestclasses;

import thistle.Describe;

@Describe
public class TestClassWithInitialisation {
    public static boolean init = false;
    {
        init = true;
    }
}
