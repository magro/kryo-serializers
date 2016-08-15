package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.common.collect.LinkedHashMultimap;

/**
 * A kryo {@link Serializer} for guava-libraries {@link LinkedHashMultimap}.
 * This does not yet support {@link Kryo#copy(java.lang.Object)}.
 */
public class LinkedHashMultimapSerializer extends MultimapSerializerBase<Object, Object, LinkedHashMultimap<Object, Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = false;

    private static final boolean IMMUTABLE = false;

    public LinkedHashMultimapSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, LinkedHashMultimap<Object, Object> multimap) {
        writeMultimap(kryo, output, multimap);
    }

    @Override
    public LinkedHashMultimap<Object, Object> read(Kryo kryo, Input input, Class<LinkedHashMultimap<Object, Object>> type) {
        final LinkedHashMultimap<Object, Object> multimap = LinkedHashMultimap.create();
        readMultimap(kryo, input, multimap);
        return multimap;
    }

    /**
     * Creates a new {@link LinkedHashMultimapSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {
        final LinkedHashMultimapSerializer serializer = new LinkedHashMultimapSerializer();
        kryo.register(LinkedHashMultimap.class, serializer);
    }
}
