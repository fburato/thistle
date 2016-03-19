package thistle.core;

public class FinallyBlock extends DescribableBlock {
    public FinallyBlock(String condition, Block block) {
        super(condition, block);
    }

    public static FinallyBlock finallyBlock(String condition, Block block) {
        return new FinallyBlock(condition, block);
    }
}
