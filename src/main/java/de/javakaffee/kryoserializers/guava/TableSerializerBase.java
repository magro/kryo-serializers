package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Table;

import java.util.Set;


public abstract class TableSerializerBase<R, C, V, T extends Table<R, C, V>> extends Serializer<T> {

    public TableSerializerBase(boolean doesNotAcceptNull, boolean immutable) {
        super(doesNotAcceptNull, immutable);
    }

    public void writeTable(Kryo kryo, Output output, Table<R, C, V> table) {
        Set<Table.Cell<R, C, V>> cells = table.cellSet();
        output.writeInt(cells.size(), true);
        for (Table.Cell<R, C, V> cell : cells) {
            kryo.writeClassAndObject(output, cell.getRowKey());
            kryo.writeClassAndObject(output, cell.getColumnKey());
            kryo.writeClassAndObject(output, cell.getValue());
        }
    }

    public void readTable(Kryo kryo, Input input, final Table<R, C, V> table) {
        this.readTable(kryo, input, new CellConsumer<R, C, V>() {
            @Override
            public void accept(R r, C c, V v) {
                table.put(r, c, v);
            }
        });
    }

    public void readTable(Kryo kryo, Input input, CellConsumer<R, C, V> cellConsumer) {
        final int size = input.readInt(true);
        for (int i = 0; i < size; ++i) {
            R rowKey = (R) kryo.readClassAndObject(input);
            C colKey = (C) kryo.readClassAndObject(input);
            V value = (V) kryo.readClassAndObject(input);
            cellConsumer.accept(rowKey, colKey, value);
        }
    }

    interface CellConsumer<R, C, V> {
        void accept(R rowKey, C columnKey, V value);
    }
}
