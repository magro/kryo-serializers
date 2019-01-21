package de.javakaffee.kryoserializers.guava;

import static org.testng.Assert.assertNotSame;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.LinkedHashMultimap;
import de.javakaffee.kryoserializers.KryoTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LinkedHashMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKryoWithSerializer() {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
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

    @Test(dataProvider = "Google Guava multimaps")
    public void testMultimapCopy(Object[] contents) {
        final LinkedHashMultimap<Comparable, Comparable> multimap = LinkedHashMultimap.create();
        populateMultimap(multimap, contents);

        LinkedHashMultimap<Comparable, Comparable> copy = _kryo.copy(multimap);

        assertNotSame(copy, multimap);
        assertEqualMultimaps(true, true, copy, multimap);
    }
}
