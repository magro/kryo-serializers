package de.javakaffee.kryoserializers.dexx;

import com.esotericsoftware.kryo.Kryo;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;

import com.github.andrewoma.dexx.collection.Set;
import com.github.andrewoma.dexx.collection.Sets;

/**
 * Test for {@link SetSerializer}.
 */
public class SetSerializerTest {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();

        SetSerializer.registerSerializers(_kryo);
    }

    @Test(enabled = true)
    public void testEmpty() {
        final Set<?> obj = Sets.of();
        final byte[] serialized = serialize( _kryo, obj );
        final Set<?> deserialized = deserialize(_kryo, serialized, Set.class);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
    }

    @Test(enabled = true)
    public void testRegular() {
        final Set<?> obj = Sets.of(3, 4, 5, 6);
        final byte[] serialized = serialize( _kryo, obj );
        final Set<?> deserialized = deserialize(_kryo, serialized, Set.class);
        assertEquals(deserialized, obj);
    }

    // Kryo#copy tests

    @Test(enabled = true)
    public void testCopyEmpty() {
        final Set<?> obj = Sets.of();
        final Set<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }

    @Test(enabled = true)
    public void testCopyRegular() {
        final Set<?> obj = Sets.of(1, 2, 3);
        final Set<?> copied = _kryo.copy(obj);
        assertSame(copied, obj);
    }
}
