package thistle.core;

import com.google.common.base.Optional;

import java.lang.annotation.Annotation;

public interface AnnotationExtractor<T> {
    Optional<String> extract(Annotation annotation);
}
