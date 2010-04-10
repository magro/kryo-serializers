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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.SimpleSerializer;

/**
 * A serializer for jdk proxies (proxies created via <code>java.lang.reflect.Proxy.newProxyInstance</code>).
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class JdkProxySerializer extends SimpleSerializer<Object> {

    private final Kryo _kryo;

    public JdkProxySerializer( final Kryo kryo ) {
        _kryo = kryo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object read( final ByteBuffer buffer ) {
        final InvocationHandler invocationHandler = (InvocationHandler) _kryo.readClassAndObject( buffer );
        final String[] interfaceNames = _kryo.readObjectData( buffer, String[].class );
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Class<?>[] interfaces = getInterfaces( interfaceNames, classLoader );
        return Proxy.newProxyInstance( classLoader, interfaces, invocationHandler );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write( final ByteBuffer buffer, final Object obj ) {
        final InvocationHandler invocationHandler = Proxy.getInvocationHandler( obj );
        _kryo.writeClassAndObject( buffer, invocationHandler );
        final String[] interfaceNames = getInterfaceNames( obj );
        _kryo.writeObjectData( buffer, interfaceNames );
    }

    public static String[] getInterfaceNames( final Object obj ) {
        final Class<?>[] interfaces = obj.getClass().getInterfaces();
        if ( interfaces != null ) {
            final String[] interfaceNames = new String[interfaces.length];
            for ( int i = 0; i < interfaces.length; i++ ) {
                interfaceNames[i] = interfaces[i].getName();
            }
            return interfaceNames;
        }
        return new String[0];
    }

    public static Class<?>[] getInterfaces( final String[] interfaceNames, final ClassLoader classLoader ) {
        if ( interfaceNames != null ) {
            try {
                final Class<?>[] interfaces = new Class<?>[interfaceNames.length];
                for ( int i = 0; i < interfaceNames.length; i++ ) {
                    interfaces[i] = Class.forName( interfaceNames[i], true, classLoader );
                }
                return interfaces;
            } catch ( final ClassNotFoundException e ) {
                throw new RuntimeException( e );
            }
        }
        return new Class<?>[0];
    }

}
