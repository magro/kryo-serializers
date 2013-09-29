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
import java.util.EnumMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A serializer for {@link EnumMap}s.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class EnumMapSerializer extends Serializer<EnumMap<? extends Enum<?>, ?>> {
    
    private static final Field TYPE_FIELD;
    
    static {
        try {
            TYPE_FIELD = EnumMap.class.getDeclaredField( "keyType" );
            TYPE_FIELD.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( "The EnumMap class seems to have changed, could not access expected field.", e );
        }
    }

    // Workaround reference reading, this should be removed sometimes. See also
    // https://groups.google.com/d/msg/kryo-users/Eu5V4bxCfws/k-8UQ22y59AJ
    private static final Object FAKE_REFERENCE = new Object();

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EnumMap<? extends Enum<?>, ?> copy (final Kryo kryo, final EnumMap<? extends Enum<?>, ?> original) {
        return new EnumMap(original);
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private EnumMap<? extends Enum<?>, ?> create(final Kryo kryo, final Input input,
        final Class<EnumMap<? extends Enum<?>, ?>> type) {
        final Class<? extends Enum<?>> keyType = kryo.readClass( input ).getType();
        return new EnumMap( keyType );
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public EnumMap<? extends Enum<?>, ?> read(final Kryo kryo, final Input input,
            final Class<EnumMap<? extends Enum<?>, ?>> type) {
        kryo.reference(FAKE_REFERENCE);
        final EnumMap<? extends Enum<?>, ?> result = create(kryo, input, type);
        final Class<Enum<?>> keyType = getKeyType( result );
        final Enum<?>[] enumConstants = keyType.getEnumConstants();
        final EnumMap rawResult = result;
        final int size = input.readInt(true);
        for ( int i = 0; i < size; i++ ) {
            final int ordinal = input.readInt(true);
            final Enum<?> key = enumConstants[ordinal];
            final Object value = kryo.readClassAndObject( input );
            rawResult.put( key, value );
        }
        return result;
    }

    @Override
    public void write(final Kryo kryo, final Output output, final EnumMap<? extends Enum<?>, ?> map) {
        kryo.writeClass( output, getKeyType( map ) );
        output.writeInt(map.size(), true);
        for ( final Map.Entry<? extends Enum<?>,?> entry :  map.entrySet() ) {
            output.writeInt(entry.getKey().ordinal(), true);
            kryo.writeClassAndObject(output, entry.getValue());
        }
        if ( TRACE ) trace( "kryo", "Wrote EnumMap: " + map );
    }

    @SuppressWarnings("unchecked")
    private Class<Enum<?>> getKeyType( final EnumMap<?, ?> map ) {
        try {
            return (Class<Enum<?>>)TYPE_FIELD.get( map );
        } catch ( final Exception e ) {
            throw new RuntimeException( "Could not access keys field.", e );
        }
    }
}
