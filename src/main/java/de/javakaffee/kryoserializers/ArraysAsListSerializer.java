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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * A kryo {@link Serializer} for lists created via {@link Arrays#asList(Object...)}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings( "unchecked" )
public class ArraysAsListSerializer<T> implements Serializer<T> {

    private Field _arrayField;

    public ArraysAsListSerializer() {
        try {
            _arrayField = Class.forName( "java.util.Arrays$ArrayList" ).getDeclaredField( "a" );
            _arrayField.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public T read(Kryo kryo, Input input, Class<T> type) {
        final int length = input.readInt(true);
        final Class<?> componentType = kryo.readClass( input ).getType();
        try {
            final Object[] items = (Object[]) Array.newInstance( componentType, length );
            for( int i = 0; i < length; i++ ) {
                items[i] = kryo.readClassAndObject( input );
            }
            return (T) Arrays.asList( items );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void write(Kryo kryo, Output output, T obj) {
         try {
            final Object[] array = (Object[]) _arrayField.get( obj );
             output.writeInt(array.length, true);
            final Class<?> componentType = array.getClass().getComponentType();
             kryo.writeClass( output, componentType );
            for( final Object item : array ) {
                kryo.writeClassAndObject( output, item );
            }
         } catch ( final RuntimeException e ) {
             // Don't eat and wrap RuntimeExceptions because the ObjectBuffer.write...
             // handles SerializationException specifically (resizing the buffer)...
             throw e;
         } catch ( final Exception e ) {
             throw new RuntimeException( e );
         }
    }
}
