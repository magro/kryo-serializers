package de.javakaffee.kryoserializers;

import static org.testng.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;

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
    private EnumMap<Vipers, Object> _newTempMap;

    @BeforeTest
    protected void beforeTest() {
        _kryo = new Kryo();
		_kryo.register(EnumMap.class, new EnumMapSerializer());
		_original = new EnumMap<Vipers, Set<String>>(Vipers.class);
		_newTempMap = new EnumMap<Vipers, Object>(Vipers.class);
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
    	_kryo.register(java.util.HashSet.class);

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

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Test
	public void testEnumMapNest() throws Exception {
		_kryo.register(HashSet.class);
		_kryo.register(Vipers.class);
		_kryo.register(Colors.class);
		_kryo.register(EnumMap.class, new EnumMapSerializer());

		final Set<String> mambaAka = new HashSet<String>();
		final Set<String> papaAka = new HashSet<>();
		mambaAka.add("Beatrix Kiddo");
		mambaAka.add("The Bride");
		papaAka.add("Good Job");
		papaAka.add("There is Great");

		_original.put(Vipers.BLACK_MAMBA, mambaAka);
		_original.put(Vipers.SIDEWINDER, papaAka);
		_newTempMap.put(Vipers.COPPERHEAD, _original);

		final File outputFile = File.createTempFile("input_file", "dat");
		try (final Output output = new Output(new FileOutputStream(outputFile))) {
			_kryo.writeObject(output, _newTempMap);
			output.flush();
			output.close();
			final Input input = new Input(new FileInputStream(outputFile));
			final EnumMap tEnumMap = _kryo.readObject(input, EnumMap.class);
			input.close();
			Assert.assertEquals(_newTempMap, tEnumMap);
		}
	}
}
