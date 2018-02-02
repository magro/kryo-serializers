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

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ImmutableSet;

/**
 * Test for {@link ImmutableSetSerializer}.
 */
public class ImmutableSetSerializerTest {

	private Kryo _kryo;

	@BeforeTest
	public void setUp() throws Exception {
		_kryo = new Kryo();

		ImmutableSetSerializer.registerSerializers(_kryo);
	}

	@Test
	public void testEmpty() {
		final ImmutableSet<?> obj = ImmutableSet.of();
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSet.class);
		assertTrue(deserialized.isEmpty());
		assertEquals(deserialized.size(), obj.size());
	}

	@Test
	public void testSingleton() {
		final ImmutableSet<?> obj = ImmutableSet.of(3);
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSet.class);
		assertEquals(deserialized, obj);
	}

	@Test
	public void testRegular() {
		final ImmutableSet<?> obj = ImmutableSet.of(3, 4, 5, 6);
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSet.class);
		assertEquals(deserialized, obj);
	}

	@Test
	public void testStringAsImmutableSet() {
		final ImmutableSet<?> obj = ImmutableSet.of("K", "r", "y", "o");
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableSet<?> deserialized = deserialize(_kryo, serialized, ImmutableSet.class);
		assertEquals(deserialized, obj);
	}

	// Kryo#copy tests

	@Test
	public void testCopyEmpty() {
		final ImmutableSet<?> obj = ImmutableSet.of();
		final ImmutableSet<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test
	public void testCopySingleton() {
		final ImmutableSet<?> obj = ImmutableSet.of(1);
		final ImmutableSet<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test
	public void testCopyRegular() {
		final ImmutableSet<?> obj = ImmutableSet.of(1, 2, 3);
		final ImmutableSet<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

}
