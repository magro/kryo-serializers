package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ArrayTable}.
 */
public class ArrayTableSerializer<R, C, V> extends TableSerializerBase<R, C, V, ArrayTable<R, C, V>> {

    private static final boolean HANDLES_NULL = false;
    private static final boolean IMMUTABLE = false;

    public ArrayTableSerializer() {
        super(HANDLES_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, ArrayTable<R, C, V> table) {
        List<R> rowKeys = table.rowKeyList();
        List<C> columnKeys = table.columnKeyList();
        kryo.writeClassAndObject(output, rowKeys);
        kryo.writeClassAndObject(output, columnKeys);
        for (R rowKey : rowKeys) {
            for (C columnKey : columnKeys) {
                V val = table.get(rowKey, columnKey);
                kryo.writeClassAndObject(output, val);
            }
        }
    }

    @Override
    public ArrayTable<R, C, V> read(Kryo kryo, Input input, Class<? extends ArrayTable<R, C, V>> type) {
        List<R> rowKeys = (List<R>) kryo.readClassAndObject(input);
        List<C> columnKeys = (List<C>) kryo.readClassAndObject(input);
        ArrayTable<R, C, V> table = ArrayTable.create(rowKeys, columnKeys);
        for (R rowKey : rowKeys) {
            for (C columnKey : columnKeys) {
                V val = (V) kryo.readClassAndObject(input);
                table.put(rowKey, columnKey, val);
            }
        }
        return table;
    }

    @Override
    public ArrayTable<R, C, V> copy(final Kryo kryo, final ArrayTable<R, C, V> original) {
        return ArrayTable.create(original);
    }

    /* kryo.getSerializer (invoking kryo.getRegistration) throws an exception if registration is required,
     * therefore we're getting a potentially existing serializer on another way.
     */
    private static Serializer<?> getSerializer(Kryo kryo, Class<?> type) {
        Registration registration = kryo.getClassResolver().getRegistration(type);
        return registration != null ? registration.getSerializer() : null;
    }

    /**
     * Creates a new {@link ArrayTableSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {

        // ImmutableList is used by ArrayTable. However,
        // we already have a separate serializer class for ImmutableList,
        // ImmutableListSerializer. If it is not already being used, register it.
        Serializer immutableListSerializer = getSerializer(kryo, ImmutableList.class);
        if (!(immutableListSerializer instanceof ImmutableListSerializer)) {
            ImmutableListSerializer.registerSerializers(kryo);
        }

        final ArrayTableSerializer serializer = new ArrayTableSerializer();
        kryo.register(ArrayTable.class, serializer);
    }
}
