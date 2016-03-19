package thistle.core;

public class ThenBlock extends DescribableBlock {
    public ThenBlock(String condition, Block block) {
        super(condition, block);
    }

    public static ThenBlock thenBlock(String condition, Block block){
        return new ThenBlock(condition, block);
    }
}
