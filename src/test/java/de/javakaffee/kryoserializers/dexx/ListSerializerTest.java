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
package de.javakaffee.kryoserializers.dexx;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.github.andrewoma.dexx.collection.ArrayList;
import com.github.andrewoma.dexx.collection.IndexedLists;
import com.github.andrewoma.dexx.collection.List;

/**
 * Test for {@link ListSerializer}
 */
public class ListSerializerTest {

	private Kryo _kryo;

	@BeforeTest
	public void setUp() throws Exception {
		_kryo = new Kryo();

		ListSerializer.registerSerializers(_kryo);
	}

	@Test(enabled = true)
	public void testEmpty() {
		final List<?> obj = IndexedLists.of();
		final byte[] serialized = serialize(_kryo, obj);
		final List<?> deserialized = deserialize(_kryo, serialized, List.class);
		assertTrue(deserialized.isEmpty());
		assertEquals(deserialized.size(), obj.size());
	}

	@Test(enabled = true)
	public void testRegular() {
		final List<?> obj = IndexedLists.of(3, 4, 5, 6, 7);
		final byte[] serialized = serialize(_kryo, obj);
		final List<?> deserialized = deserialize(_kryo, serialized, List.class);
		assertEquals(deserialized, obj);
	}

	@Test(enabled = true)
	public void testCopyOfIterable() {
		final ArrayList<Object> iterable = new ArrayList<Object>();
		iterable.append(new Object());
		final List<?> obj = IndexedLists.copyOf(iterable.asList());
		final byte[] serialized = serialize(_kryo, obj);
		final List<?> deserialized = deserialize(_kryo, serialized, List.class);
		assertEquals(deserialized, obj);
	}

	// Kryo#copy tests

	@Test(enabled = true)
	public void testCopyEmpty() {
		final List<?> obj = IndexedLists.of();
		final List<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test(enabled = true)
	public void testCopyRegular() {
		final List<?> obj = IndexedLists.of(2, 3, 4, 5);
		final List<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test(enabled = true)
	public void testCopyCopyOfIterable() {
		final ArrayList<Object> iterable = new ArrayList<Object>();
		iterable.append(new Object());
		final List<?> obj = IndexedLists.copyOf(iterable.asList());
		List<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}
}
