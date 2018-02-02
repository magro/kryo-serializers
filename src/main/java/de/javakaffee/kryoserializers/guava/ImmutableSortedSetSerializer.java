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

import java.util.Comparator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableSortedSet;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableSortedSet}.
 */
public class ImmutableSortedSetSerializer extends Serializer<ImmutableSortedSet<Object>> {

	private static final boolean DOES_NOT_ACCEPT_NULL = false;
	private static final boolean IMMUTABLE = true;

	public ImmutableSortedSetSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
	}

	/**
	 * Creates a new {@link ImmutableSortedSetSerializer} and registers its serializer
	 * for the several ImmutableSortedSet related classes.
	 *
	 * @param kryo the {@link Kryo} instance to set the serializer on
	 */
	public static void registerSerializers(final Kryo kryo) {

		// ImmutableSortedSet (abstract class)
		//  +- EmptyImmutableSortedSet
		//  +- RegularImmutableSortedSet
		//  +- DescendingImmutableSortedSet

		final ImmutableSortedSetSerializer serializer = new ImmutableSortedSetSerializer();

		kryo.register(ImmutableSortedSet.class, serializer);
		kryo.register(ImmutableSortedSet.of().getClass(), serializer);
		kryo.register(ImmutableSortedSet.of("").getClass(), serializer);
		kryo.register(ImmutableSortedSet.of().descendingSet().getClass(), serializer);
	}

	@Override
	public void write(Kryo kryo, Output output, ImmutableSortedSet<Object> object) {
		kryo.writeClassAndObject(output, object.comparator());
		output.writeInt(object.size(), true);
		for (Object elm : object) {
			kryo.writeClassAndObject(output, elm);
		}
	}

	@Override
	public ImmutableSortedSet<Object> read(Kryo kryo, Input input, Class<ImmutableSortedSet<Object>> type) {
		@SuppressWarnings("unchecked")
		ImmutableSortedSet.Builder<Object> builder =
				ImmutableSortedSet.orderedBy((Comparator<Object>) kryo.readClassAndObject(input));
		final int size = input.readInt(true);
		for (int i = 0; i < size; ++i) {
			builder.add(kryo.readClassAndObject(input));
		}
		return builder.build();
	}
}
