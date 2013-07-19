package de.javakaffee.kryoserializers.annotation;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

/**
 * A Kryo {@link com.esotericsoftware.kryo.Serializer} that allows to treat fields by custom annotations that
 * have to be specified by a user.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 * 
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
 */
public class FieldAnnotationAwareSerializer<T> extends FieldSerializer<T> {

    /**
     * A collection of annotations to be analyzed for serialization.
     * <p/>
     * <b>Important:</b> This method must never return {@code null}, even within the call
     * of the <i>super</i> constructor of the {@link FieldSerializer} class. Overriding classes will
     * therefore most likely host a static collection of such annotation types. See the
     * {@link FieldAnnotationRegardingSerializer} and {@link FieldAnnotationDisregardingSerializer}
     * classes for examples of such implementations.
     */
    private Set<Class<? extends Annotation>> marked;

    /**
     * Determines whether annotated fields should be excluded from serialization.
     *
     * {@code true} if annotated fields should be excluded from serialization,
     * {@code false} if only annotated fields should be included from serialization.
     */
	private final boolean inverted;

    public FieldAnnotationAwareSerializer(final Kryo kryo, final Class<?> type, final boolean inverted) {
        super(kryo, type);
        this.inverted = inverted;
    }
    
    @Override
    protected void rebuildCachedFields() {
    	
    	// Called from rebuildCachedFields which is invoked by super constructor.
    	if(marked == null) {
    		marked = new HashSet<Class<? extends Annotation>>();
    	}
    	
    	super.rebuildCachedFields();
    }

    @Override
    protected void initializeCachedFields() {

		final CachedField<?>[] cachedFields = getFields();

        for (final CachedField<?> cachedField : cachedFields) {
            final Field field = cachedField.getField();
            if (isRemove(field)) {
                if (TRACE) {
                    trace("kryo", String.format("Ignoring field %s tag: %s", inverted ? "without" : "with", cachedField));
                }
                super.removeField(field.getName());
            }
        }
    }

    private boolean isRemove(final Field field) {
        return !isMarked(field) ^ inverted;
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

    @Override
    public void removeField(final String fieldName) {
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

	public void addAnnotation(final Class<? extends Annotation> clazz) {
		marked.add(clazz);
		rebuildCachedFields();
	}

}