package thistle.runner;

import org.junit.runner.RunWith;
import thistle.Describe;
import thistle.Finally;
import thistle.Then;
import thistle.When;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(ThistleRunner.class)
@Describe
public class ThistleRunnerTest {

    int i = 1;
    int j = 1;

    @When("i is multiplied by 2")
    public void w1() {
        i *= 2;
        assertThat(i, equalTo(2));
        assertThat(j, equalTo(1));
    }

    @Then("i times 3 is 6")
    public void t1() {
        i *= 3;
        assertThat(i, equalTo(6));
        assertThat(j, equalTo(1));
    }

    @Then("i times 5 is 10")
    public void t2() {
        i *= 5;
        assertThat(i, equalTo(10));
        assertThat(j, equalTo(1));
    }

    @Finally("i is grater than 1")
    public void f1() {
        assertThat(i > 1, is(true));
    }

    @Describe("Set j to 7")
    public class C1 {

        public C1() {
            assertThat(i, equalTo(2));
            assertThat(j, equalTo(1));
            j = 7;
        }

        @When("i is multiplied by 13 and j is multiplied by 17")
        public void w1() {
            i *= 13;
            j *= 17;
        }

        @Then("i times 23 is 598")
        public void t1() {
            i *= 23;
            assertThat(i, equalTo(598));
        }

        @Finally("j is grater than 1")
        public void f1() {
            assertThat(j > 1, is(true));
        }

        @Describe
        public class C2 {

            @When("i is multiplied by 29")
            public void w1() {
                i *= 29;
            }

            @When("j is multiplied by 31")
            public void w2() {
                assertThat(i, equalTo(754));
                j *= 31;
            }

            @Then("i times 37 is 27898")
            public void t1() {
                i *= 37;
                assertThat(i, equalTo(27898));
            }

            @Then("j times 41 is 151249")
            public void t2() {
                j *= 41;
                assertThat(i, equalTo(754));
                assertThat(j, equalTo(151249));
            }
        }
    }
}
