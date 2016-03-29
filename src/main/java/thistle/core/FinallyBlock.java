package thistle.core;

import com.google.common.base.Optional;
import thistle.Finally;

import java.lang.annotation.Annotation;

public class FinallyBlock extends DescribableBlock {
    public final static DescribableBlockBuilder<FinallyBlock> BUILDER = new DescribableBlockBuilder<FinallyBlock>() {
        @Override
        public FinallyBlock build(String description, Block block) {
            return finallyBlock(description, block);
        }
    };
    public final static AnnotationExtractor<FinallyBlock> ANNOTATION_EXTRACTOR = new AnnotationExtractor<FinallyBlock>() {
        @Override
        public Optional<String> extract(Annotation annotation) {
            if (annotation instanceof Finally) {
                return Optional.of(((Finally) annotation).value());
            } else {
                return Optional.absent();
            }
        }
    };

    public FinallyBlock(String condition, Block block) {
        super(condition, block);
    }

    public static FinallyBlock finallyBlock(String condition, Block block) {
        return new FinallyBlock(condition, block);
    }
}
