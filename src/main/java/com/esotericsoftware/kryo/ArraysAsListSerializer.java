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
package com.esotericsoftware.kryo;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.esotericsoftware.kryo.serialize.IntSerializer;

/**
 * A kryo {@link Serializer} for lists created via {@link Arrays#asList(Object...)}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings( "unchecked" )
public class ArraysAsListSerializer extends Serializer {

    private final Kryo _kryo;
    private Field _arrayField;

    public ArraysAsListSerializer( final Kryo kryo ) {
        _kryo = kryo;
        try {
            _arrayField = Class.forName( "java.util.Arrays$ArrayList" ).getDeclaredField( "a" );
            _arrayField.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T readObjectData( final ByteBuffer buffer, final Class<T> clazz ) {
        final int length = IntSerializer.get( buffer, true );
        final Class<?> componentType = _kryo.readClass( buffer ).getType();
        try {
            final Object[] items = (Object[]) Array.newInstance( componentType, length );
            for( int i = 0; i < length; i++ ) {
                items[i] = _kryo.readClassAndObject( buffer );
            }
            return (T) Arrays.asList( items );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObjectData( final ByteBuffer buffer, final Object obj ) {
         try {
            final Object[] array = (Object[]) _arrayField.get( obj );
            IntSerializer.put( buffer, array.length, true );
            final Class<?> componentType = array.getClass().getComponentType();
            _kryo.writeClass( buffer, componentType );
            for( final Object item : array ) {
                _kryo.writeClassAndObject( buffer, item );
            }
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

}
