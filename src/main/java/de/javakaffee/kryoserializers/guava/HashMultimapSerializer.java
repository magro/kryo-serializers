package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.common.collect.HashMultimap;

/**
 * A kryo {@link Serializer} for guava-libraries {@link HashMultimap}.
 * This does not yet support {@link Kryo#copy(java.lang.Object)}.
 */
public class HashMultimapSerializer extends MultimapSerializerBase<Object, Object, HashMultimap<Object, Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = false;

    private static final boolean IMMUTABLE = false;

    public HashMultimapSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, HashMultimap<Object, Object> multimap) {
        writeMultimap(kryo, output, multimap);
    }

    @Override
    public HashMultimap<Object, Object> read(Kryo kryo, Input input, Class<HashMultimap<Object, Object>> type) {
        final HashMultimap<Object, Object> multimap = HashMultimap.create();
        readMultimap(kryo, input, multimap);
        return multimap;
    }

    /**
     * Creates a new {@link HashMultimapSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {
        final HashMultimapSerializer serializer = new HashMultimapSerializer();
        kryo.register(HashMultimap.class, serializer);
    }
}
