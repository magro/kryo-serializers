/*
 * Copyright 2015 Rennie Petersen
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
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.EthiopicChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;
import org.joda.time.chrono.JulianChronology;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.Serializer;

/**
 * A Kryo serializer for joda {@link LocalDateTime}. The LocalDateTime object is read or written as
 * year, month-of-year, day-of-month and millis-of-day packed into a long integer, and chronology as
 * a separate attribute. No time zone is involved. If the chronology is {@link ISOChronology} the
 * attribute is serialized as an empty string, thus {@link ISOChronology} is considered to be
 * default.
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
 * @author <a href="mailto:rp@merlinia.com">Rennie Petersen</a>
 */
public class JodaLocalDateTimeSerializer extends Serializer<LocalDateTime> {

   public JodaLocalDateTimeSerializer() { setImmutable(true); }

   @Override
   public LocalDateTime read(Kryo kryo, Input input, Class<LocalDateTime> type) {
      final long packedLocalDateTime = input.readLong(true);
      final int packedYearMonthDay = (int)(packedLocalDateTime / 86400000);
      final int millisOfDay = (int)(packedLocalDateTime % 86400000);
      final Chronology chronology = IdentifiableChronology.readChronology(input);
      return new LocalDateTime(packedYearMonthDay / (13 * 32),
                               (packedYearMonthDay % (13 * 32)) / 32,
                               packedYearMonthDay % 32,
                               millisOfDay / 3600000,
                               (millisOfDay % 3600000) / 60000,
                               (millisOfDay % 60000) / 1000,
                               millisOfDay % 1000,
                               chronology );
   }

   @Override
   public void write(Kryo kryo, Output output, LocalDateTime localDateTime) {
      final int packedYearMonthDay = localDateTime.getYear() * 13 * 32 +
                                     localDateTime.getMonthOfYear() * 32 +
                                     localDateTime.getDayOfMonth();
      output.writeLong((long)packedYearMonthDay * 86400000 + localDateTime.getMillisOfDay(), true);
      final String chronologyId =
                           IdentifiableChronology.getChronologyId(localDateTime.getChronology());
      output.writeString(chronologyId == null ? "" : chronologyId);
   }
}
