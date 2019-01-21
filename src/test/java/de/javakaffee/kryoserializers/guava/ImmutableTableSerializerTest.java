package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ImmutableTable;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.*;

public class ImmutableTableSerializerTest {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();

        ImmutableTableSerializer.registerSerializers(_kryo);
    }

    @Test(enabled = true)
    public void testEmpty() {
        final ImmutableTable<?, ?, ?> obj = ImmutableTable.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableTable<?, ?, ?> deserialized = deserialize(_kryo, serialized, ImmutableTable.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }

    @Test(enabled = true)
    public void testSingleton() {
        final ImmutableTable<?, ?, ?> obj = ImmutableTable.of("a", 1, 2.3);
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableTable<?, ?, ?> deserialized = deserialize(_kryo, serialized, ImmutableTable.class);
        assertEquals(deserialized, obj);
    }

    @Test(enabled = true)
    public void testDense() {
        final ImmutableTable<?, ?, ?> obj = ImmutableTable.builder()
                .put("a", 1, 1)
                .put("b", 1, 1)
                .build();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableTable<?, ?, ?> deserialized = deserialize(_kryo, serialized, ImmutableTable.class);
        assertEquals(deserialized, obj);
    }

    @Test(enabled = true)
    public void testSparse() {
        final ImmutableTable<?, ?, ?> obj = ImmutableTable.builder()
                .put("a", 1, 1)
                .put("b", 2, 1)
                .build();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableTable<?, ?, ?> deserialized = deserialize(_kryo, serialized, ImmutableTable.class);
        assertEquals(deserialized, obj);
    }

    @Test(enabled = true)
    public void testCopyEmpty() {
        final ImmutableTable<?, ?, ?> obj = ImmutableTable.of();
        final ImmutableTable<?, ?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test(enabled = true)
    public void testCopySingleton() {
        final ImmutableTable<?, ?, ?> obj = ImmutableTable.of("a", 1, 2.3);
        final ImmutableTable<?, ?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test(enabled = true)
    public void testCopyDense() {
        final ImmutableTable<?, ?, ?> obj = ImmutableTable.builder()
                .put("a", 1, 1)
                .put("b", 1, 1)
                .build();
        final ImmutableTable<?, ?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test(enabled = true)
    public void testCopySparse() {
        final ImmutableTable<?, ?, ?> obj = ImmutableTable.builder()
                .put("a", 1, 1)
                .put("b", 2, 1)
                .build();
        final ImmutableTable<?, ?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

}