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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * A serializer for jdk proxies (proxies created via <code>java.lang.reflect.Proxy.newProxyInstance</code>).
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class JdkProxySerializer implements Serializer<Object> {

    public Object read(Kryo kryo, Input input, Class<Object> type) {
        final InvocationHandler invocationHandler = (InvocationHandler) kryo.readClassAndObject( input );
        final Class<?>[] interfaces = kryo.readObject( input, Class[].class );
        final ClassLoader classLoader = kryo.getClass().getClassLoader(); // TODO: can we do this?
        try {
            return Proxy.newProxyInstance( classLoader, interfaces, invocationHandler );
        } catch( final RuntimeException e ) {
            System.err.println( getClass().getName()+ ".read:\n" +
            		"Could not create proxy using classLoader " + classLoader + "," +
                    " have invocationhandler.classloader: " + invocationHandler.getClass().getClassLoader() +
                    " have contextclassloader: " + Thread.currentThread().getContextClassLoader() );
            throw e;
        }
    }

    public void write(Kryo kryo, Output output, Object obj) {
        kryo.writeClassAndObject( output, Proxy.getInvocationHandler( obj ) );
        kryo.writeObject( output, obj.getClass().getInterfaces() );
    }
}
