package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A kryo {@link com.esotericsoftware.kryo.Serializer} that allows to exclude fields
 * that are attributed with a specific annotation. A typical use case would be serialization in bean container
 * contexts. For example, calling
 * {@code FieldAnnotationDisregardingSerializer.getMarks().add(Autowired.class)}
 * one could avoid that fields that carry Spring beans would get serialized.
 * <p/>
 * <b>Important:</b> Once an annotation added to {@code getMarks} caused the discount of a field, it can never again be
 * serialized by this instance. Annotated fields are deleted during the construction of the object and whenever {@code refresh}
 * is called. This is a tribute to the behavior of the super class implementation
 * {@link com.esotericsoftware.kryo.serializers.FieldSerializer}. If you need new fields to be contained you
 * have to create a new instance of this serializer.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public class FieldAnnotationDisregardingSerializer<T> extends FieldAnnotationAwareSerializer<T> {

    private static final Set<Class<? extends Annotation>> MARKED
            = new HashSet<Class<? extends Annotation>>();

    public static Set<Class<? extends Annotation>> getMarks() {
        return MARKED;
    }

    public FieldAnnotationDisregardingSerializer(Kryo kryo, Class<?> type) {
        super(kryo, type);
    }

    @Override
    protected boolean isInverted() {
        return true;
    }

    @Override
    protected Collection<Class<? extends Annotation>> getCurrentlyMarkedAnnotations() {
        return MARKED;
    }
}
