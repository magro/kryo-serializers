package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/**
 * A Kryo {@link com.esotericsoftware.kryo.Serializer} that allows to configure the serialization of a class
 * by user-defined field annotations.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public abstract class FieldAnnotationAwareSerializer<T> extends FieldSerializer<T> {

    private final Collection<Class<? extends Annotation>> markedAnnotations;

    public FieldAnnotationAwareSerializer(Kryo kryo, Class<?> type, Collection<Class<? extends Annotation>> markedAnnotations) {
        super(kryo, type);
        // This implementation can be changed when rebuildCachedFields's visibility is changed to protected.
        this.markedAnnotations = new HashSet<Class<? extends Annotation>>(markedAnnotations);
        initializeCachedFields();
    }

    @Override
    protected void initializeCachedFields() {

        // Abort this procedure when called from the super constructor, see comment in constructor.
        if(markedAnnotations == null) {
            return;
        }

        CachedField[] cachedFields = getFields();

        for (CachedField cachedField : cachedFields) {
            Field field = cachedField.getField();
            if (isRemove(field)) {
                if (TRACE) {
                    trace("kryo", String.format("Ignoring field %s tag: %s", isInverted() ? "without" : "with", cachedField));
                }
                super.removeField(field.getName());
            }
        }
    }

    private boolean isRemove(Field field) {
        return !isMarked(field) ^ isInverted();
    }

    private boolean isMarked(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            if (getCurrentlyMarkedAnnotations().contains(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether annotated fields should be excluded from serialization.
     *
     * @return {@code true} if annotated fields should be excluded from serialization,
     *         {@code false} if only annotated fields should be included from serialization.
     */
    protected abstract boolean isInverted();

    /**
     * Returns a collection of annotations to be analyzed for serialization.
     * <p/>
     * <b>Important:</b> This method must never return {@code null}, even within the call
     * of the <i>super</i> constructor of the {@link FieldSerializer} class. Overriding classes will
     * therefore most likely host a static collection of such annotation types. See the
     * {@link FieldAnnotationRegardingSerializer} and {@link FieldAnnotationDisregardingSerializer}
     * classes for examples of such implementations.
     *
     * @return A collection containing all relevant annotations.
     */
    protected Collection<Class<? extends Annotation>> getCurrentlyMarkedAnnotations() {
        return markedAnnotations;
    }

    /**
     * Marks relevant to all instances of this serializer type.
     *
     * @return A set representing the relevant marks.
     */
    public Collection<Class<? extends Annotation>> getMarkedAnnotations() {
        return Collections.unmodifiableCollection(markedAnnotations);
    }

    @Override
    public void removeField(String fieldName) {
        super.removeField(fieldName);
        initializeCachedFields();
    }
}