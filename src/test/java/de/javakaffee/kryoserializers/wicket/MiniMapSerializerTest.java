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
package de.javakaffee.kryoserializers.wicket;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;

import java.util.Map.Entry;

import org.apache.wicket.util.collections.MiniMap;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;

import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;

/**
 * Test for {@link JodaDateTimeSerializer}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class MiniMapSerializerTest {
    
    private Kryo _kryo;

    @BeforeTest
    protected void beforeTest() {
        _kryo = new Kryo();
        _kryo.register( MiniMap.class, new MiniMapSerializer() );
    }

    @Test( enabled = true )
    public void testMiniMapEmpty() {
        final MiniMap<?, ?> obj = new MiniMap<Object, Object>( 0 );
        final byte[] serialized = serialize( _kryo, obj );
        final MiniMap<?, ?> deserialized = deserialize( _kryo, serialized, MiniMap.class );
        Assert.assertEquals( deserialized.size(), obj.size() );
    }

    @Test( enabled = true )
    public void testMiniMapExactNumberOfEntries() {
        final MiniMap<String, String> obj = new MiniMap<String, String>( 1 );
        obj.put( "foo", "bar" );
        final byte[] serialized = serialize( _kryo, obj );
        final MiniMap<?, ?> deserialized = deserialize( _kryo, serialized, MiniMap.class );
        Assert.assertEquals( deserialized.size(), obj.size() );
        final Entry<?, ?> deserializedNext = deserialized.entrySet().iterator().next();
        final Entry<?, ?> origNext = obj.entrySet().iterator().next();
        Assert.assertEquals( deserializedNext.getKey(), origNext.getKey() );
        Assert.assertEquals( deserializedNext.getValue(), origNext.getValue() );
    }

    @Test( enabled = true )
    public void testMiniMapLessThanMaxEntries() {
        final MiniMap<String, String> obj = new MiniMap<String, String>( 2 );
        obj.put( "foo", "bar" );
        final byte[] serialized = serialize( _kryo, obj );
        final MiniMap<?, ?> deserialized = deserialize( _kryo, serialized, MiniMap.class );
        Assert.assertEquals( deserialized.size(), obj.size() );
    }

    @SuppressWarnings("unchecked")
    @Test( enabled = true )
    public void testMiniMapAddEntriesAfterDeserialization() {
        final MiniMap<String, String> obj = new MiniMap<String, String>( 2 );
        obj.put( "foo", "bar" );
        final byte[] serialized = serialize( _kryo, obj );
        final MiniMap<String, String> deserialized = deserialize( _kryo, serialized, MiniMap.class );
        Assert.assertEquals( deserialized.size(), obj.size() );
        
        deserialized.put( "bar", "baz" );
        try {
            deserialized.put( "this should", "fail" );
            Assert.fail( "We told the orig MiniMap to accept 2 entries at max," +
            		" therefore we should not be allowed to put more." );
        } catch( final RuntimeException e ) {
            // this is expected - didn't use @Test.expectedExceptions
            // as this would tie us to the exactly thrown exception
        }
    }

}
