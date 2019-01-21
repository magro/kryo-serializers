package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.HashBasedTable;
import de.javakaffee.kryoserializers.KryoTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

public class HashBasedTableSerializerTest extends TableSerializerTestBase {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
        HashBasedTableSerializer.registerSerializers(_kryo);
    }

    @Test(dataProvider = "Google Guava tables")
    public void testTable(Object[] contents) {
        final HashBasedTable<Object, Object, Object> table = HashBasedTable.create();
        populateTable(table, contents);
        final byte[] serialized = KryoTest.serialize(_kryo, table);
        final HashBasedTable<Object, Object, Object> deserialized = KryoTest.deserialize(_kryo, serialized, HashBasedTable.class);
        assertEquals(deserialized, table);
    }

    @Test(dataProvider = "Google Guava tables")
    public void testTableCopy(Object[] contents) {
        final HashBasedTable<Object, Object, Object> table = HashBasedTable.create();
        populateTable(table, contents);

        HashBasedTable<Object, Object, Object> copy = _kryo.copy(table);

        assertNotSame(copy, table);
        assertEquals(copy, table);
    }

}