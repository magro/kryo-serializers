package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableTable;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableTable}.
 */
public class ImmutableTableSerializer<R, C, V> extends TableSerializerBase<R, C, V, ImmutableTable<R, C, V>> {

    private static final boolean HANDLES_NULL = false;
    private static final boolean IMMUTABLE = true;

    public ImmutableTableSerializer() {
        super(HANDLES_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, ImmutableTable<R, C, V> immutableTable) {
        super.writeTable(kryo, output, immutableTable);
    }

    @Override
    public ImmutableTable<R, C, V> read(Kryo kryo, Input input, Class<? extends ImmutableTable<R, C, V>> type) {
        final ImmutableTable.Builder<R, C, V> builder = ImmutableTable.builder();
        super.readTable(kryo, input, new CellConsumer<R, C, V>() {
            @Override
            public void accept(R r, C c, V v) {
                builder.put(r, c, v);
            }
        });
        return builder.build();
    }

    /**
     * Creates a new {@link ImmutableTableSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {

        // ImmutableTable (abstract class)
        //  +- SparseImmutableTable
        //  |   SparseImmutableTable
        //  +- DenseImmutableTable
        //  |   Used when more than half of the cells have values
        //  +- SingletonImmutableTable
        //  |   Optimized for Table with only 1 element.

        final ImmutableTableSerializer serializer = new ImmutableTableSerializer();

        kryo.register(ImmutableTable.class, serializer); // ImmutableTable
        kryo.register(ImmutableTable.of().getClass(), serializer); // SparseImmutableTable
        kryo.register(ImmutableTable.of(1, 2, 3).getClass(), serializer);  // SingletonImmutableTable

        kryo.register(ImmutableTable.builder()
                .put("a", 1, 1)
                .put("b", 1, 1)
                .build().getClass(), serializer); // DenseImmutableTable

    }
}
