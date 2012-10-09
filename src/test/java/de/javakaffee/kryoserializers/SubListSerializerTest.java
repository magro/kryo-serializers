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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Test for {@link SubListSerializer}.
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class SubListSerializerTest {

    @SuppressWarnings("unchecked")
	@Test( enabled = true )
    public void testFieldSerializer () throws Exception {
        final Kryo kryo = new KryoReflectionFactorySupport() {
            
            @SuppressWarnings("rawtypes")
			@Override
            public Serializer<?> newSerializer(
            		Class<? extends Serializer> serializerClass, Class type) {
            	if ( SubListSerializer.canSerialize( type ) ) {
                    return new SubListSerializer();
                }
            	return super.newSerializer(serializerClass, type);
            }
        };
        kryo.setRegistrationRequired(false);
        
        final List<TestEnum> subList = new ArrayList<TestEnum>( Arrays.asList( TestEnum.values() ) ).subList( 1, 2 );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);        
        kryo.writeObject(output, subList);
        output.close();
        final byte[] serialized = outputStream.toByteArray();
        
       
        Input input = new Input(serialized);
        
        final List<TestEnum> deserialized =  kryo.readObject(input, subList.getClass() );
        input.close();
        
        assertEquals( deserialized, subList );
        assertEquals( deserialized.remove( 0 ), subList.remove( 0 ) );
        
    }
    
    static enum TestEnum {
        ITEM1, ITEM2, ITEM3;
    }

}