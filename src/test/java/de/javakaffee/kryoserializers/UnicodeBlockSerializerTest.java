package de.javakaffee.kryoserializers;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import com.esotericsoftware.kryo.Kryo;

import org.objenesis.ObjenesisStd;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.Character.UnicodeBlock;

/**
 * Test for {@link UnicodeBlockSerializer}.
 *
 * @author <a href="mailto:seahen123@gmail.com">Chris Hennick</a>
 */
public class UnicodeBlockSerializerTest {

    private static final String NONEXISTENT_BLOCK_NAME = "RURITANIAN";
    private Kryo _kryo;

    @BeforeTest
    protected void beforeTest() {
        _kryo = new Kryo();
        _kryo.register(UnicodeBlock.class, new UnicodeBlockSerializer());
    }

    @Test
    public void testBasicRoundTrip() {
        byte[] serialized = serialize(_kryo, UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS);
        assertSame(UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS,
                deserialize(_kryo, serialized, UnicodeBlock.class));
    }

    @Test
    public void testDeserializingUnknownInstanceReturnsNull() {
        byte[] serialized = serialize(_kryo, new ObjenesisStd().newInstance(UnicodeBlock.class));
        assertNull(deserialize(_kryo, serialized, UnicodeBlock.class));
        serialized = serialize(_kryo, NONEXISTENT_BLOCK_NAME);
        assertNull(deserialize(_kryo, serialized, UnicodeBlock.class));
    }

    /**
     * Copying should return the same object, since UnicodeBlock is effectively an enum.
     */
    @Test
    public void testShallowCopy() {
        assertSame(UnicodeBlock.HIRAGANA, _kryo.copy(UnicodeBlock.HIRAGANA));
    }

    /**
     * Copying should return the same object, since UnicodeBlock is effectively an enum.
     */
    @Test
    public void testDeepCopy() {
        assertSame(UnicodeBlock.VEDIC_EXTENSIONS, _kryo.deepCopy(UnicodeBlock.VEDIC_EXTENSIONS));
    }
}
