package de.javakaffee.kryoserializers.guava;


import static org.testng.Assert.assertNotSame;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.LinkedListMultimap;
import de.javakaffee.kryoserializers.KryoTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LinkedListMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKyroWithSerializer() {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
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

    @Test(dataProvider = "Google Guava multimaps")
    public void testMultimapCopy(Object[] contents) {
        final LinkedListMultimap<Object, Object> multimap = LinkedListMultimap.create();
        populateMultimap(multimap, contents);

        LinkedListMultimap<Object, Object> copy = _kryo.copy(multimap);

        assertNotSame(copy, multimap);
        assertEqualMultimaps(true, true, copy, multimap);
    }
}
