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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sun.reflect.ReflectionFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

/**
 * A {@link Kryo} specialization that uses sun's {@link ReflectionFactory} to create
 * new instance for classes without a default constructor.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings("restriction")
public class KryoReflectionFactorySupport extends Kryo {

    private static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();
    private static final Object[] INITARGS = new Object[0];
    
    private static final Map<Class<?>, Constructor<?>> _constructors = new ConcurrentHashMap<Class<?>, Constructor<?>>();

    @Override
    public Serializer<?> getDefaultSerializer(@SuppressWarnings("rawtypes") final Class type) {
        final Serializer<?> result = super.getDefaultSerializer(type);
        if(result instanceof FieldSerializer) {
            // don't ignore synthetic fields so that inner classes work (see KryoTest.testInnerClass)
            ((FieldSerializer<?>) result).setIgnoreSyntheticFields(false);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public <T> T newInstance( final Class<T> type ) {
        if (type == null) { throw new IllegalArgumentException("type cannot be null."); }
        Constructor<?> constructor = _constructors.get( type );
        if ( constructor == null ) {
            constructor = getNoArgsConstructor( type );
            if ( constructor == null ) {
                constructor = newConstructorForSerialization( type );
            }
            _constructors.put( type, constructor );
        }
        return (T) newInstanceFrom( constructor );
    }

    private static Object newInstanceFrom( final Constructor<?> constructor ) {
        try {
            return constructor.newInstance( INITARGS );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T newInstanceFromReflectionFactory( final Class<T> type ) {
        Constructor<?> constructor = _constructors.get( type );
        if ( constructor == null ) {
            constructor = newConstructorForSerialization( type );
            _constructors.put( type, constructor );
        }
        return (T) newInstanceFrom( constructor );
    }

    private static <T> Constructor<?> newConstructorForSerialization( final Class<T> type ) {
        try {
            final Constructor<?> constructor = REFLECTION_FACTORY.newConstructorForSerialization( type, Object.class.getDeclaredConstructor( new Class[0] ) );
            constructor.setAccessible( true );
            return constructor;
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private static Constructor<?> getNoArgsConstructor( final Class<?> type ) {
        final Constructor<?>[] constructors = type.getConstructors();
        for ( final Constructor<?> constructor : constructors ) {
            if ( constructor.getParameterTypes().length == 0 ) {
                constructor.setAccessible( true );
                return constructor;
            }
        }
        return null;
    }
    
}