/*
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
import org.joda.time.Interval;
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
 * A format for Joda {@link Interval}, that stores the start and end millis, and chronology 
 * as separate attributes. If the chronology is {@link ISOChronology},
 * the attribute is omitted, thus {@link ISOChronology} is seen as default.
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
 */
public class JodaIntervalSerializer extends Serializer<Interval> {

    public JodaIntervalSerializer() {
        setImmutable(true);
    }

    @Override
    public Interval read(final Kryo kryo, final Input input, final Class<Interval> type) {
        
        long startMillis = input.readLong(true);
        long endMillis = input.readLong(true);
        
        final Chronology chronology = IdentifiableChronology.readChronology( input );
        
        return new Interval(startMillis, endMillis, chronology);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Interval obj) {
        final long startMillis = obj.getStartMillis();
        final long endMillis = obj.getEndMillis();
        final String chronologyId = IdentifiableChronology.getChronologyId( obj.getChronology() );
        
        output.writeLong(startMillis, true);
        output.writeLong(endMillis, true);
        output.writeString(chronologyId == null ? "" : chronologyId);
    }
    

}
