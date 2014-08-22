package de.javakaffee.kryoserializers.guava;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;


/**
 * Test for {@link ImmutableListSerializer}.
 */
public class ImmutableListSerializerTest {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();

        ImmutableListSerializer.registerSerializers(_kryo);
    }

    @Test( enabled = true )
    public void testEmpty() {
        final ImmutableList<?> obj = ImmutableList.of();
        final byte[] serialized = serialize( _kryo, obj );
        final ImmutableList<?> deserialized = deserialize(_kryo, serialized, ImmutableList.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }

    @Test( enabled = true )
    public void testSingleton() {
        final ImmutableList<?> obj = ImmutableList.of(3);
        final byte[] serialized = serialize( _kryo, obj );
        final ImmutableList<?> deserialized = deserialize(_kryo, serialized, ImmutableList.class);
        assertEquals(deserialized, obj);
    }

    @Test( enabled = true )
    public void testRegular() {
        final ImmutableList<?> obj = ImmutableList.of(3, 4, 5, 6);
        final byte[] serialized = serialize( _kryo, obj );
        final ImmutableList<?> deserialized = deserialize(_kryo, serialized, ImmutableList.class);
        assertEquals(deserialized, obj);
    }

    @Test( enabled = true )
    public void testSubList() {
        final ImmutableList<?> obj = ImmutableList.of(3, 4, 5, 6).subList(1, 2);
        final byte[] serialized = serialize( _kryo, obj );
        final ImmutableList<?> deserialized = deserialize(_kryo, serialized, ImmutableList.class);
        assertEquals(deserialized, obj);
    }

    @Test( enabled = true )
    public void testReverse() {
        final ImmutableList<?> obj = ImmutableList.of(3,4,5).reverse();
        final byte[] serialized = serialize( _kryo, obj );
        final ImmutableList<?> deserialized = deserialize(_kryo, serialized, ImmutableList.class);
        assertEquals(deserialized, obj);
    }

    @Test( enabled = true )
    public void testStringAsImmutableList() {
        final ImmutableList<?> obj = Lists.charactersOf("KryoRocks");
        final byte[] serialized = serialize( _kryo, obj );
        final ImmutableList<?> deserialized = deserialize(_kryo, serialized, ImmutableList.class);
        assertEquals(deserialized, obj);
    }

    @Test( enabled = true )
    public void testValues() {
        Table<Integer,Integer,Integer> baseTable = HashBasedTable.create();
        baseTable.put(1, 2, 3);
        baseTable.put(4, 5, 6);
        Table<Integer, Integer, Integer> table = ImmutableTable.copyOf(baseTable);
        final ImmutableList<?> obj = (ImmutableList<?>) table.values();
        final byte[] serialized = serialize( _kryo, obj );
        final ImmutableList<?> deserialized = deserialize(_kryo, serialized, ImmutableList.class);
        assertEquals(deserialized, obj);
    }

    // Kryo#copy tests

    @Test( enabled = true )
    public void testCopyEmpty() {
        final ImmutableList<?> obj = ImmutableList.of();
        final ImmutableList<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test( enabled = true )
    public void testCopySingleton() {
        final ImmutableList<?> obj = ImmutableList.of(1);
        final ImmutableList<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test( enabled = true )
    public void testCopyRegular() {
        final ImmutableList<?> obj = ImmutableList.of(1, 2, 3);
        final ImmutableList<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test( enabled = true )
    public void testCopySubList() {
        final ImmutableList<?> obj = ImmutableList.of(1, 2, 3, 4).subList(1, 2);
        final ImmutableList<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test( enabled = true )
    public void testCopyReverse() {
        final ImmutableList<?> obj = ImmutableList.of(1,2,3).reverse();
        final ImmutableList<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test( enabled = true )
    public void testCopyStringAsImmutableList() {
        final ImmutableList<?> obj = Lists.charactersOf("KryoRocks");
        final ImmutableList<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test( enabled = true )
    public void testCopyValues() {
        Table<Integer,Integer,Integer> baseTable = HashBasedTable.create();
        baseTable.put(1, 2, 3);
        baseTable.put(4, 5, 6);
        Table<Integer, Integer, Integer> table = ImmutableTable.copyOf(baseTable);
        final ImmutableList<?> obj = (ImmutableList<?>) table.values();
        final ImmutableList<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }
}
