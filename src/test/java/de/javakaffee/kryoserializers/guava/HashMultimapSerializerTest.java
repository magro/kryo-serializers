package de.javakaffee.kryoserializers.guava;

import de.javakaffee.kryoserializers.KryoTest;

import com.esotericsoftware.kryo.Kryo;

import com.google.common.collect.HashMultimap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HashMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKyroWithSerializer() {
        _kryo = new Kryo();
        HashMultimapSerializer.registerSerializers(_kryo);
    }

    @Test(dataProvider = "Google Guava multimaps")
    public void testMultimap(Object[] contents) {
        final HashMultimap<Object, Object> multimap = HashMultimap.create();
        populateMultimap(multimap, contents);
        final byte[] serialized = KryoTest.serialize(_kryo, multimap);
        final HashMultimap<Object, Object> deserialized = KryoTest.deserialize(_kryo, serialized, HashMultimap.class);
        assertEqualMultimaps(false, false, deserialized, multimap);
    }
}
