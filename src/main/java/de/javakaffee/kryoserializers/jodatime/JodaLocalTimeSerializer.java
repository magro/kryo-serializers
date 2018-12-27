package de.javakaffee.kryoserializers.jodatime;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.chrono.*;

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
 * </p>
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
    public LocalTime read(Kryo kryo, Input input, Class<? extends LocalTime> type) {
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
