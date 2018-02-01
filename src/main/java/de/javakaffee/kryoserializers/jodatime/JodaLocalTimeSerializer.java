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

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.chrono.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A format for Joda {@link LocalTime}, that stores the milliseconds of the day and chronology
 * as separate attributes.
 * <p>
 * The following chronologies are supported:
 * <ul>
 * <li>{@link ISOChronology}</li>
 * <li>{@link CopticChronology}</li>
 * <li>{@link EthiopicChronology}</li>
 * <li>{@link GregorianChronology}</li>
 * <li>{@link JulianChronology}</li>
 * <li>{@link IslamicChronology}</li>
 * <li>{@link BuddhistChronology}</li>
 * <li>{@link GJChronology}</li>
 * </ul>
 *
 * @author <a href="mailto:robertpreeves@gmail.com">Rob Reeves</a>
 */
public class JodaLocalTimeSerializer extends Serializer<LocalTime> {
	@Override
	public void write(Kryo kryo, Output output, LocalTime object) {
		final int time = object.getMillisOfDay();
		output.writeInt(time, true);

		//LocalTime always converts the internal DateTimeZone to UTC so there is no need to serialize it.
		final String chronologyId = IdentifiableChronology.getChronologyId(object.getChronology());
		output.writeString(chronologyId);
	}

	@Override
	public LocalTime read(Kryo kryo, Input input, Class<LocalTime> type) {
		final int time = input.readInt(true);
		final Chronology chronology = IdentifiableChronology.readChronology(input);

		//LocalTime always converts the internal DateTimeZone to UTC.
		return new LocalTime(time, chronology.withZone(DateTimeZone.UTC));
	}

	@Override
	public LocalTime copy(Kryo kryo, LocalTime original) {
		return new LocalTime(original);
	}
}
