package de.javakaffee.kryoserializers.guava;

import de.javakaffee.kryoserializers.KryoTest;

import com.esotericsoftware.kryo.Kryo;

import com.google.common.collect.LinkedHashMultimap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LinkedHashMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKyroWithSerializer() {
        _kryo = new Kryo();
        LinkedHashMultimapSerializer.registerSerializers(_kryo);
    }

    @Test(dataProvider = "Google Guava multimaps")
    public void testMultimap(Object[] contents) {
        final LinkedHashMultimap<Object, Object> multimap = LinkedHashMultimap.create();
        populateMultimap(multimap, contents);
        final byte[] serialized = KryoTest.serialize(_kryo, multimap);
        final LinkedHashMultimap<Object, Object> deserialized = KryoTest.deserialize(_kryo, serialized, LinkedHashMultimap.class);
        assertEqualMultimaps(true, true, deserialized, multimap);
    }
}
