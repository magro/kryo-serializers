package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.factories.SerializerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * A {@link SerializerFactory} for creating instances of {@link FieldAnnotationDisregardingSerializer} instances.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public class FieldAnnotationDisregardingSerializerFactory implements SerializerFactory {

    private final Collection<Class<? extends Annotation>> markedAnnotations;

    public FieldAnnotationDisregardingSerializerFactory(Collection<Class<? extends Annotation>> markedAnnotations) {
        this.markedAnnotations = markedAnnotations;
    }

    /**
     * Returns the collection of marked annotation that is handed over to every {@link FieldAnnotationRegardingSerializer}
     * that is created by this factory.
     *
     * @return The collection of marked annotations used by this factory.
     */
    public Collection<Class<? extends Annotation>> getMarkedAnnotations() {
        return markedAnnotations;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Serializer makeSerializer(Kryo kryo, Class<?> aClass) {
        return new FieldAnnotationDisregardingSerializer(kryo, aClass, markedAnnotations);
    }
}
