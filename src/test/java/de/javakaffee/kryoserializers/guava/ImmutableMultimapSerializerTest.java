package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ImmutableMultimap;

import static org.testng.Assert.*;

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
    public void testEmpty() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }

    @Test
    public void testImmutableListSerializerAlreadyRegistered() {
        ImmutableListSerializer.registerSerializers(_kryo);
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }

    @Test
    public void testImmutableMapSerializerAlreadyRegistered() {
        ImmutableMapSerializer.registerSerializers(_kryo);
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }


    @Test
    public void testSingleton() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of(3, "k");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
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
    public void testRegularMultipleElementsPerKey() {
        final ImmutableMultimap<?, ?> obj = ImmutableMultimap.of(3, "k", 3, "r", 4, "y", 4, "o");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMultimap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMultimap.class);
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
}