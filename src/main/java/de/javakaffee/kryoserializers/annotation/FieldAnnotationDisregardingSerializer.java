package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;

/**
 * A kryo {@link com.esotericsoftware.kryo.Serializer} that allows to exclude fields
 * that are attributed with a specific annotation. A typical use case would be serialization in bean container
 * contexts. For example:
 * <pre>
 * {@code new Kryo().setDefaultSerializer(new FieldAnnotationDisregardingSerializerFactory(Arrays.asList(CustomMark.class));}
 * </pre>
 * or equivalently
 * <pre>
 * {@code kryo = new Kryo() {
 *     protected Serializer newDefaultSerializer(Class type) {
 *       return new FieldAnnotationDisregardingSerializer(this, type, Arrays.asList(CustomMark.class));
 *     }
 *   };
 * }
 * </pre>
 * would prevent any field annotated with {@code CustomMark} to be serialized.
 * <p/>
 * <b>Important:</b> Once an annotation was added to a serializer, annotated fields can never again be subject of
 * serialization by this instance. This is a tribute to the behavior of the super class implementation
 * {@link com.esotericsoftware.kryo.serializers.FieldSerializer}. If you need fields to be contained in serialization
 * that were formerly excluded, you have to create a new instance of this serializer.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public class FieldAnnotationDisregardingSerializer<T> extends FieldAnnotationAwareSerializer<T> {

    public FieldAnnotationDisregardingSerializer(Kryo kryo, Class<?> type) {
        super(kryo, type, new HashSet<Class<? extends Annotation>>());
    }

    public FieldAnnotationDisregardingSerializer(Kryo kryo, Class<?> type, Collection<Class<? extends Annotation>> markedAnnotations) {
        super(kryo, type, markedAnnotations);
    }

    @Override
    protected boolean isInverted() {
        return true;
    }

    /**
     * Removes fields annotated by the argument annotation permanently from serialization.
     *
     * @param annotationType The annotation type to not consider any longer.
     */
    public void addAnnotation(Class<? extends Annotation> annotationType) {
        if (getCurrentlyMarkedAnnotations().add(annotationType)) {
            initializeCachedFields();
        }
    }
}
