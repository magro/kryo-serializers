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

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;

/**
 * Test for {@link JodaIntervalSerializer}.
 * 
 */
public class JodaIntervalSerializerTest {
    
    private Kryo _kryo;

    @BeforeTest
    protected void beforeTest() {
        _kryo = new Kryo();
        _kryo.register( Interval.class, new JodaIntervalSerializer() );
    }

    @Test( enabled = true )
    public void testJodaInterval() {
        final Interval obj = new Interval(new DateTime(1942,1,1,0,0,0,0), Days.days(42));
        final byte[] serialized = serialize( _kryo, obj );
        final Interval deserialized = deserialize( _kryo, serialized, Interval.class );
        Assert.assertEquals( deserialized, obj );
    }

    @Test( enabled = true )
    public void testCopyJodaDateTime() {
        final Interval obj = new Interval(new DateTime(1942,1,1,0,0,0,0), Days.days(42));
        final Interval copy = _kryo.copy(obj);
        Assert.assertEquals( copy, obj );
    }

}
