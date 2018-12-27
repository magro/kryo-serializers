package de.javakaffee.kryoserializers.guava;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.Sets;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Test for {@link ImmutableSortedSetSerializer}.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class UnmodifiableNavigableSetSerializerTest {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
        UnmodifiableNavigableSetSerializer.registerSerializers(_kryo);
    }

    Class<NavigableSet> unmodifiableClass;
    {
        unmodifiableClass = (Class<NavigableSet>) Sets.unmodifiableNavigableSet(new TreeSet()).getClass();
    }

    UnmodifiableNavigableSetSerializer forUnwrapping = new UnmodifiableNavigableSetSerializer();

    private void assertUnderlyingSet(NavigableSet<String> deserialized, Class<?> class1) {
        assertEquals(
            forUnwrapping.getDelegateFromUnmodifiableNavigableSet(deserialized).getClass(),
            class1,
            "Expected underlying class to match");
    }

    @Test
    public void testEmptyTreeSet() {
        final TreeSet<String> coreSet = Sets.newTreeSet();
        final NavigableSet<String> obj = Sets.unmodifiableNavigableSet(coreSet);
        final byte[] serialized = serialize(_kryo, obj);
        final NavigableSet<String> deserialized = deserialize(_kryo, serialized, unmodifiableClass);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
        // And ensure what we get is truly unmodifiable
        try {
            deserialized.add("a");
            fail("Should have been unable to add a field to an unmodifiable collection post deserialization");
        } catch (UnsupportedOperationException expected) {}
        assertUnderlyingSet(deserialized, coreSet.getClass());
    }

    @Test
    public void testEmptySkipList() {
        final ConcurrentSkipListSet<String> coreSet = new ConcurrentSkipListSet();
        final NavigableSet<String> obj = Sets.unmodifiableNavigableSet(coreSet);
        final byte[] serialized = serialize(_kryo, obj);
        final NavigableSet<String> deserialized = deserialize(_kryo, serialized, unmodifiableClass);
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
        // And ensure what we get is truly unmodifiable
        try {
            deserialized.add("a");
            fail("Should have been unable to add a field to an unmodifiable collection post deserialization");
        } catch (UnsupportedOperationException expected) {}
        assertUnderlyingSet(deserialized, coreSet.getClass());
    }

    @Test
    public void testPopulatedTreeSet() {
        final TreeSet<String> coreSet = Sets.newTreeSet();
        coreSet.add("k");
        coreSet.add("r");
        coreSet.add("y");
        coreSet.add("o");
        final NavigableSet<String> obj = Sets.unmodifiableNavigableSet(coreSet);
        final byte[] serialized = serialize(_kryo, obj);
        final NavigableSet<String> deserialized = deserialize(_kryo, serialized, unmodifiableClass);
        assertFalse(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
        assertEquals(deserialized, obj);
        // And ensure what we get is truly unmodifiable
        try {
            deserialized.add("a");
            fail("Should have been unable to add a field to an unmodifiable collection post deserialization");
        } catch (UnsupportedOperationException expected) {}
        assertUnderlyingSet(deserialized, coreSet.getClass());
    }

    @Test
    public void testPopulatedSkipList() {
        final ConcurrentSkipListSet<String> coreSet = new ConcurrentSkipListSet();
        coreSet.add("k");
        coreSet.add("r");
        coreSet.add("y");
        coreSet.add("o");
        final NavigableSet<String> obj = Sets.unmodifiableNavigableSet(coreSet);
        final byte[] serialized = serialize(_kryo, obj);
        final NavigableSet<String> deserialized = deserialize(_kryo, serialized, unmodifiableClass);
        assertFalse(deserialized.isEmpty());
        assertEquals(deserialized.size(), obj.size());
        assertEquals(deserialized, obj);
        // And ensure what we get is truly unmodifiable
        try {
            deserialized.add("a");
            fail("Should have been unable to add a field to an unmodifiable collection post deserialization");
        } catch (UnsupportedOperationException expected) {}
        assertUnderlyingSet(deserialized, coreSet.getClass());
    }

    // Kryo#copy tests

    @Test
    public void testCopyEmptyTreeSet() {
        final TreeSet<String> coreSet = Sets.newTreeSet();
        final NavigableSet<String> obj = Sets.unmodifiableNavigableSet(coreSet);
        final NavigableSet<String> copied = _kryo.copy(obj);
        assertTrue(copied.isEmpty());
        assertEquals(copied.size(), obj.size());

        // And ensure what we get is truly unmodifiable
        try {
            copied.add("a");
            fail("Should have been unable to add a field to an unmodifiable collection post deserialization");
        } catch (UnsupportedOperationException expected) {}
        assertUnderlyingSet(copied, coreSet.getClass());
    }

    @Test
    public void testCopyEmptySkipList() {
        final ConcurrentSkipListSet<String> coreSet = new ConcurrentSkipListSet();
        final NavigableSet<String> obj = Sets.unmodifiableNavigableSet(coreSet);
        final NavigableSet<String> copied = _kryo.copy(obj);
        assertTrue(copied.isEmpty());
        assertEquals(copied.size(), obj.size());

        // And ensure what we get is truly unmodifiable
        try {
            copied.add("a");
            fail("Should have been unable to add a field to an unmodifiable collection post deserialization");
        } catch (UnsupportedOperationException expected) {}
        assertUnderlyingSet(copied, coreSet.getClass());
    }

    @Test
    public void testCopyPopulatedTreeSet() {
        final TreeSet<String> coreSet = Sets.newTreeSet();
        coreSet.add("k");
        coreSet.add("r");
        coreSet.add("y");
        coreSet.add("o");
        final NavigableSet<String> obj = Sets.unmodifiableNavigableSet(coreSet);
        final NavigableSet<String> copied = _kryo.copy(obj);
        assertFalse(copied.isEmpty());
        assertEquals(copied.size(), obj.size());
        assertEquals(copied, obj);

        // And ensure what we get is truly unmodifiable
        try {
            copied.add("a");
            fail("Should have been unable to add a field to an unmodifiable collection post deserialization");
        } catch (UnsupportedOperationException expected) {}
        assertUnderlyingSet(copied, coreSet.getClass());
    }

    @Test
    public void testCopyPopulatedSkipList() {
        final ConcurrentSkipListSet<String> coreSet = new ConcurrentSkipListSet();
        coreSet.add("k");
        coreSet.add("r");
        coreSet.add("y");
        coreSet.add("o");
        final NavigableSet<String> obj = Sets.unmodifiableNavigableSet(coreSet);
        final NavigableSet<String> copied = _kryo.copy(obj);
        assertFalse(copied.isEmpty());
        assertEquals(copied.size(), obj.size());
        assertEquals(copied, obj);

        // And ensure what we get is truly unmodifiable
        try {
            copied.add("a");
            fail("Should have been unable to add a field to an unmodifiable collection post deserialization");
        } catch (UnsupportedOperationException expected) {}
        assertUnderlyingSet(copied, coreSet.getClass());
    }
}
