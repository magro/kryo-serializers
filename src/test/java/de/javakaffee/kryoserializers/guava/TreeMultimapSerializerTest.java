package de.javakaffee.kryoserializers.guava;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import de.javakaffee.kryoserializers.KryoTest;
import de.javakaffee.kryoserializers.TestClasses;
import de.javakaffee.kryoserializers.TestClasses.Person;
import de.javakaffee.kryoserializers.TestClasses.Person.Gender;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Comparator;

import static org.testng.Assert.assertNotSame;

public class TreeMultimapSerializerTest extends MultimapSerializerTestBase {

    private Kryo _kryo;

    @BeforeClass
    public void initializeKyroWithSerializer() {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
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

    @Test(dataProvider = "Google Guava multimaps (no nulls)")
    public void testMultimapCopy(Object[] contents) {
        final TreeMultimap<Comparable, Comparable> multimap = TreeMultimap.create();
        populateMultimap(multimap, contents);

        TreeMultimap<Comparable, Comparable> copy = _kryo.copy(multimap);

        assertNotSame(copy, multimap);
        assertEqualMultimaps(true, true, copy, multimap);
    }

    @Test
    public void testDifferentKeyComparatorCopy() {
        final TreeMultimap<TestClasses.Person, String> multimap = TreeMultimap.create(CompareByAge.INSTANCE, Ordering.natural());

        // Natural order: "Alice" < "Bob"; by age: "Bob" < "Alice"
        multimap.put(TestClasses.createPerson("Alice", Gender.FEMALE, 20), "foo");
        multimap.put(TestClasses.createPerson("Bob", Gender.MALE, 10), "bar");

        TreeMultimap<TestClasses.Person, String> copy = _kryo.copy(multimap);

        assertNotSame(copy, multimap);
        assertEqualMultimaps(true, true, copy, multimap);
    }

    @Test
    public void testDifferentKeyComparatorSerialize() {
        final TreeMultimap<TestClasses.Person, String> multimap = TreeMultimap.create(CompareByAge.INSTANCE, Ordering.natural());

        // Natural order: "Alice" < "Bob"; by age: "Bob" < "Alice"
        multimap.put(TestClasses.createPerson("Alice", Gender.FEMALE, 20), "foo");
        multimap.put(TestClasses.createPerson("Bob", Gender.MALE, 10), "bar");

        final byte[] serialized = KryoTest.serialize(_kryo, multimap);
        final TreeMultimap<TestClasses.Person, String> deserialized = KryoTest.deserialize(_kryo, serialized, TreeMultimap.class);

        assertNotSame(deserialized, multimap);
        assertEqualMultimaps(true, true, deserialized, multimap);
    }

    @Test
    public void testDifferentValueComparatorCopy() {
        final TreeMultimap<String, TestClasses.Person> multimap = TreeMultimap.create(Ordering.<String>natural(), CompareByAge.INSTANCE);

        // Natural order: "Alice" < "Bob"; by age: "Bob" < "Alice"
        multimap.put("foo", TestClasses.createPerson("Alice", Gender.FEMALE, 20));
        multimap.put("bar", TestClasses.createPerson("Bob", Gender.MALE, 10));

        TreeMultimap<String, TestClasses.Person> copy = _kryo.copy(multimap);

        assertNotSame(copy, multimap);
        assertEqualMultimaps(true, true, copy, multimap);
    }

    @Test
    public void testDifferentValueComparatorSerialize() {
        final TreeMultimap<String, TestClasses.Person> multimap = TreeMultimap.create(Ordering.<String>natural(), CompareByAge.INSTANCE);

        // Natural order: "Alice" < "Bob"; by age: "Bob" < "Alice"
        multimap.put("foo", TestClasses.createPerson("Alice", Gender.FEMALE, 20));
        multimap.put("bar", TestClasses.createPerson("Bob", Gender.MALE, 10));

        final byte[] serialized = KryoTest.serialize(_kryo, multimap);
        final TreeMultimap<String, TestClasses.Person> deserialized = KryoTest.deserialize(_kryo, serialized, TreeMultimap.class);

        assertNotSame(deserialized, multimap);
        assertEqualMultimaps(true, true, deserialized, multimap);
    }

    private static class CompareByAge implements Comparator<Person>, Serializable {
        private static final CompareByAge INSTANCE = new CompareByAge();
        private static final long serialVersionUID = -6835503002691497297L;

        @Override
        public int compare(Person o1, Person o2) {
            return o1.getAge().compareTo(o2.getAge());
        }
    }
}
