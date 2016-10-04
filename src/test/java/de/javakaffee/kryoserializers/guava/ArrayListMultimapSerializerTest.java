package de.javakaffee.kryoserializers.guava;

import de.javakaffee.kryoserializers.KryoTest;

import com.esotericsoftware.kryo.Kryo;

import com.google.common.collect.ArrayListMultimap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ArrayListMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKyroWithSerializer() {
        _kryo = new Kryo();
        ArrayListMultimapSerializer.registerSerializers(_kryo);
    }

    @Test(dataProvider = "Google Guava multimaps")
    public void testMultimap(Object[] contents) {
        final ArrayListMultimap<Object, Object> multimap = ArrayListMultimap.create();
        populateMultimap(multimap, contents);
        final byte[] serialized = KryoTest.serialize(_kryo, multimap);
        final ArrayListMultimap<Object, Object> deserialized = KryoTest.deserialize(_kryo, serialized, ArrayListMultimap.class);
        assertEqualMultimaps(false, true, deserialized, multimap);
    }
}
