package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import java.util.Comparator;
import java.util.Set;

/**
 * A kryo {@link Serializer} for guava-libraries {@link TreeBasedTable}.
 */
public class TreeBasedTableSerializer<R extends Comparable, C extends Comparable, V> extends TableSerializerBase<R, C, V, TreeBasedTable<R, C, V>> {

    private static final boolean HANDLES_NULL = false;
    private static final boolean IMMUTABLE = false;

    public TreeBasedTableSerializer() {
        super(HANDLES_NULL, IMMUTABLE);
    }

    @Override
    public void write(Kryo kryo, Output output, TreeBasedTable<R, C, V> table) {
        kryo.writeClassAndObject(output, table.rowComparator());
        kryo.writeClassAndObject(output, table.columnComparator());
        super.writeTable(kryo, output, table);
    }

    @Override
    public TreeBasedTable<R, C, V> read(Kryo kryo, Input input, Class<? extends TreeBasedTable<R, C, V>> type) {
        Comparator<? super Comparable> rowComparator = (Comparator<? super Comparable>) kryo.readClassAndObject(input);
        Comparator<? super Comparable> columnComparator = (Comparator<? super Comparable>) kryo.readClassAndObject(input);
        TreeBasedTable<R, C, V> table = TreeBasedTable.create(rowComparator, columnComparator);
        super.readTable(kryo, input, table);
        return table;
    }

    @Override
    public TreeBasedTable<R, C, V> copy(final Kryo kryo, final TreeBasedTable<R, C, V> original) {
        return TreeBasedTable.create(original);
    }

    /**
     * Creates a new {@link TreeBasedTableSerializer} and registers its serializer.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on
     */
    public static void registerSerializers(final Kryo kryo) {
        final TreeBasedTableSerializer serializer = new TreeBasedTableSerializer();
        kryo.register(TreeBasedTable.class, serializer);
    }
}
