package de.javakaffee.kryoserializers.guava;

import de.javakaffee.kryoserializers.KryoTest;

import com.esotericsoftware.kryo.Kryo;

import com.google.common.collect.LinkedListMultimap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LinkedListMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKyroWithSerializer() {
        _kryo = new Kryo();
        LinkedListMultimapSerializer.registerSerializers(_kryo);
    }

    @Test(dataProvider = "Google Guava multimaps")
    public void testMultimap(Object[] contents) {
        final LinkedListMultimap<Object, Object> multimap = LinkedListMultimap.create();
        populateMultimap(multimap, contents);
        final byte[] serialized = KryoTest.serialize(_kryo, multimap);
        final LinkedListMultimap<Object, Object> deserialized = KryoTest.deserialize(_kryo, serialized, LinkedListMultimap.class);
        assertEqualMultimaps(true, true, deserialized, multimap);
    }
}
