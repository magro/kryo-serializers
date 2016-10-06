package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;

import static org.testng.Assert.*;

import com.google.common.collect.ImmutableSetMultimap;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;

public class ImmutableMultimapSerializerTest {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();

        ImmutableMultimapSerializer.registerSerializers(_kryo);
    }

    @Test
    public void testRegularEmpty() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableListMultimapEmpty() {
        final ImmutableMultimap<?, ?> obj = ImmutableListMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableListMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableSetMultimapEmpty() {
        final ImmutableMultimap<?, ?> obj = ImmutableSetMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSetMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized, obj);
    }

    @Test
    public void testRegularSingleton() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of(3, "k");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableListMultimapSingleton() {
        final ImmutableMultimap<?, ?> obj = ImmutableListMultimap.of(3, "k");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableListMultimap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableSetMultimapSingleton() {
        final ImmutableMultimap<?, ?> obj = ImmutableSetMultimap.of(3, "k");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSetMultimap.class);
        assertEquals(deserialized.getClass(), obj.getClass());
        assertEquals(deserialized, obj);
    }

    @Test
    public void testRegular() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of(3, "k", 5, "r", 6, "y");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableListMultimap() {
        final ImmutableMultimap<?, ?> obj = ImmutableListMultimap.of(3, "k", 5, "r", 6, "y");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableListMultimap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableSetMultimap() {
        final ImmutableMultimap<?, ?> obj = ImmutableSetMultimap.of(3, "k", 5, "r", 6, "y");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSetMultimap.class);
        assertEquals(deserialized.getClass(), obj.getClass());
        assertEquals(deserialized, obj);
    }

    @Test
    public void testRegularMultipleElementsPerKey() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of(3, "k", 3, "r", 4, "y", 4, "o");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableListMultimapMultipleElementsPerKey() {
        final ImmutableMultimap<?, ?> obj = ImmutableListMultimap.of(3, "k", 3, "r", 4, "y", 4, "o");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableListMultimap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableSetMultimapMultipleElementsPerKey() {
        final ImmutableMultimap<?, ?> obj = ImmutableSetMultimap.of(3, "k", 3, "r", 4, "y", 4, "o");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSetMultimap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableMapSerializerAlreadyRegistered() {
        ImmutableMapSerializer.registerSerializers(_kryo);
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableListSerializerAlreadyRegistered() {
        ImmutableListSerializer.registerSerializers(_kryo);
        final ImmutableMultimap<?, ?> obj = ImmutableListMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableListMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized, obj);
    }

    @Test
    public void testImmutableSetSerializerAlreadyRegistered() {
        ImmutableSetSerializer.registerSerializers(_kryo);
        final ImmutableMultimap<?, ?> obj = ImmutableSetMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSetMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized, obj);
    }

    // Kryo#copy tests

    @Test
    public void testCopyEmpty() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of();
        final ImmutableMultimap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopySingleton() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of(1, "k");
        final ImmutableMultimap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyRegular() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of(1, "k", 2, "r", 3, "y");
        final ImmutableMultimap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyImmutableListMultimap() {
        final ImmutableMultimap<?, ?> obj = ImmutableListMultimap.of(1, "k", 2, "r", 3, "y");
        final ImmutableMultimap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyImmutableListMultimapEmpty() {
        final ImmutableMultimap<?, ?> obj = ImmutableListMultimap.of();
        final ImmutableMultimap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyImmutableSetMultimap() {
        final ImmutableMultimap<?, ?> obj = ImmutableSetMultimap.of(1, "k", 2, "r", 3, "y");
        final ImmutableMultimap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyImmutableSetMultimapEmpty() {
        final ImmutableMultimap<?, ?> obj = ImmutableSetMultimap.of();
        final ImmutableMultimap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }
}