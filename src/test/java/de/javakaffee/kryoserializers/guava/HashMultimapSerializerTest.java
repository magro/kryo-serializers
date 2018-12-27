package de.javakaffee.kryoserializers.guava;

import static org.testng.Assert.assertNotSame;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.HashMultimap;
import de.javakaffee.kryoserializers.KryoTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HashMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKyroWithSerializer() {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
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

    @Test(dataProvider = "Google Guava multimaps")
    public void testMultimapCopy(Object[] contents) {
        final HashMultimap<Comparable, Comparable> multimap = HashMultimap.create();
        populateMultimap(multimap, contents);

        HashMultimap<Comparable, Comparable> copy = _kryo.copy(multimap);

        assertNotSame(copy, multimap);
        assertEqualMultimaps(false, false, copy, multimap);
    }
}
