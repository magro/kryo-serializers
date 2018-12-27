package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

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
    public ImmutableMultimap<Object, Object> read(Kryo kryo, Input input, Class<? extends ImmutableMultimap<Object, Object>> type) {
        final ImmutableMultimap.Builder builder;
        if (type.equals (ImmutableListMultimap.class)) {
            builder = ImmutableMultimap.builder();
        }
        else if (type.equals (ImmutableSetMultimap.class)) {
            builder = ImmutableSetMultimap.builder();
        }
        else {
            builder = ImmutableMultimap.builder();
        }

        final Map map = kryo.readObject(input, ImmutableMap.class);
        final Set<Map.Entry<Object, List<? extends Object>>> entries = map.entrySet();

        for (Map.Entry<Object, List<? extends Object>> entry : entries) {
            builder.putAll(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    /* kryo.getSerializer (invoking kryo.getRegistration) throws an exception if registration is required,
     * therefore we're getting a potentially existing serializer on another way.
     */
    private static Serializer<?> getSerializer(Kryo kryo, Class<?> type) {
        Registration registration = kryo.getClassResolver().getRegistration(type);
        return registration != null ? registration.getSerializer() : null;
    }

    /**
     * Creates a new {@link ImmutableMultimapSerializer} and registers its serializer
     * for the several ImmutableMultimap related classes.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {

        // ImmutableMap is used by ImmutableMultimap. However,
        // we already have a separate serializer class for ImmutableMap,
        // ImmutableMapSerializer. If it is not already being used, register it.
        Serializer immutableMapSerializer = getSerializer(kryo, ImmutableMap.class);
        if (!(immutableMapSerializer instanceof ImmutableMapSerializer)) {
            ImmutableMapSerializer.registerSerializers(kryo);
        }

        // ImmutableList is used by ImmutableListMultimap. However,
        // we already have a separate serializer class for ImmutableList,
        // ImmutableListSerializer. If it is not already being used, register it.
        Serializer immutableListSerializer = getSerializer(kryo, ImmutableList.class);
        if (!(immutableListSerializer instanceof ImmutableListSerializer)) {
            ImmutableListSerializer.registerSerializers(kryo);
        }

        // ImmutableSet is used by ImmutableSetMultimap. However,
        // we already have a separate serializer class for ImmutableSet,
        // ImmutableSetSerializer. If it is not already being used, register it.
        Serializer immutableSetSerializer = getSerializer(kryo, ImmutableSet.class);
        if (!(immutableSetSerializer instanceof ImmutableSetSerializer)) {
            ImmutableSetSerializer.registerSerializers(kryo);
        }

        final ImmutableMultimapSerializer serializer = new ImmutableMultimapSerializer();

        // ImmutableMultimap (abstract class)
        //  +- EmptyImmutableListMultimap
        //  +- ImmutableListMultimap
        //  +- EmptyImmutableSetMultimap
        //  +- ImmutableSetMultimap

        kryo.register(ImmutableMultimap.class, serializer);
        kryo.register(ImmutableListMultimap.of().getClass(), serializer);
        kryo.register(ImmutableListMultimap.of("A", "B").getClass(), serializer);
        kryo.register(ImmutableSetMultimap.of().getClass(), serializer);
        kryo.register(ImmutableSetMultimap.of("A", "B").getClass(), serializer);
    }
}
