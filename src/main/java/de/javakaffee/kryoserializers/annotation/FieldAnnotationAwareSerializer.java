package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/**
 * A Kryo {@link com.esotericsoftware.kryo.Serializer} that allows to treat fields by custom annotations that
 * have to be specified by a user.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public abstract class FieldAnnotationAwareSerializer<T> extends FieldSerializer<T> {

    public FieldAnnotationAwareSerializer(Kryo kryo, Class<?> type) {
        super(kryo, type);
    }

    @Override
    protected void initializeCachedFields() {

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
    protected abstract Collection<Class<? extends Annotation>> getCurrentlyMarkedAnnotations();

    @Override
    public void removeField(String fieldName) {
        super.removeField(fieldName);
        initializeCachedFields();
    }

    /**
     * Removes fields from serialization that were not longer relevant. Be aware that no new fields
     * can be added to serialization once they were removed. This is the same behavior as observed with
     * the {@link FieldSerializer} class. If you need to add fields to serialization, you have to construct
     * a new instance of this serializer type.
     */
    public void refresh() {
        initializeCachedFields();
    }
}