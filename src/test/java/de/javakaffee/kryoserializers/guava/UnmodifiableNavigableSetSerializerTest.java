/*
 * Copyright 2018 Martin Grotzke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.javakaffee.kryoserializers.guava;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.*;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.Sets;

/**
 * Test for {@link ImmutableSortedSetSerializer}.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UnmodifiableNavigableSetSerializerTest {

	Class<NavigableSet> unmodifiableClass;
	UnmodifiableNavigableSetSerializer forUnwrapping = new UnmodifiableNavigableSetSerializer();
	private Kryo _kryo;

	{
		unmodifiableClass = (Class<NavigableSet>) Sets.unmodifiableNavigableSet(new TreeSet()).getClass();
	}

	@BeforeTest
	public void setUp() {
		_kryo = new Kryo();

		UnmodifiableNavigableSetSerializer.registerSerializers(_kryo);
	}

	private void assertUnderlyingSet(NavigableSet<String> deserialized, Class<?> class1) {
		assertEquals(forUnwrapping.getDelegateFromUnmodifiableNavigableSet(deserialized).getClass(), class1,
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
		} catch (UnsupportedOperationException expected) {
		}
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
		} catch (UnsupportedOperationException expected) {
		}
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
		} catch (UnsupportedOperationException expected) {
		}
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
		} catch (UnsupportedOperationException expected) {
		}
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
		} catch (UnsupportedOperationException expected) {
		}
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
		} catch (UnsupportedOperationException expected) {
		}
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
		} catch (UnsupportedOperationException expected) {
		}
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
		} catch (UnsupportedOperationException expected) {
		}
		assertUnderlyingSet(copied, coreSet.getClass());
	}
}
