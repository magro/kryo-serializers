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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A kryo {@link Serializer} for guava-libraries {@link ImmutableSet}.
 * <p>
 * (the array.clone as done by copyOf is saved), for more elements copyOf seems to get more efficient.
 *
 * @implNote ImmutableSet builder would be a little bit more efficient for less than five elements
 */
public class ImmutableSetSerializer extends Serializer<ImmutableSet<Object>> {

	private static final boolean DOES_NOT_ACCEPT_NULL = false;
	private static final boolean IMMUTABLE = true;

	public ImmutableSetSerializer() {
		super(DOES_NOT_ACCEPT_NULL, IMMUTABLE);
	}

	/**
	 * Creates a new {@link ImmutableSetSerializer} and registers its serializer
	 * for the several ImmutableSet related classes.
	 *
	 * @param kryo the {@link Kryo} instance to set the serializer on
	 */
	public static void registerSerializers(final Kryo kryo) {

		// ImmutableList (abstract class)
		//  +- EmptyImmutableSet
		//  |   EmptyImmutableSet
		//  +- SingletonImmutableSet
		//  |   Optimized for Set with only 1 element.
		//  +- RegularImmutableSet
		//  |   RegularImmutableList
		//  +- EnumImmutableSet
		//  |   EnumImmutableSet

		final ImmutableSetSerializer serializer = new ImmutableSetSerializer();

		kryo.register(ImmutableSet.class, serializer);

		// Note:
		//  Only registering above is good enough for serializing/deserializing.
		//  but if using Kryo#copy, following is required.

		kryo.register(ImmutableSet.of().getClass(), serializer);
		kryo.register(ImmutableSet.of(1).getClass(), serializer);
		kryo.register(ImmutableSet.of(1, 2, 3).getClass(), serializer);

		kryo.register(Sets.immutableEnumSet(SomeEnum.A, SomeEnum.B, SomeEnum.C).getClass(), serializer);
	}

	@Override
	public void write(Kryo kryo, Output output, ImmutableSet<Object> object) {
		output.writeInt(object.size(), true);
		for (Object elm : object) {
			kryo.writeClassAndObject(output, elm);
		}
	}

	@Override
	public ImmutableSet<Object> read(Kryo kryo, Input input, Class<ImmutableSet<Object>> type) {
		final int size = input.readInt(true);
		ImmutableSet.Builder<Object> builder = ImmutableSet.builder();
		for (int i = 0; i < size; ++i) {
			builder.add(kryo.readClassAndObject(input));
		}
		return builder.build();
	}

	private enum SomeEnum {
		A,
		B,
		C
	}
}
