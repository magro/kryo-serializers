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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ObjectBuffer;
import com.esotericsoftware.kryo.Serializer;

import de.javakaffee.kryoserializers.ClassSerializer;
import de.javakaffee.kryoserializers.KryoReflectionFactorySupport;

/**
 * Test for {@link CGLibProxyFormat}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class CGLibProxySerializerTest {
    
    private Kryo _kryo;

    @BeforeTest
    protected void beforeTest() {
        _kryo = new KryoReflectionFactorySupport() {
            
            @SuppressWarnings( "unchecked" )
            @Override
            public Serializer newSerializer( final Class type ) {
                if ( CGLibProxySerializer.canSerialize( type ) ) {
                    return new CGLibProxySerializer( this );
                }
                return super.newSerializer( type );
            }
            
        };
        _kryo.setRegistrationOptional( true );
        _kryo.register( Class.class, new ClassSerializer( _kryo ) );
        _kryo.register( CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer( _kryo ) );
    }

    @Test( enabled = true )
    public void testCGLibProxy() {
        final ClassToProxy proxy = createProxy( new ClassToProxy() );
        proxy.setValue( "foo" );
        
        final byte[] serialized = new ObjectBuffer( _kryo ).writeObject( proxy );
        final ClassToProxy deserialized = new ObjectBuffer( _kryo ).readObject( serialized, proxy.getClass() );
        Assert.assertEquals( deserialized.getValue(), proxy.getValue() );
    }

    /**
     * Test that a cglib proxy is handled correctly.
     */
    @Test( enabled = true )
    public void testCGLibProxyForExistingFormat() throws XMLStreamException {
        final Map<String, String> proxy = createProxy( new HashMap<String, String>() );
        proxy.put( "foo", "bar" );
        Assert.assertEquals( proxy.get( "foo" ), "bar" );
        
        final byte[] serialized = new ObjectBuffer( _kryo ).writeObject( proxy );
        @SuppressWarnings( "unchecked" )
        final Map<String, String> deserialized = new ObjectBuffer( _kryo ).readObject( serialized, proxy.getClass() );
        Assert.assertEquals( deserialized.get( "foo" ), proxy.get( "foo" ) );
    }

    @SuppressWarnings( "unchecked" )
    private <T> T createProxy( final T obj ) {
        
        final Enhancer e = new Enhancer();
        e.setInterfaces( new Class[] { Serializable.class } );
        final Class<? extends Object> class1 = obj.getClass();
        e.setSuperclass( class1 );
        e.setCallback( new DelegatingHandler( obj ) );
        e.setNamingPolicy( new DefaultNamingPolicy() {
            @Override
            public String getClassName(final String prefix, final String source,
                final Object key, final Predicate names) {
                return super.getClassName( "MSM_" + prefix, source, key, names );
            }
        } );

        return (T) e.create();
    }

    public static class DelegatingHandler implements InvocationHandler, Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private final Object _delegate;

        public DelegatingHandler( final Object delegate ) {
            _delegate = delegate;
        }

        public Object invoke( final Object obj, final Method method, final Object[] args ) throws Throwable {
            return method.invoke( _delegate, args );
        }
    }
    
    public static class ClassToProxy {
        private String _value;
        
        /**
         * @param value the value to set
         */
        public void setValue( final String value ) {
            _value = value;
        }
        
        /**
         * @return the value
         */
        public String getValue() {
            return _value;
        }
    }

}
