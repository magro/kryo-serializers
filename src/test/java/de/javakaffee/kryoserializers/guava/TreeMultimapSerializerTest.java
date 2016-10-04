package de.javakaffee.kryoserializers.guava;

import de.javakaffee.kryoserializers.KryoTest;

import com.esotericsoftware.kryo.Kryo;

import com.google.common.collect.TreeMultimap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TreeMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKyroWithSerializer() {
        _kryo = new Kryo();
        TreeMultimapSerializer.registerSerializers(_kryo);
    }

    @Test(dataProvider = "Google Guava multimaps (no nulls)")
    public void testMultimap(Object[] contents) {
        final TreeMultimap<Comparable, Comparable> multimap = TreeMultimap.<Comparable, Comparable>create();
        populateMultimap(multimap, contents);
        final byte[] serialized = KryoTest.serialize(_kryo, multimap);
        final TreeMultimap<Comparable, Comparable> deserialized = KryoTest.deserialize(_kryo, serialized, TreeMultimap.class);
        assertEqualMultimaps(true, true, deserialized, multimap);
    }
}
