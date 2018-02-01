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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Test for {@link ImmutableSortedSetSerializer}.
 */
public class ImmutableSortedSetSerializerTest {

	private Kryo _kryo;

	@BeforeTest
	public void setUp() throws Exception {
		_kryo = new Kryo();

		ImmutableSortedSetSerializer.registerSerializers(_kryo);
	}

	@Test
	public void testEmpty() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of();
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSortedSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSortedSet.class);
		assertTrue(deserialized.isEmpty());
		assertEquals(deserialized.size(), obj.size());
	}

	@Test
	public void testSingleton() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of(3);
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSortedSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSortedSet.class);
		assertEquals(deserialized, obj);
	}

	@Test
	public void testDescending() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of(3, 4, 5, 6).descendingSet();
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSortedSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSortedSet.class);
		assertEquals(deserialized, obj);
	}

	@Test
	public void testRegular() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of(3, 4, 5, 6);
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSortedSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSortedSet.class);
		assertEquals(deserialized, obj);
	}

	@Test
	public void testStringAsImmutableSortedSet() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of("K", "r", "y", "o");
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSortedSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSortedSet.class);
		assertEquals(deserialized, obj);
	}

	// Kryo#copy tests

	@Test
	public void testCopyEmpty() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of();
		final ImmutableSortedSet<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test
	public void testCopySingleton() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of(1);
		final ImmutableSortedSet<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test
	public void testCopyDescending() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of(1, 2, 3).descendingSet();
		final ImmutableSortedSet<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test
	public void testCopyRegular() {
		final ImmutableSortedSet<?> obj = ImmutableSortedSet.of(1, 2, 3);
		final ImmutableSortedSet<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}
}
