package thistle.core;

import com.google.common.base.Optional;
import thistle.Then;

import java.lang.annotation.Annotation;

public class ThenBlock extends DescribableBlock {
    public final static DescribableBlockBuilder<ThenBlock> BUILDER = new DescribableBlockBuilder<ThenBlock>() {
        @Override
        public ThenBlock build(String description, Block block) {
            return thenBlock(description, block);
        }
    };
    public final static AnnotationExtractor<ThenBlock> ANNOTATION_EXTRACTOR = new AnnotationExtractor<ThenBlock>() {
        @Override
        public Optional<String> extract(Annotation annotation) {
            if (annotation instanceof Then) {
                return Optional.of(((Then) annotation).value());
            } else {
                return Optional.absent();
            }
        }
    };

    public ThenBlock(String condition, Block block) {
        super(condition, block);
    }

    public static ThenBlock thenBlock(String condition, Block block) {
        return new ThenBlock(condition, block);
    }
}
