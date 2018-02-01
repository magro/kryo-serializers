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

import java.util.EnumMap;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.ImmutableMap;

/**
 * Created by pmarcos on 29/06/15.
 */
public class ImmutableMapSerializerTest {

	private enum Planet {
		MERCURY,
		VENUS,
		EARTH,
		MARS;
	}

	private Kryo _kryo;

	@BeforeTest
	public void setUp() throws Exception {
		_kryo = new Kryo();

		ImmutableMapSerializer.registerSerializers(_kryo);
	}

	@Test
	public void testEmpty() {
		final ImmutableMap<?, ?> obj = ImmutableMap.of();
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
		assertTrue(deserialized.isEmpty());
		assertEquals(deserialized.size(), obj.size());
	}

	@Test
	public void testSingleton() {
		final ImmutableMap<?, ?> obj = ImmutableMap.of(3, "k");
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
		assertEquals(deserialized, obj);
	}

	@Test
	public void testRegular() {
		final ImmutableMap<?, ?> obj = ImmutableMap.of(3, "k", 5, "r", 6, "y");
		final byte[] serialized = serialize(_kryo, obj);
		final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
		assertEquals(deserialized, obj);
	}

	@Test
	public void testEnum() {
		final EnumMap<Planet, String> obj = new EnumMap<Planet, String>(Planet.class);
		for (Planet p : Planet.values()) {
			obj.put(p, p.name());
		}

		final ImmutableMap<?, ?> immutableObj = ImmutableMap.copyOf(obj);
		final byte[] serialized = serialize(_kryo, immutableObj);
		final ImmutableMap<?, ?> deserialized = deserialize(_kryo, serialized, ImmutableMap.class);
		assertEquals(deserialized, immutableObj);
	}

	// Kryo#copy tests

	@Test
	public void testCopyEmpty() {
		final ImmutableMap<?, ?> obj = ImmutableMap.of();
		final ImmutableMap<?, ?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test
	public void testCopySingleton() {
		final ImmutableMap<?, ?> obj = ImmutableMap.of(1, 1);
		final ImmutableMap<?, ?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

	@Test
	public void testCopyRegular() {
		final ImmutableMap<?, ?> obj = ImmutableMap.of(1, 2, 3, 4);
		final ImmutableMap<?, ?> copied = _kryo.copy(obj);
		assertSame(copied, obj);
	}

}
