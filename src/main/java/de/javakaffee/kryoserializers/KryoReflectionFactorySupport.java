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

import sun.reflect.ReflectionFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serialize.ReferenceFieldSerializer;

/**
 * A {@link Kryo} specialization that uses sun's {@link ReflectionFactory} to create
 * new instance for classes without a default constructor.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class KryoReflectionFactorySupport extends Kryo {

    private static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();
    private static final Object[] INITARGS = new Object[0];
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    @Override
    protected Serializer newDefaultSerializer( final Class type ) {
        final ReferenceFieldSerializer result = new ReferenceFieldSerializer( this, type );
        result.setIgnoreSyntheticFields( false );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T newInstance( final Class<T> type ) {
        final T result = newInstanceFromNoArgsConstructor( type );
        return result != null ? result : newInstanceFromReflectionFactory( type );
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T newInstanceFromReflectionFactory( final Class<T> type ) {
        try {
            final Constructor<?> constructor = REFLECTION_FACTORY.newConstructorForSerialization( type, Object.class.getDeclaredConstructor( new Class[0] ) );
            constructor.setAccessible( true );
            return (T) constructor.newInstance( INITARGS );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public static <T> T newInstanceFromNoArgsConstructor( final Class<T> type ) {
        final Constructor<T> noArgsConstructor = getNoArgsConstructor( type );
        if ( noArgsConstructor != null ) {
            try {
                return noArgsConstructor.newInstance();
            } catch ( final Exception e ) {
                throw new RuntimeException( e );
            }
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    private static <T> Constructor<T> getNoArgsConstructor( final Class<T> type ) {
        final Constructor<?>[] constructors = type.getConstructors();
        for ( final Constructor<?> constructor : constructors ) {
            if ( constructor.getParameterTypes().length == 0 ) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }
    
}