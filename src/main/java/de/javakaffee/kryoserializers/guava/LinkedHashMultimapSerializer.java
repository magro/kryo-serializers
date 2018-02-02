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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.LinkedHashMultimap;

/**
 * A kryo {@link Serializer} for guava-libraries {@link LinkedHashMultimap}.
 * This does not yet support {@link Kryo#copy(java.lang.Object)}.
 */
public class LinkedHashMultimapSerializer
		extends MultimapSerializerBase<Object, Object, LinkedHashMultimap<Object, Object>> {

	private static final boolean DOES_NOT_ACCEPT_NULL = false;

	private static final boolean IMMUTABLE = false;

	public LinkedHashMultimapSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
	}

	/**
	 * Creates a new {@link LinkedHashMultimapSerializer} and registers its serializer.
	 *
	 * @param kryo the {@link Kryo} instance to set the serializer on
	 */
	public static void registerSerializers(final Kryo kryo) {
		final LinkedHashMultimapSerializer serializer = new LinkedHashMultimapSerializer();
		kryo.register(LinkedHashMultimap.class, serializer);
	}

	@Override
	public void write(Kryo kryo, Output output, LinkedHashMultimap<Object, Object> multimap) {
		writeMultimap(kryo, output, multimap);
	}

	@Override
	public LinkedHashMultimap<Object, Object> read(Kryo kryo, Input input, Class<LinkedHashMultimap<Object, Object>> type) {
		final LinkedHashMultimap<Object, Object> multimap = LinkedHashMultimap.create();
		readMultimap(kryo, input, multimap);
		return multimap;
	}
}
