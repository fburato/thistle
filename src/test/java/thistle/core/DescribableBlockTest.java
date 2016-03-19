package thistle.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DescribableBlockTest {

    DescribableBlock testee;

    @Mock
    Block block;

    @Before
    public void setUp() {
        testee = new DescribableBlock("foo", block);
    }

    @Test
    public void shouldReturnExcpectedDescription() {
        assertThat(testee.describe(), equalTo("foo"));
    }

    @Test
    public void shouldExecutePassedBlock() throws Exception {
        testee.execute();
        verify(block).execute();
    }

    @Test
    public void shouldInitFieldsCorrectly() {
        assertThat(testee.block, equalTo(block));
        assertThat(testee.condition, equalTo("foo"));
    }
}
