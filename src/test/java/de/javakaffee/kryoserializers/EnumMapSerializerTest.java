package de.javakaffee.kryoserializers;

import static org.testng.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.factories.SerializerFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A test case for the {@link EnumMapSerializer}.
 */
public class EnumMapSerializerTest {
	private static enum Vipers {
		SNAKE_CHARMER, BLACK_MAMBA, COTTONMOUTH, COPPERHEAD, CALIFORNIA_MOUNTAIN_SNAKE, SIDEWINDER;
	}

	private static enum Colors {
		BLUE, ORANGE, PINK, WHITE, BROWN, BLONDE;
	}

    private Kryo _kryo;
    private EnumMap<Vipers, Set<String>> _original;
    
    @BeforeTest
    protected void beforeTest() {
        _kryo = new Kryo();
		_kryo.register(EnumMap.class, new EnumMapSerializer());
		_original = new EnumMap<Vipers, Set<String>>(Vipers.class);
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expectedExceptions={ClassCastException.class})
    public void testCopyEmpty() throws Exception {
        EnumMap copy = _kryo.copy(_original);        
        // The next statement asserts that the key type of the copy is initialized correctly - 
        // it should throw the expected ClassCastException.
    	copy.put(Colors.BROWN, new HashSet<String>());
    }

	@Test
    public void testDeepCopy() throws Exception {
		final Set<String> mambaAka = new HashSet<String>();
		mambaAka.add("Beatrix Kiddo");
		mambaAka.add("The Bride");
        _original.put(Vipers.BLACK_MAMBA, mambaAka);
        
		EnumMap<Vipers, Set<String>> copy = _kryo.copy(_original);        
        assertNotSame(_original, copy);
        assertTrue(copy.containsKey(Vipers.BLACK_MAMBA));
        assertNotSame(_original.get(Vipers.BLACK_MAMBA), copy.get(Vipers.BLACK_MAMBA));
        assertEquals(_original, copy);
    }
}
