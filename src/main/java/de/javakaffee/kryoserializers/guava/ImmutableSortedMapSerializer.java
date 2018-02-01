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

import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableSortedMap}.
 */
public class ImmutableSortedMapSerializer extends Serializer<ImmutableSortedMap<Object, ? extends Object>> {

	private static final boolean DOES_NOT_ACCEPT_NULL = true;
	private static final boolean IMMUTABLE = true;

	public ImmutableSortedMapSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
	}

	@Override
	public void write(Kryo kryo, Output output, ImmutableSortedMap<Object, ? extends Object> immutableMap) {
		kryo.writeObject(output, Maps.newTreeMap(immutableMap));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ImmutableSortedMap<Object, Object> read(Kryo kryo, Input input, Class<ImmutableSortedMap<Object, ? extends Object>> type) {
		Map map = kryo.readObject(input, TreeMap.class);
		return ImmutableSortedMap.copyOf(map);
	}

	/**
	 * Creates a new {@link ImmutableSortedMapSerializer} and registers its serializer
	 * for the several ImmutableMap related classes.
	 *
	 * @param kryo the {@link Kryo} instance to set the serializer on
	 */
	public static void registerSerializers(final Kryo kryo) {

		final ImmutableSortedMapSerializer serializer = new ImmutableSortedMapSerializer();

		kryo.register(ImmutableSortedMap.class, serializer);
		kryo.register(ImmutableSortedMap.of().getClass(), serializer);

		final Comparable<Object> k1 = new Comparable<Object>() {
			@Override
			public int compareTo(Object o) {
				return o == this ? 0 : -1;
			}
		};
		final Comparable<Object> k2 = new Comparable<Object>() {
			@Override
			public int compareTo(Object o) {
				return o == this ? 0 : 1;
			}
		};
		final Object v1 = new Object();
		final Object v2 = new Object();

		kryo.register(ImmutableSortedMap.of(k1, v1).getClass(), serializer);
		kryo.register(ImmutableSortedMap.of(k1, v1, k2, v2).getClass(), serializer);

		Map<DummyEnum, Object> enumMap = new EnumMap<DummyEnum, Object>(DummyEnum.class);
		for (DummyEnum e : DummyEnum.values()) {
			enumMap.put(e, v1);
		}

		kryo.register(ImmutableSortedMap.copyOf(enumMap).getClass(), serializer);
	}

	private enum DummyEnum {
		VALUE1,
		VALUE2
	}
}
