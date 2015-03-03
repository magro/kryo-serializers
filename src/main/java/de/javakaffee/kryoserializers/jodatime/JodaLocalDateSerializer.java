/*
 * Copyright 2015 Martin Grotzke
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
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.EthiopicChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;
import org.joda.time.chrono.JulianChronology;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A Kryo serializer for joda {@link LocalDate}, that reads or writes the millis and chronology
 * corresponding to the LocalDate object as separate attributes. (No time zone is involved.) If the
 * chronology is {@link org.joda.time.chrono.ISOChronology} the attribute is serialized as an empty
 * string, thus {@link org.joda.time.chrono.ISOChronology} is considered to be default.
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
 * </p>
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class JodaLocalDateSerializer extends Serializer<LocalDate> {

   public JodaLocalDateSerializer() {
      setImmutable(true);
   }

   @Override
   public LocalDate read(final Kryo kryo, final Input input, final Class<LocalDate> type) {
      final long millis = input.readLong(true);
      final Chronology chronology = IdentifiableChronology.readChronology(input);
      return new LocalDate( millis, chronology );
   }

   @Override
   public void write(final Kryo kryo, final Output output, final LocalDate localDate) {
      DateTime dateTime = localDate.toDateTimeAtStartOfDay();  // Because getLocalMillis() protected
      output.writeLong(dateTime.getMillis(), true);
      final String chronologyId = IdentifiableChronology.getChronologyId(localDate.getChronology());
      output.writeString(chronologyId == null ? "" : chronologyId);
   }
}
