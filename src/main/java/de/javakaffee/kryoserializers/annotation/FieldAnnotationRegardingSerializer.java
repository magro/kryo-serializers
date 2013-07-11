package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;

/**
 * A kryo {@link com.esotericsoftware.kryo.Serializer} that allows to exclusively include
 * ({@link de.javakaffee.kryoserializers.annotation.FieldAnnotationRegardingSerializer})
 * fields that are attributed with a specific annotation.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public class FieldAnnotationRegardingSerializer<T> extends FieldAnnotationAwareSerializer<T> {

    private static final Collection<Class<? extends Annotation>> markedAnnotations
            = new HashSet<Class<? extends Annotation>>();

    public static boolean markAnnotation(Class<? extends Annotation> annotationType) {
        return markedAnnotations.add(annotationType);
    }

    public static boolean unmarkAnnotation(Class<? extends Annotation> annotationType) {
        return markedAnnotations.remove(annotationType);
    }

    public FieldAnnotationRegardingSerializer(Kryo kryo, Class<?> type) {
        super(kryo, type);
    }

    @Override
    protected boolean isInverted() {
        return false;
    }

    @Override
    protected Collection<Class<? extends Annotation>> getMarkedAnnotations() {
        return markedAnnotations;
    }
}