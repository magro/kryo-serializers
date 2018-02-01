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
import com.github.andrewoma.dexx.collection.Set;
import com.github.andrewoma.dexx.collection.Sets;

/**
 * Test for {@link SetSerializer}.
 */
public class SetSerializerTest {

	private Kryo _kryo;

	@BeforeTest
	public void setUp() throws Exception {
		_kryo = new Kryo();

		SetSerializer.registerSerializers(_kryo);
	}

	@Test(enabled = true)
	public void testEmpty() {
		final Set<?> obj = Sets.of();
		final byte[] serialized = serialize(_kryo, obj);
		final Set<?> deserialized = deserialize(_kryo, serialized, Set.class);
		assertTrue(deserialized.isEmpty());
		assertEquals(deserialized.size(), obj.size());
	}

	@Test(enabled = true)
	public void testRegular() {
		final Set<?> obj = Sets.of(3, 4, 5, 6);
		final byte[] serialized = serialize(_kryo, obj);
		final Set<?> deserialized = deserialize(_kryo, serialized, Set.class);
		assertEquals(deserialized, obj);
	}

	// Kryo#copy tests

	@Test(enabled = true)
	public void testCopyEmpty() {
		final Set<?> obj = Sets.of();
		final Set<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test(enabled = true)
	public void testCopyRegular() {
		final Set<?> obj = Sets.of(1, 2, 3);
		final Set<?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}
}
