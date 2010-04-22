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
import java.util.EnumSet;
import java.util.Iterator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.IntSerializer;
import com.esotericsoftware.kryo.serialize.SimpleSerializer;

/**
 * A serializer for {@link EnumSet}s.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings( "unchecked" )
public class EnumSetSerializer extends SimpleSerializer<EnumSet> {
    
    private static final Field TYPE_FIELD;
    
    static {
        try {
            TYPE_FIELD = EnumSet.class.getDeclaredField( "elementType" );
            TYPE_FIELD.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( "The EnumSet class seems to have changed, could not access expected field.", e );
        }
    }
    
    private final Kryo _kryo;

    /**
     * Constructor.
     */
    public EnumSetSerializer( final Kryo kryo ) {
        _kryo = kryo;
    }

    @Override
    public EnumSet read( final ByteBuffer buffer ) {
        final Class elementType = _kryo.readClass( buffer ).getType();
        final EnumSet result = EnumSet.noneOf( elementType );
        final int size = IntSerializer.get( buffer, true );
        for ( int i = 0; i < size; i++ ) {
            result.add( _kryo.readClassAndObject( buffer ) );
        }
        return result;
    }

    @Override
    public void write( final ByteBuffer buffer, final EnumSet set ) {
        _kryo.writeClass( buffer, getElementType( set ) );
        IntSerializer.put( buffer, set.size(), true );
        for ( final Iterator<?> iter = set.iterator(); iter.hasNext(); ) {
            _kryo.writeClassAndObject( buffer, iter.next() );
        }
        if ( TRACE ) trace( "kryo", "Wrote EnumSet: " + set );
    }

    private Class getElementType( final EnumSet<? extends Enum<?>> set ) {
        try {
            return (Class)TYPE_FIELD.get( set );
        } catch ( final Exception e ) {
            throw new RuntimeException( "Could not access keys field.", e );
        }
    }

}
