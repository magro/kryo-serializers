package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ArrayTable;
import de.javakaffee.kryoserializers.KryoTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

public class ArrayTableSerializerTest extends TableSerializerTestBase {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
        ArrayTableSerializer.registerSerializers(_kryo);
    }

    @Test(dataProvider = "Google Guava tables (non empty)")
    public void testTable(Object[] contents) {
        Set rowKeys = new LinkedHashSet();
        Set colKeys = new LinkedHashSet();
        for (int index = 0; index < contents.length; ) {
            rowKeys.add(contents[index++]);
            colKeys.add(contents[index++]);
            index++; // skip value
        }

        final ArrayTable<Object, Object, Object> table = ArrayTable.create(rowKeys, colKeys);
        populateTable(table, contents);
        final byte[] serialized = KryoTest.serialize(_kryo, table);
        final ArrayTable<Object, Object, Object> deserialized = KryoTest.deserialize(_kryo, serialized, ArrayTable.class);
        assertEquals(deserialized, table);
    }

    @Test(dataProvider = "Google Guava tables (non empty)")
    public void testTableCopy(Object[] contents) {
        Set rowKeys = new LinkedHashSet();
        Set colKeys = new LinkedHashSet();
        for (int index = 0; index < contents.length; ) {
            rowKeys.add(contents[index++]);
            colKeys.add(contents[index++]);
            index++; // skip value
        }

        final ArrayTable<Object, Object, Object> table = ArrayTable.create(rowKeys, colKeys);
        populateTable(table, contents);
        ArrayTable<Object, Object, Object> copy = _kryo.copy(table);

        assertNotSame(copy, table);
        assertEquals(copy, table);
    }

}