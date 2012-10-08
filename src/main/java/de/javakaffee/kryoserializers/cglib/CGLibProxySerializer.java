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
package de.javakaffee.kryoserializers.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A kryo serializer for cglib proxies. It needs to be registered for {@link CGLibProxyMarker} class.
 * When the serializer for a certain class is requested (via {@link Kryo#getDefaultSerializer(Class)})
 * {@link #canSerialize(Class)} has to be checked with the provided class to see if 
 * a {@link CGLibProxySerializer} should be returned.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class CGLibProxySerializer extends Serializer<Object> {

    /**
     * This class is used as a marker class - written to the class attribute
     * on serialization and checked on deserialization (via {@link CGLibProxyFormat#canConvert(Class)}.
     */
    public static interface CGLibProxyMarker {}

    private static String DEFAULT_NAMING_MARKER = "$$EnhancerByCGLIB$$";

    public static boolean canSerialize( final Class<?> cls ) {
        return Enhancer.isEnhanced( cls ) && cls.getName().indexOf( DEFAULT_NAMING_MARKER ) > 0;
    }
    
    @Override
    public Object read(final Kryo kryo, final Input input, final Class<Object> type) {
        final Class<?> superclass = kryo.readClass( input ).getType();
        final Class<?>[] interfaces = kryo.readObject(input, Class[].class);
        final Callback[] callbacks = kryo.readObject(input, Callback[].class);
        return createProxy( superclass, interfaces, callbacks );
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Object obj) {
        kryo.writeClass( output, obj.getClass().getSuperclass() );
        kryo.writeObject( output, obj.getClass().getInterfaces() );
        kryo.writeObject( output, ((Factory)obj).getCallbacks() );
    }

    private Object createProxy( final Class<?> targetClass, final Class<?>[] interfaces, final Callback[] callbacks ) {
        final Enhancer e = new Enhancer();
        e.setInterfaces( interfaces );
        e.setSuperclass( targetClass );
        e.setCallbacks( callbacks );
        return e.create();
    }

}
