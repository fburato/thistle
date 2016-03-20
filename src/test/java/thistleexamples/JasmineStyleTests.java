package thistleexamples;

import org.junit.runner.RunWith;
import thistle.Describe;
import thistle.Finally;
import thistle.Then;
import thistle.When;
import thistle.runner.ThistleRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(ThistleRunner.class)
@Describe
public class JasmineStyleTests {
    private int i = 1;

    @Describe("even number tests")
    public class EvenNumber {

        @When("i is an even number")
        public void w() {
            i = 2;
        }

        @Then("multiply i by an even number")
        public void t1() {
            i *= 2;
        }

        @Then("multiply i by an odd number")
        public void t2() {
            i *= 3;
        }

        @Finally("i is still even")
        public void f() {
            assertThat(i % 2 == 0, is(true));
        }
    }

    @Describe("odd number times even number")
    public class OddNumber1 {
        @When("i is an odd number")
        public void w() {
            i = 3;
        }

        @Then("multiply i by an even number")
        public void t() {
            i *= 2;
        }

        @Finally("i is even")
        public void f() {
            assertThat(i % 2 == 0, is(true));
        }
    }

    @Describe("odd number times odd number")
    public class OddNumber2 {
        @When("i is an odd number")
        public void w() {
            i = 3;
        }

        @Then("multiply i by an odd number")
        public void t() {
            i *= 5;
        }

        @Finally("i is odd")
        public void f() {
            assertThat(i % 2 == 0, is(false));
        }
    }
}
