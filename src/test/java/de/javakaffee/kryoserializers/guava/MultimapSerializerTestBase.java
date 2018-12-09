package de.javakaffee.kryoserializers.guava;

import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

public abstract class MultimapSerializerTestBase {

    protected <K, V> void populateMultimap(Multimap<K, V> multimap, Object[] contents) {
        for (int index = 0; index < contents.length;) {
            multimap.put((K) contents[index++], (V) contents[index++]);
        }
    }

    protected <K, V> void assertEqualMultimaps(boolean orderedKeys, boolean orderedValues,
        Multimap<K, V> actual, Multimap<K, V> expected) {
        if (orderedKeys) {
            Assert.assertEquals(actual.keySet().toArray(), expected.keySet().toArray());
        } else {
            Assert.assertEqualsNoOrder(actual.keySet().toArray(), expected.keySet().toArray());
        }
        for (final K key : expected.keySet()) {
            if (orderedValues) {
                Assert.assertEquals(actual.get(key).toArray(), expected.get(key).toArray());
            } else {
                Assert.assertEqualsNoOrder(actual.get(key).toArray(), expected.get(key).toArray());
            }
        }
    }

    @DataProvider(name = "Google Guava multimaps")
    public Object[][][] getMultimaps() {
        final Object[][] multimaps = new Object[][]{new Object[]{},
            new Object[]{"foo", "bar"},
            new Object[]{"foo", null},
            new Object[]{null, "bar"},
            new Object[]{null, null},
            new Object[]{"new", Thread.State.NEW, "run", Thread.State.RUNNABLE},
            new Object[]{1.0, "foo", null, "bar", 1.0, null, null, "baz", 1.0, "wibble"},
            new Object[]{'a', 1, 'b', 2, 'c', 3, 'a', 4, 'b', 5},
            new Object[]{'a', 1, 'b', 2, 'c', 3, 'a', 1, 'b', 2}};
        final Object[][][] toProvide = new Object[multimaps.length][][];
        int index = 0;
        for (final Object[] multimap : multimaps) {
            toProvide[index++] = new Object[][]{multimap};
        }
        return toProvide;
    }

    @DataProvider(name = "Google Guava multimaps (no nulls)")
    public Object[][][] getMultimapsNoNulls() {
        final List<Object[][]> multimaps = new ArrayList<Object[][]>();
        for (final Object[][] multimap : getMultimaps()) {
            boolean isNull = false;
            for (final Object element : multimap[0]) {
                if (element == null) {
                    isNull = true;
                    break;
                }
            }
            if (!isNull) {
                multimaps.add(multimap);
            }
        }
        return multimaps.toArray(new Object[multimaps.size()][][]);
    }
}
