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

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import org.objenesis.strategy.StdInstantiatorStrategy;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultClassResolver;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.esotericsoftware.minlog.Log;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;

/**
 * Test for {@link CGLibProxyFormat}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class CGLibProxySerializerTest {
    
    private Kryo _kryo;

    @BeforeTest
    protected void beforeTest() {
        _kryo = createKryo();
    }

    private Kryo createKryo() {
        final DefaultClassResolver classResolver = new DefaultClassResolver() {
            @Override
            protected Class<?> getTypeByName(final String className) {
                if (className.indexOf(CGLibProxySerializer.DEFAULT_NAMING_MARKER) > 0) {
                    return CGLibProxySerializer.CGLibProxyMarker.class;
                }
                return super.getTypeByName(className);
            }
        };
        final Kryo kryo = new Kryo(classResolver, new MapReferenceResolver()) {

            @Override
            @SuppressWarnings("rawtypes")
            public Serializer getDefaultSerializer(final Class type) {
                if ( CGLibProxySerializer.canSerialize( type ) ) {
                    return getSerializer(CGLibProxySerializer.CGLibProxyMarker.class);
                }
                return super.getDefaultSerializer(type);
            }

        };
        // The default strategy is needed so that HashMap is created via the constructor,
        // the StdInstantiatorStrategy (fallback) is needed for classes without default
        // constructor (e.g. DelegatingHandler).
        final DefaultInstantiatorStrategy instantiatorStrategy = new DefaultInstantiatorStrategy();
        instantiatorStrategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        kryo.setInstantiatorStrategy(instantiatorStrategy);
        kryo.register( CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer() );
        kryo.register( Arrays.asList( "" ).getClass(), new ArraysAsListSerializer() );
        return kryo;
    }

    @Test( enabled = false )
    public void testProxiesFromFileWrite() throws Exception {
        Log.TRACE = true;
        final ClassToProxy obj = new ClassToProxy();
        obj._value = "foo";
		final ClassToProxy proxy1 = createProxy( obj );
		final ClassToProxy proxy2 = createProxy( obj );
        final List<ClassToProxy> proxies = Arrays.asList( proxy1, proxy2 );
        final OutputStream out = new FileOutputStream( new File( "/tmp/cglib-data-1.ser" ) );
        final Output output = new Output(out);
        _kryo.writeClassAndObject(output, proxies);
        output.close();
    }

    /**
     * Uses data serialized by {@link #testProxiesFromFileWrite()} that got moved to src/main/resources.
     * That serialized data is read from a file verifies that there's no hot class loader
     * that might cache some proxy class.
     */
    @SuppressWarnings("unchecked")
    @Test( enabled = true )
    public void testProxiesFromFileRead() throws Exception {
        Log.TRACE = true;
        final InputStream in = new FileInputStream( new File( getClass().getResource("/cglib-data-1.ser").toURI() ) );
        final Input input = new Input(in);
        final List<ClassToProxy> proxies = (List<ClassToProxy>) _kryo.readClassAndObject(input);
        in.close();
        System.out.println(proxies);
        assertEquals(2, proxies.size());
        final ClassToProxy proxy1 = proxies.get(0);
		assertEquals( "foo", proxy1.getValue() );
        final ClassToProxy proxy2 = proxies.get(1);
		assertEquals( "foo", proxy2.getValue() );
        // test that the proxied object is referenced by both proxies
		proxy1.setValue("bar");
		assertEquals( "bar", proxy1.getValue() );
		assertEquals( "bar", proxy2.getValue() );
    }

    @Test( enabled = true )
    public void testContainerWithCGLibProxy() throws Exception {

        final CustomClassLoader loader = new CustomClassLoader( getClass().getClassLoader() );
        // _kryo.setClassLoader(loader);
        
        final Class<?> myServiceClass = loader.loadClass( MyServiceImpl.class.getName() );
        final Object proxy = createProxy( myServiceClass.newInstance() );
        
        final Class<?> myContainerClass = loader.loadClass( MyContainer.class.getName() );
        final Object myContainer = myContainerClass.getConstructors()[0].newInstance( proxy );

        System.out.println("---------------- test ------------------");
        System.out.println(proxy.getClass());
        System.out.println(Arrays.asList(proxy.getClass().getInterfaces()));
        System.out.println(proxy.getClass().getSuperclass());
        System.out.println(Arrays.asList(proxy.getClass().getSuperclass().getInterfaces()));
        System.out.println("---------------- END test ------------------");
        
        final byte[] serialized = serialize(_kryo, myContainer);
        
        deserialize(_kryo, serialized, MyContainer.class);
        // If we reached this kryo was able to deserialize the proxy, so we're fine
    }

    @Test( enabled = true )
    public void testCGLibProxy() {
        final ClassToProxy proxy = createProxy( new ClassToProxy() );
        proxy.setValue( "foo" );
        
        final byte[] serialized = serialize(_kryo, proxy);
        final ClassToProxy deserialized = deserialize(_kryo, serialized, proxy.getClass() );
        Assert.assertEquals( deserialized.getValue(), proxy.getValue() );
    }

    /**
     * Test that a cglib proxy is handled correctly.
     */
    @Test( enabled = true )
    public void testCGLibProxyForExistingFormat() {
        final Map<String, String> proxy = createProxy( new HashMap<String, String>() );
        proxy.put( "foo", "bar" );
        Assert.assertEquals( proxy.get( "foo" ), "bar" );
        
        final byte[] serialized = serialize(_kryo, proxy);
        @SuppressWarnings( "unchecked" )
        final Map<String, String> deserialized = deserialize(_kryo, serialized, proxy.getClass() );
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

		@Override
		public String toString() {
			return "ClassToProxy [_value=" + _value + "]";
		}
    }
    
    public static class MySpecialContainer extends MyContainer {

        public MySpecialContainer( final MyService myService ) {
            super( myService );
        }
        
    }
    
    public static class MyContainer {
        
        private MyService _myService;

        public MyContainer( final MyService myService ) {
            _myService = myService;
        }

        public MyService getMyService() {
            return _myService;
        }

        public void setMyService( final MyService myService ) {
            _myService = myService;
        }
        
    }
    
    public static interface MyService {
        String sayHello();
    }
    
    public static class MyServiceImpl implements MyService {

        @Override
        public String sayHello() {
            return "hi";
        }
        
    }

}
