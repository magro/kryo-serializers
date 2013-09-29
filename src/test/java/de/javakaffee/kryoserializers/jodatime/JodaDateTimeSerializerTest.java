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
package de.javakaffee.kryoserializers.jodatime;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;

/**
 * Test for {@link JodaDateTimeSerializer}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class JodaDateTimeSerializerTest {
    
    private Kryo _kryo;

    @BeforeTest
    protected void beforeTest() {
        _kryo = new Kryo();
        _kryo.register( DateTime.class, new JodaDateTimeSerializer() );
    }

    @Test( enabled = true )
    public void testJodaDateTime() {
        final DateTime obj = new DateTime().withDayOfYear( 42 );
        final byte[] serialized = serialize( _kryo, obj );
        final DateTime deserialized = deserialize( _kryo, serialized, DateTime.class );
        Assert.assertEquals( deserialized, obj );
    }

    @Test( enabled = true )
    public void testCopyJodaDateTime() {
        final DateTime obj = new DateTime().withDayOfYear( 42 );
        final DateTime copy = _kryo.copy(obj);
        Assert.assertEquals( copy, obj );
    }

}
