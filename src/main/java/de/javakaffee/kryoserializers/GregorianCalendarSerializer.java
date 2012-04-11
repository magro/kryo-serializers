/*
 * Copyright 2010 Martin Grotzke
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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A more efficient kryo {@link Serializer} for {@link GregorianCalendar} instances (which
 * are created via <code>Calendar.getInstance()</code> if the locale is not thai or japanese, so
 * JapaneseImperialCalendar and BuddhistCalendar are not supported by this serializer).
 * <p>
 * With the default reflection based serialization, a calendar instance
 * (created via <code>Calendar.getInstance(Locale.ENGLISH)</code>)
 * would take 1323 byte, this one only takes 24 byte.
 * </p>
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class GregorianCalendarSerializer implements Serializer<GregorianCalendar> {

    private final Field _zoneField;

    public GregorianCalendarSerializer() {
        try {
            _zoneField = Calendar.class.getDeclaredField( "zone" );
            _zoneField.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public GregorianCalendar read(Kryo kryo, Input input, Class<GregorianCalendar> type) {
        final Calendar result = GregorianCalendar.getInstance();
        
        result.setTimeInMillis( input.readLong( true ) );
        result.setLenient( fromInt( input.readInt( true ) ) );
        result.setFirstDayOfWeek( input.readInt( true ) );
        result.setMinimalDaysInFirstWeek( input.readInt( true ) );
        
        /* check if we actually need to set the timezone, as
         * TimeZone.getTimeZone is synchronized, so we might prevent this
         */
        final String timeZoneId = input.readString();
        if ( !getTimeZone( result ).getID().equals( timeZoneId ) ) {
            result.setTimeZone( TimeZone.getTimeZone( timeZoneId ) );
        }
        
        return (GregorianCalendar) result;
    }

    public void write(Kryo kryo, Output output, GregorianCalendar calendar) {
        output.writeLong( calendar.getTimeInMillis(), true );
        output.writeLong( calendar.getTimeInMillis(), true );
        output.writeInt( toInt( calendar.isLenient() ), true );
        output.writeInt( calendar.getFirstDayOfWeek(), true );
        output.writeInt( calendar.getMinimalDaysInFirstWeek(), true );
        output.writeString( getTimeZone( calendar ).getID() );
    }
    
    private int toInt( final boolean b ) {
        return b ? 1 : 0;
    }
    
    private boolean fromInt( final int value ) {
        if ( value == 0 )
            return false;
        if ( value == 1 )
            return true;
        throw new IllegalArgumentException( "The value " + value + " cannot be translated into a boolean value, only 0 or 1 are considered valid." );
    }

    private TimeZone getTimeZone( final Calendar obj ) {
        /* access the timezone via the field, to prevent cloning of the tz */
        try {
            return (TimeZone) _zoneField.get( obj );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
