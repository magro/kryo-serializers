package de.javakaffee.kryoserializers;

import java.util.BitSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class BitSetSerializer extends Serializer<BitSet> {

    @Override
    public BitSet copy(final Kryo kryo, final BitSet original) {
        return BitSet.valueOf(original.toLongArray());
    }

    @Override
    public void write(final Kryo kryo, final Output output, final BitSet bitSet) {
        final long[] longs = bitSet.toLongArray();
        output.writeInt(longs.length, true);
        output.writeLongs(longs);
    }

    @Override
    public BitSet read(final Kryo kryo, final Input input, final Class<? extends BitSet> bitSetClass) {
        final int len = input.readInt(true);
        return BitSet.valueOf(input.readLongs(len));
    }
}
