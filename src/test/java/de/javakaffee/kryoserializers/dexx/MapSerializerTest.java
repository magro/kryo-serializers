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
import com.github.andrewoma.dexx.collection.Map;
import com.github.andrewoma.dexx.collection.Maps;

/**
* Test for {@link MapSerializer}
 */
public class MapSerializerTest {

	private Kryo _kryo;

	@BeforeTest
	public void setUp() throws Exception {
		_kryo = new Kryo();

		MapSerializer.registerSerializers(_kryo);
	}

	@Test(enabled = true)
	public void testEmpty() {
		final Map<?, ?> obj = Maps.of();
		final byte[] serialized = serialize(_kryo, obj);
		final Map<?, ?> deserialized = deserialize(_kryo, serialized, Map.class);
		assertTrue(deserialized.isEmpty());
		assertEquals(deserialized.size(), obj.size());
	}

	@Test(enabled = true)
	public void testRegular() {
		final Map<?, ?> obj = Maps.of(3, "k", 5, "r", 6, "y");
		final byte[] serialized = serialize(_kryo, obj);
		final Map<?, ?> deserialized = deserialize(_kryo, serialized, Map.class);
		assertEquals(deserialized, obj);
	}

	// Kryo#copy tests

	@Test(enabled = true)
	public void testCopyEmpty() {
		final Map<?, ?> obj = Maps.of();
		final Map<?, ?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test(enabled = true)
	public void testCopyRegular() {
		final Map<?, ?> obj = Maps.of(1, 2, 3, 4);
		final Map<?, ?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}
}
