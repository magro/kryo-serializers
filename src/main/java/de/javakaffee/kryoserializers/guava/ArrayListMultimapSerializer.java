package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.common.collect.ArrayListMultimap;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ArrayListMultimap}.
 * This does not yet support {@link Kryo#copy(java.lang.Object)}.
 */
public class ArrayListMultimapSerializer extends MultimapSerializerBase<Object, Object, ArrayListMultimap<Object, Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = false;

    private static final boolean IMMUTABLE = false;

    public ArrayListMultimapSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, ArrayListMultimap<Object, Object> multimap) {
        writeMultimap(kryo, output, multimap);
    }

    @Override
    public ArrayListMultimap<Object, Object> read(Kryo kryo, Input input, Class<ArrayListMultimap<Object, Object>> type) {
        final ArrayListMultimap<Object, Object> multimap = ArrayListMultimap.create();
        readMultimap(kryo, input, multimap);
        return multimap;
    }

    /**
     * Creates a new {@link ArrayListMultimapSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {
        final ArrayListMultimapSerializer serializer = new ArrayListMultimapSerializer();
        kryo.register(ArrayListMultimap.class, serializer);
    }
}
