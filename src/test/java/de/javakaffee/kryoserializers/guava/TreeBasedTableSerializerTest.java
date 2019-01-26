package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeBasedTable;
import de.javakaffee.kryoserializers.KryoTest;
import de.javakaffee.kryoserializers.TestClasses;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Comparator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

public class TreeBasedTableSerializerTest extends TableSerializerTestBase {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
        TreeBasedTableSerializer.registerSerializers(_kryo);
    }

    @Test(dataProvider = "Google Guava tables")
    public void testTable(Object[] contents) {
        final TreeBasedTable<Comparable, Comparable, Object> table = TreeBasedTable.create();
        populateTable(table, contents);
        final byte[] serialized = KryoTest.serialize(_kryo, table);
        final TreeBasedTable<Comparable, Comparable, Object> deserialized = KryoTest.deserialize(_kryo, serialized, TreeBasedTable.class);
        assertEquals(deserialized, table);
    }

    @Test(dataProvider = "Google Guava tables")
    public void testTableCopy(Object[] contents) {
        final TreeBasedTable<Comparable, Comparable, Object> table = TreeBasedTable.create();
        populateTable(table, contents);

        TreeBasedTable<Comparable, Comparable, Object> copy = _kryo.copy(table);

        assertNotSame(copy, table);
        assertEquals(copy, table);
    }

    @Test
    public void testDifferentRowComparatorCopy() {
        final TreeBasedTable<TestClasses.Person, String, String> table = TreeBasedTable.create(TreeBasedTableSerializerTest.CompareByAge.INSTANCE, Ordering.natural());

        // Natural order: "Alice" < "Bob"; by age: "Bob" < "Alice"
        table.put(TestClasses.createPerson("Alice", TestClasses.Person.Gender.FEMALE, 20), "foo", "bar");
        table.put(TestClasses.createPerson("Bob", TestClasses.Person.Gender.MALE, 10), "bar", "baz");

        TreeBasedTable<TestClasses.Person, String, String> copy = _kryo.copy(table);

        assertNotSame(copy, table);
        assertEquals(copy, table);
    }

    @Test
    public void testDifferentRowComparatorSerialize() {
        final TreeBasedTable<TestClasses.Person, String, String> table = TreeBasedTable.create(TreeBasedTableSerializerTest.CompareByAge.INSTANCE, Ordering.natural());

        // Natural order: "Alice" < "Bob"; by age: "Bob" < "Alice"
        table.put(TestClasses.createPerson("Alice", TestClasses.Person.Gender.FEMALE, 20), "foo", "bar");
        table.put(TestClasses.createPerson("Bob", TestClasses.Person.Gender.MALE, 10), "bar", "baz");

        final byte[] serialized = KryoTest.serialize(_kryo, table);
        final TreeBasedTable<TestClasses.Person, String, String> deserialized = KryoTest.deserialize(_kryo, serialized, TreeBasedTable.class);

        assertNotSame(deserialized, table);
        assertEquals(deserialized, table);
    }

    @Test
    public void testDifferentColumnComparatorCopy() {
        final TreeBasedTable<String, TestClasses.Person, String> table = TreeBasedTable.create(Ordering.natural(), TreeBasedTableSerializerTest.CompareByAge.INSTANCE);

        // Natural order: "Alice" < "Bob"; by age: "Bob" < "Alice"
        table.put("foo", TestClasses.createPerson("Alice", TestClasses.Person.Gender.FEMALE, 20), "bar");
        table.put("bar", TestClasses.createPerson("Bob", TestClasses.Person.Gender.MALE, 10), "baz");

        TreeBasedTable<String, TestClasses.Person, String> copy = _kryo.copy(table);

        assertNotSame(copy, table);
        assertEquals(copy, table);
    }

    @Test
    public void testDifferentColumnComparatorSerialize() {
        final TreeBasedTable<String, TestClasses.Person, String> table = TreeBasedTable.create(Ordering.natural(), TreeBasedTableSerializerTest.CompareByAge.INSTANCE);

        // Natural order: "Alice" < "Bob"; by age: "Bob" < "Alice"
        table.put("foo", TestClasses.createPerson("Alice", TestClasses.Person.Gender.FEMALE, 20), "bar");
        table.put("bar", TestClasses.createPerson("Bob", TestClasses.Person.Gender.MALE, 10), "baz");

        final byte[] serialized = KryoTest.serialize(_kryo, table);
        final TreeBasedTable<String, TestClasses.Person, String> deserialized = KryoTest.deserialize(_kryo, serialized, TreeBasedTable.class);

        assertNotSame(deserialized, table);
        assertEquals(deserialized, table);
    }

    private static class CompareByAge implements Comparator<TestClasses.Person> {
        private static final TreeBasedTableSerializerTest.CompareByAge INSTANCE = new TreeBasedTableSerializerTest.CompareByAge();

        @Override
        public int compare(TestClasses.Person o1, TestClasses.Person o2) {
            return o1.getAge().compareTo(o2.getAge());
        }
    }

}