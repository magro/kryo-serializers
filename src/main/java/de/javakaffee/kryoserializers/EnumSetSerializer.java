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
import com.esotericsoftware.kryo.serializers.DefaultSerializers;

import java.lang.reflect.Field;
import java.util.EnumSet;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/**
 * A serializer for {@link EnumSet}s.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings( "unchecked" )
public class EnumSetSerializer implements Serializer<EnumSet> {
    
    private static final Field TYPE_FIELD;
    
    static {
        try {
            TYPE_FIELD = EnumSet.class.getDeclaredField( "elementType" );
            TYPE_FIELD.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( "The EnumSet class seems to have changed, could not access expected field.", e );
        }
    }

    public EnumSet read(Kryo kryo, Input input, Class<EnumSet> type) {
        final Class<Enum> elementType = kryo.readClass( input ).getType();
        DefaultSerializers.EnumSerializer enumSerializer = new DefaultSerializers.EnumSerializer(kryo, elementType);
        final EnumSet result = EnumSet.noneOf( elementType );
        final int size = input.readInt(true);
        for ( int i = 0; i < size; i++ ) {
            result.add( enumSerializer.read( kryo, input, elementType ) );
        }
        return result;
    }

    public void write(Kryo kryo, Output output, EnumSet set) {
        Class<Enum> elementType = getElementType( set );
        kryo.writeClass( output, elementType );
        output.writeInt( set.size(), true );

        DefaultSerializers.EnumSerializer enumSerializer = new DefaultSerializers.EnumSerializer(kryo, elementType);
        for (Enum aSet : (Iterable<Enum>) set) {
            enumSerializer.write(kryo, output, aSet);
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
