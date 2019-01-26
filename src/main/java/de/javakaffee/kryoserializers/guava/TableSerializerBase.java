package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Table;

import java.util.Map;


public abstract class TableSerializerBase<R, C, V, T extends Table<R, C, V>> extends Serializer<T> {

    public TableSerializerBase(boolean doesNotAcceptNull, boolean immutable) {
        super(doesNotAcceptNull, immutable);
    }

    public void writeTable(Kryo kryo, Output output, Table<R, C, V> table) {
        Map<R, Map<C, V>> rowMap = table.rowMap();
        output.writeInt(rowMap.size(), true);
        for (R r : rowMap.keySet()) {
            kryo.writeClassAndObject(output, r);
            Map<C, V> colMap = rowMap.get(r);
            output.writeInt(colMap.size(), true);
            for (C c : colMap.keySet()) {
                V v = colMap.get(c);
                kryo.writeClassAndObject(output, c);
                kryo.writeClassAndObject(output, v);
            }
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
        int rows = input.readInt(true);
        for (int i = 0; i < rows; i++) {
            R r = (R) kryo.readClassAndObject(input);
            int cols = input.readInt(true);
            for (int j = 0; j < cols; j++) {
                C c = (C) kryo.readClassAndObject(input);
                V v = (V) kryo.readClassAndObject(input);
                cellConsumer.accept(r, c, v);
            }
        }
    }

    interface CellConsumer<R, C, V> {
        void accept(R rowKey, C columnKey, V value);
    }
}
