package thistleexamples;

import org.junit.runner.RunWith;
import thistle.Describe;
import thistle.Then;
import thistle.When;
import thistle.runner.ThistleRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(ThistleRunner.class)
@Describe
public class ImprovedJUnitTest {

    private int i;

    @When("i is initialised to 2")
    public void setUp() {
        i = 2;
    }

    @Then("i times 3 is 6")
    public void t1() {
        assertThat(i * 3, equalTo(6));
    }

    @Then("i times 4 is 8")
    public void t2() {
        assertThat(i * 4, equalTo(8));
    }

    @Then("i times 5 is 10")
    public void t3() {
        assertThat(i * 5, equalTo(10));
    }
}
