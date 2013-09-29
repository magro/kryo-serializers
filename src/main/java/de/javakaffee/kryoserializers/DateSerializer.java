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

import java.lang.reflect.Constructor;
import java.util.Date;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A kryo {@link Serializer} for {@link Date} and subclasses. Must be registered like this:
 * <code><pre>
 *  Kryo kryo = new Kryo() {
 *      public Serializer<?> getDefaultSerializer(final Class clazz) {
 *          if ( Date.class.isAssignableFrom( type ) ) {
 *              return new DateSerializer( type );
 *          }
 *          return super.getDefaultSerializer( clazz );
 *      }
 *  };
 * </pre></code>
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class DateSerializer extends Serializer<Date> {

    private final Constructor<? extends Date> _constructor;

    public DateSerializer(final Class<? extends Date> clazz) {
        try {
            _constructor = clazz.getConstructor(long.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date read(final Kryo kryo, final Input input, final Class<Date> type) {
        try {
            return _constructor.newInstance(input.readLong(true));
        } catch (final Exception e) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final Kryo kryo, final Output output, final Date obj) {
        output.writeLong(obj.getTime(), true);
    }

    @Override
    public Date copy(final Kryo kryo, final Date original) {
        return (Date) original.clone();
    }

}
