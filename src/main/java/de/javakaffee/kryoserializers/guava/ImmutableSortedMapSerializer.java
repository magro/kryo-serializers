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
import java.util.Objects;
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
public class ImmutableSortedMapSerializer extends Serializer<ImmutableSortedMap<Object, ?>> {

	private static final boolean DOES_NOT_ACCEPT_NULL = true;
	private static final boolean IMMUTABLE = true;

	public ImmutableSortedMapSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
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

		final DummyComparable k1 = new DummyComparable(1);
		final DummyComparable k2 = new DummyComparable(0);

		final Object v1 = new Object();
		final Object v2 = new Object();

		kryo.register(ImmutableSortedMap.of(k1, v1).getClass(), serializer);
		kryo.register(ImmutableSortedMap.of(k1, v1, k2, v2).getClass(), serializer);

		Map<DummyEnum, Object> enumMap = new EnumMap<>(DummyEnum.class);
		for (DummyEnum e : DummyEnum.values()) {
			enumMap.put(e, v1);
		}

		kryo.register(ImmutableSortedMap.copyOf(enumMap).getClass(), serializer);
	}

	@Override
	public void write(Kryo kryo, Output output, ImmutableSortedMap<Object, ?> immutableMap) {
		kryo.writeObject(output, Maps.newTreeMap(immutableMap));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ImmutableSortedMap<Object, Object> read(Kryo kryo, Input input, Class<ImmutableSortedMap<Object, ?>> type) {
		Map map = kryo.readObject(input, TreeMap.class);
		return ImmutableSortedMap.copyOf(map);
	}

	private enum DummyEnum {
		VALUE_1,
		VALUE_2
	}

	private static class DummyComparable implements Comparable<DummyComparable> {
		private final int ordering;

		private DummyComparable(int ordering) {
			this.ordering = ordering;
		}

		@Override
		public int compareTo(DummyComparable o) {
			return Integer.compare(ordering, o.ordering);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			DummyComparable that = (DummyComparable) o;
			return ordering == that.ordering;
		}

		@Override
		public int hashCode() {
			return Objects.hash(ordering);
		}
	}
}
