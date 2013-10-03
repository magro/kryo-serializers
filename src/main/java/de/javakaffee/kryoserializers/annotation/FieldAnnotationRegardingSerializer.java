package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * A kryo {@link com.esotericsoftware.kryo.Serializer} that allows to exclusively include fields
 * that are attributed with a specific annotation. For example:
 * <pre>
 * {@code new Kryo().setDefaultSerializer(new FieldAnnotationRegardingSerializerFactory(Arrays.asList(CustomMark.class));}
 * </pre>
 * or equivalently
 * <pre>
 * {@code kryo = new Kryo() {
 *     protected Serializer newDefaultSerializer(Class type) {
 *       return new FieldAnnotationRegardingSerializer(this, type, Arrays.asList(CustomMark.class));
 *     }
 *   };
 * }
 * </pre>
 * would only include fields annotated with {@code CustomMark} to be serialized.
 * <p/>
 * <b>Important:</b> You need to name all fields that are to be included in serialization at the construction of the serializer.
 * Once a field is removed from serialization, it can never again be serialized by this instance. This is a tribute to the
 * behavior of the super class implementation {@link com.esotericsoftware.kryo.serializers.FieldSerializer}. If you need
 * fields again to be added to serialization, you have to create a new instance of this serializer.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public class FieldAnnotationRegardingSerializer<T> extends FieldAnnotationAwareSerializer<T> {

    public FieldAnnotationRegardingSerializer(Kryo kryo, Class<?> type, Collection<Class<? extends Annotation>> markedAnnotations) {
        super(kryo, type, markedAnnotations);
    }

    @Override
    protected boolean isInverted() {
        return false;
    }
}