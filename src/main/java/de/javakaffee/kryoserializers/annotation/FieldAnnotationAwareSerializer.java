package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/**
 * A kryo {@link com.esotericsoftware.kryo.Serializer} that allows to ignore
 * ({@link de.javakaffee.kryoserializers.annotation.FieldAnnotationDisregardingSerializer})
 * or to exclusively include ({@link de.javakaffee.kryoserializers.annotation.FieldAnnotationRegardingSerializer})
 * fields that are attributed with a specific annotation.
 *
 * <p>This serializer can for example be used to serialize objects that carry non-serializable Spring injections. By
 * overriding the read object method, the injections can be reattached whenever an object is deserialized.</p>
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 */
public abstract class FieldAnnotationAwareSerializer<T> extends FieldSerializer<T> {

    public FieldAnnotationAwareSerializer(Kryo kryo, Class<?> type) {
        super(kryo, type);
    }

    protected void initializeCachedFields() {

        CachedField[] cachedFields = getFields();

        for (CachedField cachedField : cachedFields) {
            Field field = cachedField.getField();
            if (isInverted() ^ isMarked(field)) {
                if (TRACE) trace("kryo", String.format("Ignoring field %s tag: %s", isInverted() ? "without" : "with", cachedField));
                super.removeField(field.getName());
            }
        }
    }

    private boolean isMarked(Field field) {
        boolean markFound = false;
        for (Annotation annotation : field.getAnnotations()) {
            if (getMarkedAnnotations().contains(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    protected abstract boolean isInverted();

    protected abstract Collection<Class<? extends Annotation>> getMarkedAnnotations();

    public void removeField(String fieldName) {
        super.removeField(fieldName);
        initializeCachedFields();
    }
}