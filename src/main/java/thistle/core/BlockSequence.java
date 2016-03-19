package thistle.core;

import com.google.common.collect.ImmutableList;
import org.junit.runners.model.Statement;

import java.util.Collections;
import java.util.List;

public class BlockSequence extends Statement implements Block {

    public static final BlockSequence EMPTY_SEQUENCE = new BlockSequence(Collections.<Block>emptyList());
    public final ImmutableList<? extends Block> sequence;

    public BlockSequence(List<? extends Block> premises) {
        this.sequence = ImmutableList.<Block>builder().addAll(premises).build();
    }

    @Override
    public void execute() throws Exception {
        for (Block block : sequence) {
            block.execute();
        }
    }

    public BlockSequence catenate(BlockSequence other) {
        return new BlockSequence(ImmutableList.<Block>builder()
                .addAll(sequence)
                .addAll(other.sequence)
                .build());
    }


    @Override
    public void evaluate() throws Throwable {
        execute();
    }
}
