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
package de.javakaffee.kryoserializers;

import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A kryo {@link Serializer} for fields of type {@link UnicodeBlock}, which is effectively but not
 * actually an enum.
 *
 * @author <a href="mailto:seahen123@gmail.com">Chris Hennick</a>
 */
public class UnicodeBlockSerializer extends Serializer<UnicodeBlock> {
	private static final IdentityHashMap<UnicodeBlock, String> BLOCK_NAMES =
			new IdentityHashMap<UnicodeBlock, String>();

	static {
		// Reflectively look up the instances and their names, which are in UnicodeBlock's static
		// fields (necessary since UnicodeBlock isn't an actual enum)
		for (Field field : UnicodeBlock.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				try {
					// For some reason, UnicodeBlock constants aren't already accessible, even
					// though they're public! WTF?
					field.setAccessible(true);
					Object value = field.get(null);
					if (value instanceof UnicodeBlock) {
						BLOCK_NAMES.put((UnicodeBlock) value, field.getName());
					}
				} catch (IllegalAccessException e) {
					// Should never happen
					throw new InternalError();
				}
			}
		}
	}

	public UnicodeBlockSerializer() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(final Kryo kryo, final Output output, final UnicodeBlock obj) {
		output.writeAscii(BLOCK_NAMES.get(obj));
	}

	/**
	 * Returns {@code original}; see {@link com.esotericsoftware.kryo.serializers.DefaultSerializers.EnumSerializer#copy}
	 * for why we behave this way.
	 */
	@Override
	public UnicodeBlock copy(final Kryo kryo, final UnicodeBlock original) {
		return original;
	}

	@Override
	public UnicodeBlock read(final Kryo kryo, final Input input, final Class<UnicodeBlock> unicodeBlockClass) {
		String name = input.readString();
		return (name == null) ? null : UnicodeBlock.forName(name);
	}
}
