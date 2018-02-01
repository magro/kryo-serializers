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
package de.javakaffee.kryoserializers;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.lang.Character.UnicodeBlock;

import org.objenesis.ObjenesisStd;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;

/**
 * Test for {@link UnicodeBlockSerializer}.
 *
 * @author <a href="mailto:seahen123@gmail.com">Chris Hennick</a>
 */
public class UnicodeBlockSerializerTest {

	private static final String NONEXISTENT_BLOCK_NAME = "RURITANIAN";
	private Kryo kryo;

	private static class ThingWithUnicodeBlock {
		final UnicodeBlock unicodeBlock;

		private ThingWithUnicodeBlock(UnicodeBlock unicodeBlock) {
			this.unicodeBlock = unicodeBlock;
		}
	}

	@BeforeTest
	protected void beforeTest() {
		kryo = new Kryo();
		final DefaultInstantiatorStrategy instantiatorStrategy = new DefaultInstantiatorStrategy();
		instantiatorStrategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
		kryo.setInstantiatorStrategy(instantiatorStrategy);
		kryo.register(UnicodeBlock.class, new UnicodeBlockSerializer());
	}

	@Test
	public void testBasicRoundTrip() {
		byte[] serialized = serialize(kryo, UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS);
		assertSame(deserialize(kryo, serialized, UnicodeBlock.class),
				UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS);
	}

	@Test
	public void testDeserializingUnknownInstanceReturnsNull() {
		byte[] serialized = serialize(kryo, new ObjenesisStd().newInstance(UnicodeBlock.class));
		assertNull(deserialize(kryo, serialized, UnicodeBlock.class));
	}

	@Test
	public void testCopyContainingObject() {
		ThingWithUnicodeBlock original = new ThingWithUnicodeBlock(UnicodeBlock.GREEK);
		assertSame(kryo.copy(original).unicodeBlock, UnicodeBlock.GREEK);
	}
}
