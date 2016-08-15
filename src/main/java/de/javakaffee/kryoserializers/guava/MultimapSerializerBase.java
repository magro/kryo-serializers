package de.javakaffee.kryoserializers.guava;

import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.common.collect.Multimap;

public abstract class MultimapSerializerBase<K, V, T extends Multimap<K, V>> extends Serializer<T> {

    public MultimapSerializerBase(boolean acceptsNull, boolean immutable) {
        super(acceptsNull, immutable);
    }

    protected void writeMultimap(Kryo kryo, Output output, Multimap<K, V> multimap) {
        output.writeInt(multimap.size(), true);
        for (final Map.Entry<K, V> entry : multimap.entries()) {
            kryo.writeClassAndObject(output, entry.getKey());
            kryo.writeClassAndObject(output, entry.getValue());
        }
    }

    protected void readMultimap(Kryo kryo, Input input, Multimap<K, V> multimap) {
        final int size = input.readInt(true);
        for (int i = 0; i < size; ++i) {
            final K key = (K) kryo.readClassAndObject(input);
            final V value = (V) kryo.readClassAndObject(input);
            multimap.put(key, value);
        }
    }
}
