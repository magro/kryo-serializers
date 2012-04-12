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
import java.util.EnumMap;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/**
 * A serializer for {@link EnumMap}s.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class EnumMapSerializer implements Serializer<EnumMap<? extends Enum<?>, ?>> {
    
    private static final Field TYPE_FIELD;
    
    static {
        try {
            TYPE_FIELD = EnumMap.class.getDeclaredField( "keyType" );
            TYPE_FIELD.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( "The EnumMap class seems to have changed, could not access expected field.", e );
        }
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public EnumMap<? extends Enum<?>, ?> read(Kryo kryo, Input input,
        Class<EnumMap<? extends Enum<?>, ?>> type) {
        final Class<? extends Enum<?>> keyType = kryo.readClass( input ).getType();
        final EnumMap result = new EnumMap( keyType );
        DefaultSerializers.EnumSerializer enumSerializer = new DefaultSerializers.EnumSerializer(kryo, keyType);

        final int size = input.readInt(true);
        for ( int i = 0; i < size; i++ ) {
            final Enum key = enumSerializer.read(kryo, input, (Class<Enum>) keyType );
            final Object value = kryo.readClassAndObject( input );
            result.put( key, value );
        }
        return result;
    }

    public void write(Kryo kryo, Output output, EnumMap<? extends Enum<?>, ?> map) {
        kryo.writeClass( output, getKeyType( map ) );
        output.writeInt(map.size(), true);
        DefaultSerializers.EnumSerializer serializer = null;
        for ( final Map.Entry<? extends Enum<?>,?> entry :  map.entrySet() ) {

            // As with read, we assume that all keys have the same class.
            if (serializer == null)
                serializer = new DefaultSerializers.EnumSerializer(kryo, entry.getKey().getClass());

            serializer.write(kryo, output, entry.getKey());
            kryo.writeClassAndObject( output, entry.getValue() );
        }
        if ( TRACE ) trace( "kryo", "Wrote EnumMap: " + map );
    }

    private Class<?> getKeyType( final EnumMap<?, ?> map ) {
        try {
            return (Class<?>)TYPE_FIELD.get( map );
        } catch ( final Exception e ) {
            throw new RuntimeException( "Could not access keys field.", e );
        }
    }
}
