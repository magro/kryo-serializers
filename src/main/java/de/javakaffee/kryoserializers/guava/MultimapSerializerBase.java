package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.Map;

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

    protected abstract Multimap createCopy(Kryo kryo, Multimap original);

    @Override
    public Multimap copy(final Kryo kryo, final Multimap original) {
        Multimap copy = createCopy(kryo, original);
        Iterator<Map.Entry> iter = original.entries().iterator();

        while(iter.hasNext()) {
            Map.Entry entry = iter.next();
            copy.put(kryo.copy(entry.getKey()), kryo.copy(entry.getValue()));
        }

        return copy;
    }
}
