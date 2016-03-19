package thistle.core;

public class WhenBlock extends DescribableBlock {
    public WhenBlock(String condition, Block block) {
        super(condition, block);
    }

    public static WhenBlock whenBlock(String condition, Block block) {
        return new WhenBlock(condition, block);
    }
}
