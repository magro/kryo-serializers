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
package de.javakaffee.kryoserializers.jodatime;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;

import org.joda.time.*;
import org.joda.time.chrono.GregorianChronology;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;

/**
 * Tests for {@link JodaLocalTimeSerializer}.
 *
 * @author <a href="mailto:robertpreeves@gmail.com">Rob Reeves</a>
 */
public class JodaLocalTimeSerializerTest {
	private Kryo _kryo;

	@BeforeTest
	protected void beforeTest() {
		_kryo = new Kryo();
		_kryo.register(LocalTime.class, new JodaLocalTimeSerializer());
	}

	@Test(enabled = true)
	public void testJodaLocalTime() {
		final DateTimeZone tz = DateTimeZone.forID("America/Los_Angeles");
		final Chronology chronology = GregorianChronology.getInstance(tz);
		final LocalTime obj = new LocalTime(null, chronology);
		final byte[] serialized = serialize(_kryo, obj);
		final LocalTime deserialized = deserialize(_kryo, serialized, LocalTime.class);
		Assert.assertEquals(deserialized, obj);
	}

	@Test(enabled = true)
	public void testCopyJodaLocalTime() {
		final DateTimeZone tz = DateTimeZone.forID("America/Los_Angeles");
		final Chronology chronology = GregorianChronology.getInstance(tz);
		final LocalTime obj = new LocalTime(52341234, chronology);
		final LocalTime copy = _kryo.copy(obj);
		Assert.assertEquals(copy, obj);
	}
}
