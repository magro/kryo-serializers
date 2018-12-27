package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * A kryo {@link Serializer} for guava-libraries {@link LinkedListMultimap}.
 */
public class LinkedListMultimapSerializer extends MultimapSerializerBase<Object, Object, LinkedListMultimap<Object, Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = false;

    private static final boolean IMMUTABLE = false;

    public LinkedListMultimapSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, LinkedListMultimap<Object, Object> multimap) {
        writeMultimap(kryo, output, multimap);
    }

    @Override
    public LinkedListMultimap<Object, Object> read(Kryo kryo, Input input, Class<? extends LinkedListMultimap<Object, Object>> type) {
        final LinkedListMultimap<Object, Object> multimap = LinkedListMultimap.create();
        readMultimap(kryo, input, multimap);
        return multimap;
    }

    @Override
    protected Multimap createCopy(Kryo kryo, Multimap original) {
        return LinkedListMultimap.create(original.keySet().size());
    }

    /**
     * Creates a new {@link LinkedListMultimapSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {
        final LinkedListMultimapSerializer serializer = new LinkedListMultimapSerializer();
        kryo.register(LinkedListMultimap.class, serializer);
    }
}
