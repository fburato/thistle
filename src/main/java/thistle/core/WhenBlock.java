package thistle.core;

import com.google.common.base.Optional;
import thistle.When;

import java.lang.annotation.Annotation;

public class WhenBlock extends DescribableBlock {
    public final static DescribableBlockBuilder<WhenBlock> BUILDER = new DescribableBlockBuilder<WhenBlock>() {
        @Override
        public WhenBlock build(String description, Block block) {
            return whenBlock(description, block);
        }
    };
    public final static AnnotationExtractor<WhenBlock> ANNOTATION_EXTRACTOR = new AnnotationExtractor<WhenBlock>() {
        @Override
        public Optional<String> extract(Annotation annotation) {
            if (annotation instanceof When) {
                return Optional.of(((When) annotation).value());
            } else {
                return Optional.absent();
            }
        }
    };

    public WhenBlock(String condition, Block block) {
        super(condition, block);
    }

    public static WhenBlock whenBlock(String condition, Block block) {
        return new WhenBlock(condition, block);
    }
}
