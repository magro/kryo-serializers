package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A kryo {@link com.esotericsoftware.kryo.Serializer} that allows to exclusively include fields
 * that are attributed with a specific annotation. For example calling
 * {@code FieldAnnotationRegardingSerializer.getMarks().add(NonTransient.class)}
 * with {@code NonTransient} being an inversion to some persistence framework's {@code Transient} class, would allow
 * a serialization of only fields that are annotated with this custom annotation.
 * <p/>
 * <b>Important:</b> The relevant annotations must be contained in the collection retreivable via {@code getMarks} before
 * the instance is created. If some fields should not longer be serialized after the construction, remove such
 * annotations from the collection and call {@code refresh}. No fields can be added to the serializer once they
 * are removed. This is a tribute to the behavior of the super class implementation
 * {@link com.esotericsoftware.kryo.serializers.FieldSerializer}. If you need new fields to be contained you
 * have to create a new instance of this serializer.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public class FieldAnnotationRegardingSerializer<T> extends FieldAnnotationAwareSerializer<T> {

    private static final Set<Class<? extends Annotation>> MARKED
            = new HashSet<Class<? extends Annotation>>();

    public static Set<Class<? extends Annotation>> getMarks() {
        return MARKED;
    }

    public FieldAnnotationRegardingSerializer(Kryo kryo, Class<?> type) {
        super(kryo, type);
    }

    @Override
    protected boolean isInverted() {
        return false;
    }

    @Override
    protected Collection<Class<? extends Annotation>> getCurrentlyMarkedAnnotations() {
        return MARKED;
    }
}