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
import java.util.EnumSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A serializer for {@link EnumSet}s.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings( { "unchecked", "rawtypes" } )
public class EnumSetSerializer extends Serializer<EnumSet<? extends Enum<?>>> {
    
    private static final Field TYPE_FIELD;
    
    static {
        try {
            TYPE_FIELD = EnumSet.class.getDeclaredField( "elementType" );
            TYPE_FIELD.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( "The EnumSet class seems to have changed, could not access expected field.", e );
        }
    }

    @Override
    public EnumSet read(final Kryo kryo, final Input input, final Class<EnumSet<? extends Enum<?>>> type) {
        final Class<Enum> elementType = kryo.readClass( input ).getType();
        final EnumSet result = EnumSet.noneOf( elementType );
        final int size = input.readInt(true);
        final Enum<?>[] enumConstants = elementType.getEnumConstants();
        for ( int i = 0; i < size; i++ ) {
            result.add( enumConstants[input.readInt(true)] );
        }
        return result;
    }

    @Override
    public void write(final Kryo kryo, final Output output, final EnumSet<? extends Enum<?>> set) {
        kryo.writeClass( output, getElementType( set ) );
        output.writeInt( set.size(), true );
        for (final Enum item : set) {
            output.writeInt(item.ordinal(), true);
        }

        if ( TRACE ) trace( "kryo", "Wrote EnumSet: " + set );
    }

    private Class<? extends Enum<?>> getElementType( final EnumSet<? extends Enum<?>> set ) {
        try {
            return (Class)TYPE_FIELD.get( set );
        } catch ( final Exception e ) {
            throw new RuntimeException( "Could not access keys field.", e );
        }
    }
}
