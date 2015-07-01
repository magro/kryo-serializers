package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableMultimap}.
 */
public class ImmutableMultimapSerializer extends Serializer<ImmutableMultimap<Object, Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = true;
    private static final boolean IMMUTABLE = true;

    public ImmutableMultimapSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, ImmutableMultimap<Object, Object> immutableMultiMap) {
        kryo.writeObject(output, ImmutableMap.copyOf(immutableMultiMap.asMap()));
    }

    @Override
    public ImmutableMultimap<Object, Object> read(Kryo kryo, Input input, Class<ImmutableMultimap<Object, Object>> type) {
        Map map = kryo.readObject(input, ImmutableMap.class);

        Set<Map.Entry<Object, List<? extends Object>>> entries = map.entrySet();
        ImmutableMultimap.Builder<Object, Object> builder = ImmutableMultimap.builder();
        for (Map.Entry<Object, List<? extends Object>> entry : entries) {
            builder.putAll(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    /**
     * Creates a new {@link ImmutableMultimapSerializer} and registers its serializer
     * for the several ImmutableMultimap related classes.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {

        Serializer immutableListSerializer = kryo.getSerializer(ImmutableList.class);
        if (!(immutableListSerializer instanceof ImmutableListSerializer)) {
            ImmutableListSerializer.registerSerializers(kryo);
        }

        Serializer immutableMapSerializer = kryo.getSerializer(ImmutableMap.class);
        if (!(immutableMapSerializer instanceof ImmutableMapSerializer)) {
            ImmutableMapSerializer.registerSerializers(kryo);
        }

        final ImmutableMultimapSerializer serializer = new ImmutableMultimapSerializer();

        kryo.register(ImmutableMultimap.class, serializer);
        kryo.register(ImmutableMultimap.of().getClass(), serializer);
        Object o = new Object();

        kryo.register(ImmutableMultimap.of(o, o).getClass(), serializer);

    }
}
