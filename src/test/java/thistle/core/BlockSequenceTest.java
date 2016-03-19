package thistle.core;

import org.junit.Test;
import org.mockito.InOrder;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class BlockSequenceTest {
    Block block1 = mock(Block.class),
            block2 = mock(Block.class);
    BlockSequence testee = new BlockSequence(Arrays.asList(block1, block2));

    @Test
    public void shouldReturnPremisesInTheExpectedOrder() {
        assertThat(testee.sequence.get(0), equalTo(block1));
        assertThat(testee.sequence.get(1), equalTo(block2));
    }

    @Test
    public void shouldExecuteThePremisesInTheCorrectOrder() throws Exception {
        InOrder inOrder = inOrder(block1, block2);

        testee.execute();

        inOrder.verify(block1).execute();
        inOrder.verify(block2).execute();
    }

    @Test
    public void shouldConcatenatePremises() throws Exception {
        Block block3 = mock(Block.class),
                block4 = mock(Block.class);

        BlockSequence other = new BlockSequence(Arrays.asList(block3, block4));
        BlockSequence result = testee.catenate(other);

        InOrder inOrder = inOrder(block1, block2, block3, block4);

        result.execute();

        inOrder.verify(block1).execute();
        inOrder.verify(block2).execute();
        inOrder.verify(block3).execute();
        inOrder.verify(block4).execute();
    }
}
