package thistle.runner;

import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static thistle.suite.TestDescription.testDescription;

public class TestDescriptionProcessorTest {

    TestDescriptionProcessor testee = new TestDescriptionProcessor();

    @Test
    public void shouldFormatCorrectlyWithEmptyDescription() {
        assertThat(testee.getDescription(testDescription(Collections.<String>emptyList(),asList("a","b"),"c",asList(""))),
                equalTo("When a and b, then c"));
    }

    @Test
    public void shouldFormatCorrectlyWithDescription() {
        assertThat(testee.getDescription(testDescription(asList("a","b"),asList("d","e"),"c",asList(""))),
                equalTo("a, b: When d and e, then c"));
    }

    @Test
    public void shouldIgnoreEmptyStrings() {
        assertThat(testee.getDescription(testDescription(asList("a", "","b"),asList("","d","e",""),"c",asList("", "g"))),
                equalTo("a, b: When d and e, then c. Finally g"));
    }

    @Test
    public void shouldFormatCorrectlyWithAfterStrings() {
        assertThat(testee.getDescription(testDescription(asList("a","b"),asList("d","e"),"c",asList("g", "h"))),
                equalTo("a, b: When d and e, then c. Finally g and h"));
    }

    @Test
    public void shouldFormatCorrectlyWithOnlyTest() {
        assertThat(testee.getDescription(testDescription(asList(""),asList(""),"c",asList(""))),
                equalTo("c"));
    }

    @Test
    public void shouldFormatCorrectlyWithTestAndAfter() {
        assertThat(testee.getDescription(testDescription(asList(""),asList(""),"c",asList("g"))),
                equalTo("c. Finally g"));
    }
}
