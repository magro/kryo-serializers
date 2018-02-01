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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.google.common.collect.HashMultimap;

import de.javakaffee.kryoserializers.KryoTest;

public class HashMultimapSerializerTest extends MultimapSerializerTestBase {

	private Kryo _kryo;

	@BeforeClass
	public void initializeKyroWithSerializer() {
		_kryo = new Kryo();
		HashMultimapSerializer.registerSerializers(_kryo);
	}

	@Test(dataProvider = "Google Guava multimaps")
	public void testMultimap(Object[] contents) {
		final HashMultimap<Object, Object> multimap = HashMultimap.create();
		populateMultimap(multimap, contents);
		final byte[] serialized = KryoTest.serialize(_kryo, multimap);
		final HashMultimap<Object, Object> deserialized = KryoTest.deserialize(_kryo, serialized, HashMultimap.class);
		assertEqualMultimaps(false, false, deserialized, multimap);
	}
}
