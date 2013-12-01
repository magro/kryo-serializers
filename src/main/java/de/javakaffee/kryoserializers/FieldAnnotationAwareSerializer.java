package de.javakaffee.kryoserializers;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.factories.SerializerFactory;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

/**
 * A kryo {@link FieldSerializer} that allows to exclusively include or exclude fields that
 * are attributed with user-specific annotations. This can be for example useful when serializing beans that carry
 * references to a dependency injection framework. As an example for Spring:
 * <p/>
 * <pre>
 * {@code
 * Set<Class<? extends Annotation>> marks = new HashSet<>();
 * marks.add(Autowired.class);
 * SerializerFactory disregardingFactory = new FieldAnnotationAwareSerializer.Factory(marks, true);
 * Kryo kryo = new Kryo();
 * kryo.setDefaultSerializer(factory);
 * }
 * </pre>
 * <p/>
 * The resulting {@link Kryo} instance would ignore all fields that are annotated with Spring's {@code @Autowired}
 * annotation.
 * <p/>
 * Similarly, it is possible to created a serializer which does the opposite such that the resulting serializer
 * would only serialize fields that are annotated with the specified annotations.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class FieldAnnotationAwareSerializer<T> extends FieldSerializer<T> {

    /**
     * A factory for creating instances of {@link FieldAnnotationAwareSerializer}.
     */
    public static class Factory implements SerializerFactory {

        private final Collection<Class<? extends Annotation>> marked;
        private final boolean disregarding;

        /**
         * Creates a new factory. See {@link FieldAnnotationAwareSerializer#FieldAnnotationAwareSerializer(
         *com.esotericsoftware.kryo.Kryo, Class, java.util.Collection, boolean)}
         * for additional information on the constructor parameters.
         *
         * @param marked       The annotations that will be considered of the resulting converter.
         * @param disregarding If {@code true}, the serializer will ignore all annotated fields,
         *                     if set to {@code false} it will exclusively look at annotated fields.
         */
        public Factory(final Collection<Class<? extends Annotation>> marked, final boolean disregarding) {
            this.marked = marked;
            this.disregarding = disregarding;
        }

        @Override
        public Serializer<?> makeSerializer(final Kryo kryo, final Class<?> type) {
            return new FieldAnnotationAwareSerializer<Object>(kryo, type, marked, disregarding);
        }
    }

    private final Set<Class<? extends Annotation>> marked;

    /**
     * Determines whether annotated fields should be excluded from serialization.
     * <p/>
     * {@code true} if annotated fields should be excluded from serialization,
     * {@code false} if only annotated fields should be included from serialization.
     */
    private final boolean disregarding;

    /**
     * Creates a new field annotation aware serializer.
     *
     * @param kryo         The {@link Kryo} instace.
     * @param type         The type of the class being serialized.
     * @param marked       The annotations this serializer considers for its serialization process. Be aware tha
     *                     a serializer with {@code disregarding} set to {@code false} will never be able to
     *                     serialize fields that are not annotated with any of these annotations since it is not
     *                     possible to add fields to a {@link FieldSerializer} once it is created. See the
     *                     documentation to {@link FieldAnnotationAwareSerializer#addAnnotation(Class)} and
     *                     {@link FieldAnnotationAwareSerializer#removeAnnotation(Class)} for further information.
     * @param disregarding If {@code true}, the serializer will ignore all annotated fields,
     *                     if set to {@code false} it will exclusively look at annotated fields.
     */
    public FieldAnnotationAwareSerializer(final Kryo kryo,
                                          final Class<?> type,
                                          final Collection<Class<? extends Annotation>> marked,
                                          final boolean disregarding) {
        super(kryo, type);
        this.disregarding = disregarding;
        this.marked = new HashSet<Class<? extends Annotation>>(marked);
        rebuildCachedFields();
    }

    @Override
    protected void rebuildCachedFields() {
        // In order to avoid rebuilding the cached fields twice, the super constructor's call
        // to this method will be suppressed. This can be done by a simple check of the initialization
        // state of a property of this subclass.
        if (marked == null) {
            return;
        }
        super.rebuildCachedFields();
        removeFields();
    }

    private void removeFields() {
        final CachedField<?>[] cachedFields = getFields();
        for (final CachedField<?> cachedField : cachedFields) {
            final Field field = cachedField.getField();
            if (isRemove(field)) {
                if (TRACE) {
                    trace("kryo", String.format("Ignoring field %s tag: %s", disregarding ? "without" : "with", cachedField));
                }
                super.removeField(field.getName());
            }
        }
    }

    private boolean isRemove(final Field field) {
        return !isMarked(field) ^ disregarding;
    }

    private boolean isMarked(final Field field) {
        for (final Annotation annotation : field.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (marked.contains(annotationType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an annotation to the annotations that are considered by this serializer.
     * <p/>
     * <b>Important</b>: This will not have an effect if the serializer was configured
     * to exclusively serialize annotated fields by setting {@code disregarding} to
     * {@code false}. This is similar to the contract of this serializer's superclass
     * {@link FieldSerializer} which does not allow to add fields that were formerly
     * removed. If this was possible, instances that were serialized before this field
     * was added could not longer be properly deserialized. In order to make this contract
     * break explicit, you need to create a new instance of this serializer if you want to
     * include new fields to a serializer that exclusively serializes annotated fields.
     *
     * @param clazz The annotation class to be added.
     * @return {@code true} if the method call had an effect.
     */
    public boolean addAnnotation(final Class<? extends Annotation> clazz) {
        if (disregarding && marked.add(clazz)) {
            initializeCachedFields();
            return true;
        }
        return false;
    }

    /**
     * Removes an annotation to the annotations that are considered by this serializer.
     * <p/>
     * <b>Important</b>: This will not have an effect if the serializer was configured
     * to not serialize annotated fields by setting {@code disregarding} to
     * {@code true}. This is similar to the contract of this serializer's superclass
     * {@link FieldSerializer} which does not allow to add fields that were formerly
     * removed. If this was possible, instances that were serialized before this field
     * was added could not longer be properly deserialized. In order to make this contract
     * break explicit, you need to create a new instance of this serializer if you want to
     * include new fields to a serializer that ignores annotated fields for serialization.
     *
     * @param clazz The annotation class to be removed.
     * @return {@code true} if the method call had an effect.
     */
    public boolean removeAnnotation(final Class<? extends Annotation> clazz) {
        if (!disregarding && marked.remove(clazz)) {
            initializeCachedFields();
            return true;
        }
        return false;
    }
}