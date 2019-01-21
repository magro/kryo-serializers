package de.javakaffee.kryoserializers.guava;

import com.google.common.collect.Table;
import org.testng.annotations.DataProvider;

public class TableSerializerTestBase {

    @DataProvider(name = "Google Guava tables")
    public Object[][][] getTables() {
        final Object[][] tables = new Object[][]{
                new Object[]{},
                new Object[]{"foo", "bar", "baz"},
                new Object[]{"new", Thread.State.NEW, 1, "run", Thread.State.RUNNABLE, 2},
                new Object[]{'a', 1, 1, 'b', 2, 2, 'c', 3, 3, 'a', 4, 4, 'b', 5, 5},
                new Object[]{'a', 1, 1, 'b', 2, 2, 'c', 3, 3, 'a', 4, 1, 'b', 5, 2}
        };
        final Object[][][] toProvide = new Object[tables.length][][];
        int index = 0;
        for (final Object[] table : tables) {
            toProvide[index++] = new Object[][]{table};
        }
        return toProvide;
    }

    @DataProvider(name = "Google Guava tables (non empty)")
    public Object[][][] getTablesNonEmpty() {
        final Object[][] tables = new Object[][]{
                new Object[]{"foo", "bar", "baz"},
                new Object[]{"new", Thread.State.NEW, 1, "run", Thread.State.RUNNABLE, 2},
                new Object[]{'a', 1, 1, 'b', 2, 2, 'c', 3, 3, 'a', 4, 4, 'b', 5, 5},
                new Object[]{'a', 1, 1, 'b', 2, 2, 'c', 3, 3, 'a', 4, 1, 'b', 5, 2}
        };
        final Object[][][] toProvide = new Object[tables.length][][];
        int index = 0;
        for (final Object[] table : tables) {
            toProvide[index++] = new Object[][]{table};
        }
        return toProvide;
    }

    public <R, C, V> void populateTable(Table<R, C, V> table, Object[] contents) {
        for (int index = 0; index < contents.length; ) {
            table.put((R) contents[index++], (C) contents[index++], (V) contents[index++]);
        }
    }

}
