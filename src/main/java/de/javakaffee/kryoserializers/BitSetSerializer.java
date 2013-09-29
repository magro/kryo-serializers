package de.javakaffee.kryoserializers;

import java.util.BitSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class BitSetSerializer extends Serializer<BitSet> {

    @Override
    public BitSet copy(final Kryo kryo, final BitSet original) {
        final BitSet result = new BitSet();
        final int length = original.length();
        for(int i = 0; i < length; i++) {
            result.set(i, original.get(i));
        }
        return result;
    }

    @Override
    public void write(final Kryo kryo, final Output output, final BitSet bitSet) {
        final int len = bitSet.length();

        output.writeInt(len, true);

        for(int i = 0; i < len; i++) {
            output.writeBoolean(bitSet.get(i));
        }
    }

    @Override
    public BitSet read(final Kryo kryo, final Input input, final Class<BitSet> bitSetClass) {
        final int len = input.readInt(true);
        final BitSet ret = new BitSet(len);

        for(int i = 0; i < len; i++) {
            ret.set(i, input.readBoolean());
        }

        return ret;
    }
}
