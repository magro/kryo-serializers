package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;

import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableSortedMap}.
 */
public class ImmutableSortedMapSerializer extends Serializer<ImmutableSortedMap<Object, ? extends Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = true;
    private static final boolean IMMUTABLE = true;

    public ImmutableSortedMapSerializer() {
        super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, ImmutableSortedMap<Object, ? extends Object> immutableMap) {
        kryo.writeObject(output, Maps.newTreeMap(immutableMap));
    }

    @Override
    public ImmutableSortedMap<Object, Object> read(Kryo kryo, Input input, Class<? extends ImmutableSortedMap<Object, ? extends Object>> type) {
        Map map = kryo.readObject(input, TreeMap.class);
        return ImmutableSortedMap.copyOf(map);
    }

    /**
     * Creates a new {@link ImmutableSortedMapSerializer} and registers its serializer
     * for the several ImmutableMap related classes.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {

        // we're writing a TreeMap, therefore we should register it
        kryo.register(java.util.TreeMap.class);

        final ImmutableSortedMapSerializer serializer = new ImmutableSortedMapSerializer();

        kryo.register(ImmutableSortedMap.class, serializer);
        kryo.register(ImmutableSortedMap.of().getClass(), serializer);

        final Comparable<Object> k1 = new Comparable<Object>() {
            @Override
            public int compareTo(Object o) {
                return o == this ? 0 : -1;
            }
        };
        final Comparable<Object> k2 = new Comparable<Object>() {
            @Override
            public int compareTo(Object o) {
                return o == this ? 0 : 1;
            }
        };
        final Object v1 = new Object();
        final Object v2 = new Object();

        kryo.register(ImmutableSortedMap.of(k1, v1).getClass(), serializer);
        kryo.register(ImmutableSortedMap.of(k1, v1, k2, v2).getClass(), serializer);

        Map<DummyEnum,Object> enumMap = new EnumMap<DummyEnum, Object>(DummyEnum.class);
        for (DummyEnum e : DummyEnum.values()) {
            enumMap.put(e, v1);
        }

        kryo.register(ImmutableSortedMap.copyOf(enumMap).getClass(), serializer);
    }

    private enum DummyEnum {
        VALUE1,
        VALUE2
    }
}
