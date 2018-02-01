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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableMultimap}.
 */
public class ImmutableMultimapSerializer extends Serializer<ImmutableMultimap<Object, Object>> {

	private static final boolean DOES_NOT_ACCEPT_NULL = true;
	private static final boolean IMMUTABLE = true;

	public ImmutableMultimapSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
	}

	@Override
	public void write(Kryo kryo, Output output, ImmutableMultimap<Object, Object> immutableMultiMap) {
		kryo.writeObject(output, ImmutableMap.copyOf(immutableMultiMap.asMap()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ImmutableMultimap<Object, Object> read(Kryo kryo, Input input, Class<ImmutableMultimap<Object, Object>> type) {
		final ImmutableMultimap.Builder builder;
		if (type.equals(ImmutableListMultimap.class)) {
			builder = ImmutableMultimap.builder();
		} else if (type.equals(ImmutableSetMultimap.class)) {
			builder = ImmutableSetMultimap.builder();
		} else {
			builder = ImmutableMultimap.builder();
		}

		final Map map = kryo.readObject(input, ImmutableMap.class);
		final Set<Map.Entry<Object, List<?>>> entries = map.entrySet();

		for (Map.Entry<Object, List<?>> entry : entries) {
			builder.putAll(entry.getKey(), entry.getValue());
		}

		return builder.build();
	}

	/**
	 * Creates a new {@link ImmutableMultimapSerializer} and registers its serializer
	 * for the several ImmutableMultimap related classes.
	 *
	 * @param kryo the {@link Kryo} instance to set the serializer on
	 */
	public static void registerSerializers(final Kryo kryo) {
		// ImmutableMap is used by ImmutableMultimap. However,
		// we already have a separate serializer class for ImmutableMap,
		// ImmutableMapSerializer. If it is not already being used, register it.
		Serializer immutableMapSerializer = kryo.getSerializer(ImmutableMap.class);
		if (!(immutableMapSerializer instanceof ImmutableMapSerializer)) {
			ImmutableMapSerializer.registerSerializers(kryo);
		}

		// ImmutableList is used by ImmutableListMultimap. However,
		// we already have a separate serializer class for ImmutableList,
		// ImmutableListSerializer. If it is not already being used, register it.
		Serializer immutableListSerializer = kryo.getSerializer(ImmutableList.class);
		if (!(immutableListSerializer instanceof ImmutableListSerializer)) {
			ImmutableListSerializer.registerSerializers(kryo);
		}

		// ImmutableSet is used by ImmutableSetMultimap. However,
		// we already have a separate serializer class for ImmutableSet,
		// ImmutableSetSerializer. If it is not already being used, register it.
		Serializer immutableSetSerializer = kryo.getSerializer(ImmutableSet.class);
		if (!(immutableSetSerializer instanceof ImmutableSetSerializer)) {
			ImmutableSetSerializer.registerSerializers(kryo);
		}

		final ImmutableMultimapSerializer serializer = new ImmutableMultimapSerializer();

		// ImmutableMultimap (abstract class)
		//  +- EmptyImmutableListMultimap
		//  +- ImmutableListMultimap
		//  +- EmptyImmutableSetMultimap
		//  +- ImmutableSetMultimap

		kryo.register(ImmutableMultimap.class, serializer);
		kryo.register(ImmutableListMultimap.of().getClass(), serializer);
		kryo.register(ImmutableListMultimap.of("A", "B").getClass(), serializer);
		kryo.register(ImmutableSetMultimap.of().getClass(), serializer);
		kryo.register(ImmutableSetMultimap.of("A", "B").getClass(), serializer);
	}
}
