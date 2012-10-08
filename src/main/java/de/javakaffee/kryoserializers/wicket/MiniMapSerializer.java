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
package de.javakaffee.kryoserializers.wicket;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import org.apache.wicket.util.collections.MiniMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A format for wicket's {@link MiniMap}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class MiniMapSerializer extends Serializer<MiniMap<Object, Object>> {
    
    /* To be correct we need to know the size of the internal array, otherwise
     * we might create a too small MiniMap on deserilization
     */
    private static final Field KEYS_FIELD;
    
    static {
        try {
            KEYS_FIELD = MiniMap.class.getDeclaredField( "keys" );
            KEYS_FIELD.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( "The MiniMap seems to have changed, could not access expected field.", e );
        }
    }

    private int getMaxEntries( final MiniMap<?, ?> map ) {
        try {
            return ( (Object[])KEYS_FIELD.get( map ) ).length;
        } catch ( final Exception e ) {
            throw new RuntimeException( "Could not access keys field.", e );
        }
    }

    @Override
    public void write(final Kryo kryo, final Output output, final MiniMap<Object, Object> map) {
        output.writeInt(getMaxEntries( map ), true);
        output.writeInt( map.size(), true);

        for (final Entry<?, ?> entry : map.entrySet()) {
            kryo.writeClassAndObject(output, entry.getKey());
            kryo.writeClassAndObject(output, entry.getValue());
        }

        if ( TRACE ) trace( "kryo", "Wrote map: " + map );
    }

    @Override
    public MiniMap<Object, Object> read(final Kryo kryo, final Input input, final Class<MiniMap<Object, Object>> type) {
        final int maxEntries = input.readInt( true );
        final MiniMap<Object, Object> result = new MiniMap<Object, Object>( maxEntries );
        final int size = input.readInt( true );
        for ( int i = 0; i < size; i++ ) {
            final Object key = kryo.readClassAndObject( input );
            final Object value = kryo.readClassAndObject( input );
            result.put( key, value );
        }
        return result;
    }

}
