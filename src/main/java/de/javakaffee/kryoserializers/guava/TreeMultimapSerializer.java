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
import com.google.common.collect.TreeMultimap;

/**
 * A kryo {@link Serializer} for guava-libraries {@link TreeMultimap}.
 * The default comparator is assumed so the multimaps are not null-safe.
 * This does not yet support {@link Kryo#copy(java.lang.Object)}.
 */
public class TreeMultimapSerializer
		extends MultimapSerializerBase<Comparable, Comparable, TreeMultimap<Comparable, Comparable>> {

	/* assumes default comparator */
	private static final boolean DOES_NOT_ACCEPT_NULL = true;

	private static final boolean IMMUTABLE = false;

	public TreeMultimapSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
	}

	/**
	 * Creates a new {@link TreeMultimapSerializer} and registers its serializer.
	 *
	 * @param kryo the {@link Kryo} instance to set the serializer on
	 */
	public static void registerSerializers(final Kryo kryo) {
		final TreeMultimapSerializer serializer = new TreeMultimapSerializer();
		kryo.register(TreeMultimap.class, serializer);
	}

	@Override
	public void write(Kryo kryo, Output output, TreeMultimap<Comparable, Comparable> multimap) {
		writeMultimap(kryo, output, multimap);
	}

	@Override
	public TreeMultimap<Comparable, Comparable> read(Kryo kryo, Input input, Class<TreeMultimap<Comparable, Comparable>> type) {
		final TreeMultimap<Comparable, Comparable> multimap = TreeMultimap.create();
		readMultimap(kryo, input, multimap);
		return multimap;
	}
}
