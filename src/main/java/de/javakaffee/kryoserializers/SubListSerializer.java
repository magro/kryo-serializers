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
import java.util.List;

/**
 * A kryo {@link Serializer} for lists created via {@link List#subList(int, int)}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings( "unchecked" )
public class SubListSerializer implements Serializer {
    
    private static final Class<?> SUBLIST_CLASS = getClass( "java.util.SubList" );

    private Field _listField;
    private Field _offsetField;
    private Field _sizeField;

    public SubListSerializer() {
        try {
            final Class<?> clazz = Class.forName( "java.util.SubList" );
            _listField = clazz.getDeclaredField( "l" );
            _offsetField = clazz.getDeclaredField( "offset" );
            _sizeField = clazz.getDeclaredField( "size" );
            _listField.setAccessible( true );
            _offsetField.setAccessible( true );
            _sizeField.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }
    
    private static Class<?> getClass( final String className ) {
        try {
            return Class.forName( className );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Can be used to determine, if the given type can be handled by this serializer.
     * @param type the class to check.
     * @return <code>true</code> if the given class can be serialized/deserialized by this serializer.
     */
    public static boolean canSerialize( final Class<?> type ) {
        return SUBLIST_CLASS.isAssignableFrom( type );
    }

    public Object read(Kryo kryo, Input input, Class clazz) {
        final List<?> list = (List<?>) kryo.readClassAndObject( input );
        final int fromIndex = input.readInt(true);
        final int toIndex = input.readInt(true);
        return list.subList( fromIndex, toIndex );
    }

    public void write(Kryo kryo, Output output, Object obj) {
        try {
            kryo.writeClassAndObject( output, _listField.get( obj ) );
            final int fromIndex = _offsetField.getInt( obj );
            output.writeInt(fromIndex, true);
            final int toIndex = fromIndex + _sizeField.getInt( obj );
            output.writeInt(toIndex, true);
        } catch ( final RuntimeException e ) {
            // Don't eat and wrap RuntimeExceptions because the ObjectBuffer.write...
            // handles SerializationException specifically (resizing the buffer)...
            throw e;
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
