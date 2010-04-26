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

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.EnumSerializer;
import com.esotericsoftware.kryo.serialize.IntSerializer;
import com.esotericsoftware.kryo.serialize.SimpleSerializer;

/**
 * A serializer for {@link EnumMap}s.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class EnumMapSerializer extends SimpleSerializer<EnumMap<? extends Enum<?>, ?>> {
    
    private static final Field TYPE_FIELD;
    
    static {
        try {
            TYPE_FIELD = EnumMap.class.getDeclaredField( "keyType" );
            TYPE_FIELD.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( "The EnumMap class seems to have changed, could not access expected field.", e );
        }
    }
    
    private final Kryo _kryo;

    /**
     * Constructor.
     */
    public EnumMapSerializer( final Kryo kryo ) {
        _kryo = kryo;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public EnumMap<?, ?> read( final ByteBuffer buffer ) {
        final Class<?> keyType = _kryo.readClass( buffer ).getType();
        final EnumMap result = new EnumMap( keyType );
        final int size = IntSerializer.get( buffer, true );
        for ( int i = 0; i < size; i++ ) {
            final Object key = EnumSerializer.get( buffer, keyType );
            final Object value = _kryo.readClassAndObject( buffer );
            result.put( key, value );
        }
        return result;
    }

    @Override
    public void write( final ByteBuffer buffer, final EnumMap<? extends Enum<?>, ?> map ) {
        _kryo.writeClass( buffer, getKeyType( map ) );
        IntSerializer.put( buffer, map.size(), true );
        for ( final Map.Entry<? extends Enum<?>,?> entry :  map.entrySet() ) {
            EnumSerializer.put( buffer, entry.getKey() );
            _kryo.writeClassAndObject( buffer, entry.getValue() );
        }
        if ( TRACE ) trace( "kryo", "Wrote EnumMap: " + map );
    }

    @SuppressWarnings( "unchecked" )
    private Class getKeyType( final EnumMap<?, ?> map ) {
        try {
            return (Class)TYPE_FIELD.get( map );
        } catch ( final Exception e ) {
            throw new RuntimeException( "Could not access keys field.", e );
        }
    }

}
