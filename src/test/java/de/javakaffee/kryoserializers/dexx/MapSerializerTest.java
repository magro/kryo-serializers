package de.javakaffee.kryoserializers.dexx;

import com.esotericsoftware.kryo.Kryo;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.github.andrewoma.dexx.collection.Map;
import com.github.andrewoma.dexx.collection.Maps;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.*;

/**
* Test for {@link MapSerializer}
 */
public class MapSerializerTest {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
        MapSerializer.registerSerializers(_kryo);
    }

    @Test(enabled = true)
    public void testEmpty() {
        final Map<?, ?> obj = Maps.of();
        final byte[] serialized = serialize(_kryo, obj);
        final Map<?, ?> deserialized = deserialize(_kryo, serialized, Map.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }

    @Test(enabled = true)
    public void testRegular() {
        final Map<?, ?> obj = Maps.of(3, "k", 5, "r", 6, "y");
        final byte[] serialized = serialize(_kryo, obj);
        final Map<?, ?> deserialized = deserialize(_kryo, serialized, Map.class);
        assertEquals(deserialized, obj);
    }

    // Kryo#copy tests

    @Test(enabled = true)
    public void testCopyEmpty() {
        final Map<?, ?> obj = Maps.of();
        final Map<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test(enabled = true)
    public void testCopyRegular() {
        final Map<?, ?> obj = Maps.of(1, 2, 3, 4);
        final Map<?, ?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }
}
