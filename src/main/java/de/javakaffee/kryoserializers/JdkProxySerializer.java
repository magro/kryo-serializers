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
        final Class<?>[] interfaces = _kryo.readObjectData( buffer, Class[].class );
        final ClassLoader classLoader = _kryo.getClassLoader();
        try {
            return Proxy.newProxyInstance( classLoader, interfaces, invocationHandler );
        } catch( final RuntimeException e ) {
            System.err.println( getClass().getName()+ ".read:\n" +
            		"Could not create proxy using classLoader " + classLoader + "," +
            		" have invoctaionhandler.classloader: " + invocationHandler.getClass().getClassLoader() +
                    " have contextclassloader: " + Thread.currentThread().getContextClassLoader() );
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write( final ByteBuffer buffer, final Object obj ) {
        _kryo.writeClassAndObject( buffer, Proxy.getInvocationHandler( obj ) );
        _kryo.writeObjectData( buffer, obj.getClass().getInterfaces() );
    }

}
