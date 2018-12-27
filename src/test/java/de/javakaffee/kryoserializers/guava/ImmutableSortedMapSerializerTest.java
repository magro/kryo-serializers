package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.EnumMap;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.*;

public class ImmutableSortedMapSerializerTest {

    private enum Planet {
        MERCURY, VENUS, EARTH, MARS;
    }

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
        ImmutableSortedMapSerializer.registerSerializers(_kryo);
    }

    @Test
    public void testEmpty() {
        final ImmutableSortedMap<?, ?> obj = ImmutableSortedMap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableSortedMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSortedMap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }

    @Test
    public void testSingleton() {
        final ImmutableSortedMap<?, ?> obj = ImmutableSortedMap.of(3, "k");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableSortedMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSortedMap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testRegular() {
        final ImmutableSortedMap<?, ?> obj = ImmutableSortedMap.of(5, "r", 3, "k", 6, "y");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableSortedMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSortedMap.class);
        assertEquals(deserialized, obj);
    }


    @Test
    public void testDescending() {
        final ImmutableSortedMap<?, ?> obj = ImmutableSortedMap.of(1, "a", 5, "e", 3, "c").descendingMap();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableSortedMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSortedMap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testEnum() {
        final EnumMap<Planet, String> obj = new EnumMap<Planet, String>(Planet.class);
        for (Planet p : Planet.values()) {
            obj.put(p, p.name());
        }

        final ImmutableSortedMap<?, ?> immutableObj = ImmutableSortedMap.copyOf(obj);
        final byte[] serialized = serialize(_kryo, immutableObj);
        final ImmutableSortedMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableSortedMap.class);
        assertEquals(deserialized, immutableObj);
    }

    // Kryo#copy tests

    @Test
    public void testCopyEmpty() {
        final ImmutableSortedMap<?, ?> obj = ImmutableSortedMap.of();
        final ImmutableSortedMap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopySingleton() {
        final ImmutableSortedMap<?, ?> obj = ImmutableSortedMap.of(1, 1);
        final ImmutableSortedMap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyRegular() {
        final ImmutableSortedMap<?, ?> obj = ImmutableSortedMap.of(1, 2, 3, 4);
        final ImmutableSortedMap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

}