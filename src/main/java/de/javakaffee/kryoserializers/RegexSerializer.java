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

import java.util.regex.Pattern;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Kryo {@link Serializer} for regex {@link Pattern}s.
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 * @author serverperformance
 */
public class RegexSerializer extends Serializer<Pattern> {

	public RegexSerializer() {
		setImmutable(true);
	}

	@Override
	public void write(final Kryo kryo, final Output output, final Pattern pattern) {
		output.writeString(pattern.pattern());
		output.writeInt(pattern.flags(), true);
	}

	@Override
	public Pattern read(final Kryo kryo, final Input input, final Class<Pattern> patternClass) {
		String regex = input.readString();
		int flags = input.readInt(true);
		return Pattern.compile(regex, flags);
	}
}
