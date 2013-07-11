package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;

/**
 * A kryo {@link com.esotericsoftware.kryo.Serializer} that allows to ignore
 * ({@link de.javakaffee.kryoserializers.annotation.FieldAnnotationDisregardingSerializer})
 * fields that are attributed with a specific annotation.
 *
 * <p>This serializer can for example be used to serialize objects that carry non-serializable Spring injections. By
 * overriding the read object method, the injections can be reattached whenever an object is deserialized.</p>
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public class FieldAnnotationDisregardingSerializer<T> extends FieldAnnotationAwareSerializer<T> {

    private static final Collection<Class<? extends Annotation>> markedAnnotations
            = new HashSet<Class<? extends Annotation>>();

    public static boolean markAnnotation(Class<? extends Annotation> annotationType) {
        return markedAnnotations.add(annotationType);
    }

    public static boolean unmarkAnnotation(Class<? extends Annotation> annotationType) {
        return markedAnnotations.remove(annotationType);
    }

    public FieldAnnotationDisregardingSerializer(Kryo kryo, Class<?> type) {
        super(kryo, type);
    }

    @Override
    protected boolean isInverted() {
        return true;
    }

    @Override
    protected Collection<Class<? extends Annotation>> getMarkedAnnotations() {
        return markedAnnotations;
    }
}
