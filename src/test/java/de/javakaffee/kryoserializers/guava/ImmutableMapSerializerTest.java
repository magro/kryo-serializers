package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.EnumMap;
import java.util.Map;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.*;

/**
 * Created by pmarcos on 29/06/15.
 */
public class ImmutableMapSerializerTest {

    private enum Planet {
        MERCURY, VENUS, EARTH, MARS;
    }

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
        ImmutableMapSerializer.registerSerializers(_kryo);
    }

    @Test
    public void testEmpty() {
        final ImmutableMap<?, ?> obj = ImmutableMap.of();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }

    @Test
    public void testSingleton() {
        final ImmutableMap<?, ?> obj = ImmutableMap.of(3, "k");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testRegular() {
        final ImmutableMap<?, ?> obj = ImmutableMap.of(3, "k", 5, "r", 6, "y");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testEnum() {
        final EnumMap<Planet, String> obj = new EnumMap<Planet, String>(Planet.class);
        for (Planet p : Planet.values()) {
            obj.put(p, p.name());
        }

        final ImmutableMap<?, ?> immutableObj = ImmutableMap.copyOf(obj);
        final byte[] serialized = serialize(_kryo, immutableObj);
        final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
        assertEquals(deserialized, immutableObj);
    }

    @Test
    public void testRowMap() {
        ImmutableMap<Object, Map<Object, Object>> obj = getDenseImmutableTable().rowMap();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testRow() {
        Map<Object, Object> obj = getDenseImmutableTable().rowMap().get("a");
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testColumnMap() {
        ImmutableMap<Object, Map<Object, Object>> obj = getDenseImmutableTable().columnMap();
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
        assertEquals(deserialized, obj);
    }

    @Test
    public void testColumn() {
        Map<Object, Object> obj = getDenseImmutableTable().columnMap().get(1);
        final byte[] serialized = serialize(_kryo, obj);
        final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
        assertEquals(deserialized, obj);
    }

    // Kryo#copy tests

    @Test
    public void testCopyEmpty() {
        final ImmutableMap<?, ?> obj = ImmutableMap.of();
        final ImmutableMap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopySingleton() {
        final ImmutableMap<?, ?> obj = ImmutableMap.of(1, 1);
        final ImmutableMap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyRegular() {
        final ImmutableMap<?, ?> obj = ImmutableMap.of(1, 2, 3, 4);
        final ImmutableMap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyRowMap() {
        final ImmutableMap<?, ?> obj = getDenseImmutableTable().rowMap();
        final ImmutableMap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyRow() {
        final Map<Object, Object> obj = getDenseImmutableTable().rowMap().get("a");
        final Map<Object, Object> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyColumnMap() {
        final ImmutableMap<?, ?> obj = getDenseImmutableTable().columnMap();
        final ImmutableMap<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test
    public void testCopyColumn() {
        final Map<Object, Object> obj = getDenseImmutableTable().columnMap().get(1);
        final Map<Object, Object> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    private ImmutableTable<Object, Object, Object> getDenseImmutableTable() {
        return ImmutableTable.builder()
                .put("a", 1, 1)
                .put("b", 1, 1)
                .build();
    }

}