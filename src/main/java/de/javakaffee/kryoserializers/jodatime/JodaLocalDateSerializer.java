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
 * A Kryo serializer for joda {@link LocalDate}. The LocalDate object is read or written as year,
 * month-of-year and day-of-month packed into one integer, and chronology as a separate attribute.
 * No time zone is involved. If the chronology is {@link org.joda.time.chrono.ISOChronology} the
 * attribute is serialized as an empty string, thus {@link org.joda.time.chrono.ISOChronology} is
 * considered to be default.
 *
 * Note that internally the LocalDate object makes use of an iLocalMillis value, but that field is
 * not accessible for reading here because the getLocalMillis() method is protected. There could
 * conceivably be cases where a user has created a derived version of LocalDate, and is using the
 * iLocalMillis value in some way that this serialization/deserialization will break. (Alternative
 * implementation: access the field using Java reflection?)
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
public class JodaLocalDateSerializer extends Serializer<LocalDate> {

   public JodaLocalDateSerializer() {
      setImmutable(true);
   }

   @Override
   public LocalDate read(final Kryo kryo, final Input input, final Class<LocalDate> type) {
      final int packedYearMonthDay = input.readInt(true);
      final Chronology chronology = IdentifiableChronology.readChronology(input);
      return new LocalDate(packedYearMonthDay / (13 * 32),
                           (packedYearMonthDay % (13 * 32)) / 32,
                           packedYearMonthDay % 32,
                           chronology);
   }

   @Override
   public void write(final Kryo kryo, final Output output, final LocalDate localDate) {
      final int packedYearMonthDay = localDate.getYear() * 13 * 32 +
                                     localDate.getMonthOfYear() * 32 +
                                     localDate.getDayOfMonth();
      output.writeInt(packedYearMonthDay, true);
      final String chronologyId = IdentifiableChronology.getChronologyId(localDate.getChronology());
      output.writeString(chronologyId == null ? "" : chronologyId);
   }
}
