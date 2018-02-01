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
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableMap}.
 */
public class ImmutableMapSerializer extends Serializer<ImmutableMap<Object, ?>> {

	private static final boolean DOES_NOT_ACCEPT_NULL = true;
	private static final boolean IMMUTABLE = true;

	public ImmutableMapSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
	}

	@Override
	public void write(Kryo kryo, Output output, ImmutableMap<Object, ?> immutableMap) {
		kryo.writeObject(output, Maps.newHashMap(immutableMap));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ImmutableMap<Object, Object> read(Kryo kryo, Input input, Class<ImmutableMap<Object, ?>> type) {
		Map map = kryo.readObject(input, HashMap.class);
		return ImmutableMap.copyOf(map);
	}

	/**
	 * Creates a new {@link ImmutableMapSerializer} and registers its serializer
	 * for the several ImmutableMap related classes.
	 *
	 * @param kryo the {@link Kryo} instance to set the serializer on
	 */
	public static void registerSerializers(final Kryo kryo) {

		final ImmutableMapSerializer serializer = new ImmutableMapSerializer();

		kryo.register(ImmutableMap.class, serializer);
		kryo.register(ImmutableMap.of().getClass(), serializer);

		Object o1 = new Object();
		Object o2 = new Object();

		kryo.register(ImmutableMap.of(o1, o1).getClass(), serializer);
		kryo.register(ImmutableMap.of(o1, o1, o2, o2).getClass(), serializer);

		Map<DummyEnum, Object> enumMap = new EnumMap<DummyEnum, Object>(DummyEnum.class);
		for (DummyEnum e : DummyEnum.values()) {
			enumMap.put(e, o1);
		}

		kryo.register(ImmutableMap.copyOf(enumMap).getClass(), serializer);
	}

	private enum DummyEnum {
		VALUE1,
		VALUE2
	}
}
