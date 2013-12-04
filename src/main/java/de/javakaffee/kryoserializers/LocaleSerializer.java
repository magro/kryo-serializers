/*
 * Copyright 2013 Martin Grotzke
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
 */
package de.javakaffee.kryoserializers;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Locale;

import com.esotericsoftware.kryo.serialize.SimpleSerializer;
import com.esotericsoftware.kryo.serialize.StringSerializer;

/**
 * A kryo serializer for {@link Locale}.
 * <p>
 * For deserialization by default (and if available) the static {@link Locale#getInstance(String, String, String)}
 * is used via reflection to make use of the Locale cache.<br/>
 * To use the {@link Locale#Locale(String, String, String) Locale(String, String, String)} constructor instead the
 * {@link LocaleSerializer#LocaleSerializer(boolean) LocaleSerializer(boolean)} has to be used with <code>useReflection</code>
 * set to <code>false</code>.
 * </p>
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class LocaleSerializer extends SimpleSerializer<Locale> {
	
	private static Method getInstance;
	
	static {
		try {
			getInstance = Locale.class.getDeclaredMethod("getInstance", String.class, String.class, String.class);
			getInstance.setAccessible(true);
		} catch (final Exception e) {
			if (TRACE) trace("kryo", "java.util.Locale.getInstance is not available");
			getInstance = null;
		}
	}
	
	private final boolean useReflection;
	
	/**
	 * Creates a new instance with {@link #useReflection} set to <code>true</code>.
	 * 
	 * @see #LocaleSerializer(boolean)
	 */
	public LocaleSerializer() {
		this(true);
	}

	/**
	 * Creates a new instance and activates the usage of reflection (for getting the
	 * {@link Locale} during deserialization) if <code>true</code> was provided for <code>useReflection</code>
	 * and if the static method {@link Locale#getInstance(String, String, String)} is available.
	 *
	 * @param useReflection asks to activate reflection usage.
	 */
	public LocaleSerializer(final boolean useReflection) {
		this.useReflection = getInstance != null && useReflection;
	}

	@Override
	public Locale read(final ByteBuffer buffer) {
		final String language = StringSerializer.get(buffer);
		final String country = StringSerializer.get(buffer);
		final String variant = StringSerializer.get(buffer);
		if(useReflection) {
			try {
				return (Locale) getInstance.invoke(null, language, country, variant);
			} catch (final Exception e) {
				throw new RuntimeException("Could not get Locale using Locale.getInstance", e);
			}
		}
		return new Locale(language, country, variant);
	}

	@Override
	public void write(final ByteBuffer buffer, final Locale object) {
		StringSerializer.put(buffer, object.getLanguage());
		StringSerializer.put(buffer, object.getCountry());
		StringSerializer.put(buffer, object.getVariant());
	}

}
