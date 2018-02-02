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

import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Multimap;

public abstract class MultimapSerializerBase<K, V, T extends Multimap<K, V>> extends Serializer<T> {

	public MultimapSerializerBase(boolean acceptsNull, boolean immutable) {
		super(acceptsNull, immutable);
	}

	protected void writeMultimap(Kryo kryo, Output output, Multimap<K, V> multimap) {
		output.writeInt(multimap.size(), true);
		for (final Map.Entry<K, V> entry : multimap.entries()) {
			kryo.writeClassAndObject(output, entry.getKey());
			kryo.writeClassAndObject(output, entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	protected void readMultimap(Kryo kryo, Input input, Multimap<K, V> multimap) {
		final int size = input.readInt(true);
		for (int i = 0; i < size; ++i) {
			final K key = (K) kryo.readClassAndObject(input);
			final V value = (V) kryo.readClassAndObject(input);
			multimap.put(key, value);
		}
	}
}
