package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.HashBasedTable;

/**
 * A kryo {@link Serializer} for guava-libraries {@link HashBasedTable}.
 */
public class HashBasedTableSerializer<R, C, V> extends TableSerializerBase<R, C, V, HashBasedTable<R, C, V>> {

    private static final boolean HANDLES_NULL = false;
    private static final boolean IMMUTABLE = false;

    public HashBasedTableSerializer() {
        super(HANDLES_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, HashBasedTable<R, C, V> table) {
        super.writeTable(kryo, output, table);
    }

    @Override
    public HashBasedTable<R, C, V> read(Kryo kryo, Input input, Class<? extends HashBasedTable<R, C, V>> type) {
        HashBasedTable<R, C, V> table = HashBasedTable.create();
        super.readTable(kryo, input, table);
        return table;
    }

    @Override
    public HashBasedTable<R, C, V> copy(final Kryo kryo, final HashBasedTable<R, C, V> original) {
        return HashBasedTable.create(original);
    }

    /**
     * Creates a new {@link HashBasedTableSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {
        final HashBasedTableSerializer serializer = new HashBasedTableSerializer();
        kryo.register(HashBasedTable.class, serializer);
    }
}
