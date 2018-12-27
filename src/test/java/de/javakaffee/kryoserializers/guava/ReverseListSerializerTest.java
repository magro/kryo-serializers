package de.javakaffee.kryoserializers.guava;

import com.google.common.collect.Lists;

import com.esotericsoftware.kryo.Kryo;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


/**
 * Test for {@link ReverseListSerializer}.
 */
public class ReverseListSerializerTest {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();
        _kryo.setRegistrationRequired(false);
        ReverseListSerializer.registerSerializers(_kryo);
    }

    @Test( enabled = true )
    public void testEmptyReverseList() {
        testEmpty(Lists.reverse(Lists.newLinkedList()));
    }

    @Test( enabled = true )
    public void testEmptyRandomAccessReverseList() {
        testEmpty(Lists.reverse(Lists.newArrayList()));
    }

    private void testEmpty(final List<?> reverseList) {
        final byte[] serialized = serialize( _kryo, reverseList );
        final List<?> deserialized = deserialize(_kryo, serialized, reverseList.getClass());
        assertEquals(deserialized.getClass(), reverseList.getClass());
        assertTrue(deserialized.isEmpty());
        assertEquals(deserialized.size(), reverseList.size());
    }

    @Test( enabled = true )
    public void testSingletonReverseList() {
        testSingleton(Lists.reverse(Lists.newLinkedList(Collections.singleton(3))));
    }

    @Test( enabled = true )
    public void testSingletonRandomAccessReverseList() {
        testSingleton(Lists.reverse(Lists.newArrayList(Collections.singleton(3))));
    }

    private void testSingleton(final List<?> reverseList) {
        final byte[] serialized = serialize( _kryo, reverseList );
        final List<?> deserialized = deserialize(_kryo, serialized, reverseList.getClass());
        assertEquals(deserialized.getClass(), reverseList.getClass());
        assertEquals(deserialized, reverseList);
    }

    @Test( enabled = true )
    public void testRegularReverseList() {
        testRegular(Lists.reverse(Lists.newLinkedList(Arrays.asList(3, 4, 5, 6))));
    }

    @Test( enabled = true )
    public void testRegularRandomAccessReverseList() {
        testRegular(Lists.reverse(Lists.newArrayList(3, 4, 5, 6)));
    }

    private void testRegular(final List<?> reverseList) {
        final byte[] serialized = serialize( _kryo, reverseList );
        final List<?> deserialized = deserialize(_kryo, serialized, reverseList.getClass());
        assertEquals(deserialized.getClass(), reverseList.getClass());
        assertEquals(deserialized, reverseList);
    }

    // Kryo#copy tests

    @Test( enabled = true )
    public void testCopyEmptyReverseList() {
        testCopy(Lists.reverse(Lists.newLinkedList()));
    }

    @Test( enabled = true )
    public void testCopyEmptyRandomAccessReverseList() {
        testCopy(Lists.reverse(Lists.newArrayList()));
    }

    @Test( enabled = true )
    public void testCopySingletonReverseList() {
        testCopy(Lists.reverse(Lists.newLinkedList(Collections.singleton(3))));
    }

    @Test( enabled = true )
    public void testCopySingletonRandomAccessReverseList() {
        testCopy(Lists.reverse(Lists.newArrayList(Collections.singleton(3))));
    }

    @Test( enabled = true )
    public void testCopyRegularReverseList() {
        testCopy(Lists.reverse(Lists.newLinkedList(Arrays.asList(3, 4, 5, 6))));
    }

    @Test( enabled = true )
    public void testCopyRegularRandomAccessReverseList() {
        testCopy(Lists.reverse(Lists.newArrayList(3, 4, 5, 6)));
    }

    private void testCopy(List<?> obj) {
        final List<?> copied = _kryo.copy(obj);
        assertEquals(copied.getClass(), obj.getClass());
        assertEquals(copied, obj);
    }
}
