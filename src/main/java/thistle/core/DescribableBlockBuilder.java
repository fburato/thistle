package thistle.core;

public interface DescribableBlockBuilder<T> {
    T build(String description, Block block);
}
