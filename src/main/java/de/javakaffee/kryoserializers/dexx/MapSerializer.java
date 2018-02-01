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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.andrewoma.dexx.collection.Map;
import com.github.andrewoma.dexx.collection.Maps;
import com.github.andrewoma.dexx.collection.Pair;

/**
* A kryo {@link Serializer} for dexx {@link Map}
 */
public class MapSerializer extends Serializer<Map<Object, ?>> {

	private static final boolean DOES_NOT_ACCEPT_NULL = true;
	private static final boolean IMMUTABLE = true;

	public MapSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
	}

	@Override
	public void write(Kryo kryo, Output output, Map<Object, ?> immutableMap) {
		kryo.writeObject(output, immutableMap.asMap());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Object, Object> read(Kryo kryo, Input input, Class<Map<Object, ?>> type) {
		HashMap<Object, Object> map = kryo.readObject(input, HashMap.class);
		ArrayList<Pair<Object, Object>> listOfPairs = new ArrayList();

		for (Entry<Object, Object> entry : map.entrySet()) {
			Pair pair = new Pair(entry.getKey(), entry.getValue());
			listOfPairs.add(pair);
		}

		return Maps.copyOf(listOfPairs);
	}

	/**
	 * Creates a new {@link de.javakaffee.kryoserializers.guava.ImmutableMapSerializer} and registers its serializer
	 * for the several ImmutableMap related classes.
	 *
	 * @param kryo the {@link Kryo} instance to set the serializer on
	 */
	public static void registerSerializers(final Kryo kryo) {

		final MapSerializer serializer = new MapSerializer();

		kryo.register(Map.class, serializer);
		kryo.register(Maps.of().getClass(), serializer);

		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();
		Object o5 = new Object();

		kryo.register(Maps.of(o1, o1).getClass(), serializer);
		kryo.register(Maps.of(o1, o1, o2, o2).getClass(), serializer);
		kryo.register(Maps.of(o1, o1, o2, o2, o3, o3).getClass(), serializer);
		kryo.register(Maps.of(o1, o1, o2, o2, o3, o3, o4, o4).getClass(), serializer);
		kryo.register(Maps.of(o1, o1, o2, o2, o3, o3, o4, o4, o5, o5).getClass(), serializer);

	}
}
