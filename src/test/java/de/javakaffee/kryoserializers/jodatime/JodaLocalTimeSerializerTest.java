package de.javakaffee.kryoserializers.jodatime;

import com.esotericsoftware.kryo.Kryo;
import org.joda.time.*;
import org.joda.time.chrono.GregorianChronology;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;

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
