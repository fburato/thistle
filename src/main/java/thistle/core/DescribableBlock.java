package thistle.core;

import com.google.common.base.Objects;

public class DescribableBlock implements Block, Describable {
    public final String condition;
    public final Block block;

    public DescribableBlock(String condition, Block block) {
        this.condition = condition;
        this.block = block;
    }

    public void execute() throws Exception {
        block.execute();
    }

    public String describe() {
        return condition;
    }
}
