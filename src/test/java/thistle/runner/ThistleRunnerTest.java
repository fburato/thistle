package thistle.runner;

import org.junit.runner.RunWith;
import thistle.Describe;
import thistle.Finally;
import thistle.Then;
import thistle.When;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(ThistleRunner.class)
@Describe("My first Thistle test")
public class ThistleRunnerTest {

    @When("nothing is set up")
    public void w1() {
        System.out.println(1);
    }

    @Then("one plus one equal two")
    public void t1() {
        System.out.println(2);
        assertEquals(1+1,2);
    }

    @Then("fail causes error")
    public void t2() {
        System.out.println(3);
    }

    @Describe("Also inner specification")
    public class C1 {
        @When("this happens too")
        public void w1() {
            System.out.println(4);
        }

        @Then("something magic will happen")
        public void t1() {
            System.out.println(5);
        }

        @Describe("Also inner specification")
        public class C2 {
            @When("this happens too")
            public void w1() {
                System.out.println(6);
            }

            @Then("something magic will happen")
            public void t1() {
                System.out.println(7);
            }

            @Then("something magic will happen")
            public void t2() {
                System.out.println(8);
            }
        }

        @Finally("close first inner")
        public void f1() {
            System.out.println(9);
        }
    }

    @Finally("close everything")
    public void f1() {
        System.out.println(10);
    }
}
