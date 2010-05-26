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

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ObjectBuffer;
import com.esotericsoftware.kryo.Serializer;

/**
 * Test for {@link SubListSerializer}.
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class SubListSerializerTest {

    @Test( enabled = true )
    public void testFieldSerializer () throws Exception {
        final Kryo kryo = new KryoReflectionFactorySupport() {
            
            @Override
            @SuppressWarnings( "unchecked" )
            public Serializer newSerializer( final Class type ) {
                if ( SubListSerializer.canSerialize( type ) ) {
                    return new SubListSerializer( this );
                }
                return super.newSerializer( type );
            }
        };
        kryo.setRegistrationOptional( true );
        
        final List<TestEnum> subList = new ArrayList<TestEnum>( Arrays.asList( TestEnum.values() ) ).subList( 1, 2 );
        final byte[] serialized = new ObjectBuffer( kryo, 1024 * 1024 ).writeObject( subList );
        @SuppressWarnings( "unchecked" )
        final List<TestEnum> deserialized = new ObjectBuffer( kryo, 1024 * 1024 ).readObject( serialized, subList.getClass() );

        assertEquals( deserialized, subList );
        assertEquals( deserialized.remove( 0 ), subList.remove( 0 ) );
        
    }
    
    static enum TestEnum {
        ITEM1, ITEM2, ITEM3;
    }

}