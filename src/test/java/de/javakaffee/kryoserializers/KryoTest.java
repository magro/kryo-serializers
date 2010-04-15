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

import static de.javakaffee.kryoserializers.TestClasses.createPerson;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.mutable.MutableInt;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import sun.reflect.ReflectionFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ObjectBuffer;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serialize.BigDecimalSerializer;
import com.esotericsoftware.kryo.serialize.BigIntegerSerializer;
import com.esotericsoftware.kryo.serialize.ReferenceFieldSerializer;

import de.javakaffee.kryoserializers.TestClasses.ClassWithoutDefaultConstructor;
import de.javakaffee.kryoserializers.TestClasses.Container;
import de.javakaffee.kryoserializers.TestClasses.CounterHolder;
import de.javakaffee.kryoserializers.TestClasses.CounterHolderArray;
import de.javakaffee.kryoserializers.TestClasses.Email;
import de.javakaffee.kryoserializers.TestClasses.HashMapWithIntConstructorOnly;
import de.javakaffee.kryoserializers.TestClasses.Holder;
import de.javakaffee.kryoserializers.TestClasses.HolderArray;
import de.javakaffee.kryoserializers.TestClasses.HolderList;
import de.javakaffee.kryoserializers.TestClasses.MyContainer;
import de.javakaffee.kryoserializers.TestClasses.Person;
import de.javakaffee.kryoserializers.TestClasses.SomeInterface;
import de.javakaffee.kryoserializers.TestClasses.Person.Gender;

/**
 * Test for {@link Kryo} serialization.
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class KryoTest {

    private static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();
    private static final Object[] INITARGS = new Object[0];
    
    private Kryo _kryo;

    @BeforeTest
    protected void beforeTest() {
        _kryo = new Kryo() {
            
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
            private <T> T newInstanceFromReflectionFactory( final Class<T> type ) {
                try {
                    final Constructor<?> constructor = REFLECTION_FACTORY.newConstructorForSerialization( type, Object.class.getDeclaredConstructor( new Class[0] ) );
                    constructor.setAccessible( true );
                    return (T) constructor.newInstance( INITARGS );
                } catch ( final Exception e ) {
                    throw new RuntimeException( e );
                }
            }
            
            private <T> T newInstanceFromNoArgsConstructor( final Class<T> type ) {
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
            private <T> Constructor<T> getNoArgsConstructor( final Class<T> type ) {
                final Constructor<?>[] constructors = type.getConstructors();
                for ( final Constructor<?> constructor : constructors ) {
                    if ( constructor.getParameterTypes().length == 0 ) {
                        return (Constructor<T>) constructor;
                    }
                }
                return null;
            }
        };
        _kryo.setRegistrationOptional( true );
        _kryo.register( Arrays.asList( "" ).getClass(), new ArraysAsListSerializer( _kryo ) );
        _kryo.register( Currency.class, new CurrencySerializer( _kryo ) );
        _kryo.register( StringBuffer.class, new StringBufferSerializer( _kryo ) );
        _kryo.register( StringBuilder.class, new StringBuilderSerializer( _kryo ) );
        _kryo.register( Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer() );
        _kryo.register( Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer() );
        _kryo.register( Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer() );
        _kryo.register( Class.class, new ClassSerializer( _kryo ) );
        _kryo.register( BigDecimal.class, new BigDecimalSerializer() );
        _kryo.register( BigInteger.class, new BigIntegerSerializer() );
        _kryo.register( GregorianCalendar.class, new GregorianCalendarSerializer() );
        _kryo.register( InvocationHandler.class, new JdkProxySerializer( _kryo ) );
        UnmodifiableCollectionsSerializer.registerSerializers( _kryo );
    }

    @Test( enabled = true )
    public void testGregorianCalendar() throws Exception {
        final Holder<Calendar> cal = new Holder<Calendar>( Calendar.getInstance( Locale.ENGLISH ) );
        System.out.println( "have size: " + serialize( Calendar.getInstance( Locale.ENGLISH ) ).length );
        @SuppressWarnings( "unchecked" )
        final Holder<Calendar> deserialized = deserialize( serialize( cal ), Holder.class );
        assertDeepEquals( deserialized, cal );
        
        assertEquals( deserialized.item.getTimeInMillis(), cal.item.getTimeInMillis() );
        assertEquals( deserialized.item.getTimeZone(), cal.item.getTimeZone() );
        assertEquals( deserialized.item.getMinimalDaysInFirstWeek(), cal.item.getMinimalDaysInFirstWeek() );
        assertEquals( deserialized.item.getFirstDayOfWeek(), cal.item.getFirstDayOfWeek() );
        assertEquals( deserialized.item.isLenient(), cal.item.isLenient() );
    }

    @Test( enabled = true )
    public void testStringBuffer() throws Exception {
        final StringBuffer stringBuffer = new StringBuffer( "<stringbuffer>with some content \n& some lines...</stringbuffer>" );
        final StringBuffer deserialized = deserialize( serialize( stringBuffer ), StringBuffer.class );
        assertDeepEquals( deserialized, stringBuffer );
    }

    @Test( enabled = true )
    public void testStringBuilder() throws Exception {
        final StringBuilder stringBuilder = new StringBuilder( "<stringbuilder>with some content \n& some lines...</stringbuilder>" );
        final StringBuilder deserialized = deserialize( serialize( stringBuilder ), StringBuilder.class );
        assertDeepEquals( deserialized, stringBuilder );
    }
    
    @Test( enabled = true )
    public void testMapWithIntConstructorOnly() throws Exception {
        final HashMapWithIntConstructorOnly map = new HashMapWithIntConstructorOnly( 5 );
        final HashMapWithIntConstructorOnly deserialized =
                deserialize( serialize( map ), HashMapWithIntConstructorOnly.class );
        assertDeepEquals( deserialized, map );

    }
    
    @Test( enabled = true )
    public void testCurrency() throws Exception {
        final Currency currency = Currency.getInstance( "EUR" );
        final Currency deserialized =
                deserialize( serialize( currency ), Currency.class );
        assertDeepEquals( deserialized, currency );

        // Check that the transient field defaultFractionDigits is initialized correctly
        Assert.assertEquals( deserialized.getCurrencyCode(), currency.getCurrencyCode() );
        Assert.assertEquals( deserialized.getDefaultFractionDigits(), currency.getDefaultFractionDigits() );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilCollectionsUnmodifiableList() throws Exception {
        final Holder<List<String>> unmodifiableList = new Holder<List<String>>( Collections.unmodifiableList( new ArrayList<String>( Arrays.asList( "foo", "bar" ) ) ) );
        final Holder<List<String>> deserialized = deserialize( serialize( unmodifiableList ), Holder.class );
        assertDeepEquals( deserialized, unmodifiableList );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilCollectionsUnmodifiableMap() throws Exception {
        final HashMap<String, String> m = new HashMap<String, String>();
        m.put( "foo", "bar" );
        final Holder<Map<String, String>> unmodifiableMap = new Holder<Map<String, String>>( Collections.unmodifiableMap( m ) );
        final Holder<Map<String, String>> deserialized = deserialize( serialize( unmodifiableMap ), Holder.class );
        assertDeepEquals( deserialized, unmodifiableMap );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilCollectionsEmptyList() throws Exception {
        final Holder<List<String>> emptyList = new Holder<List<String>>( Collections.<String>emptyList() );
        final Holder<List<String>> deserialized = deserialize( serialize( emptyList ), Holder.class );
        assertDeepEquals( deserialized, emptyList );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilCollectionsEmptySet() throws Exception {
        final Holder<Set<String>> emptyList = new Holder<Set<String>>( Collections.<String>emptySet() );
        final Holder<Set<String>> deserialized = deserialize( serialize( emptyList ), Holder.class );
        assertDeepEquals( deserialized, emptyList );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilCollectionsEmptyMap() throws Exception {
        final Holder<Map<String, String>> emptyMap = new Holder<Map<String, String>>( Collections.<String, String>emptyMap() );
        final Holder<Map<String, String>> deserialized = deserialize( serialize( emptyMap ), Holder.class );
        assertDeepEquals( deserialized, emptyMap );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilArraysAsListEmpty() throws Exception {
        final Holder<List<String>> asListHolder = new Holder( Arrays.asList() );
        final Holder<List<String>> deserialized = deserialize( serialize( asListHolder ), Holder.class );
        assertDeepEquals( deserialized, asListHolder );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilArraysAsListPrimitiveArrayElement() throws Exception {
        final int[] values = { 1, 2 };
        final Holder<List<String>> asListHolder = new Holder( Arrays.asList( values ) );
        final Holder<List<String>> deserialized = deserialize( serialize( asListHolder ), Holder.class );
        assertDeepEquals( deserialized, asListHolder );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilArraysAsListString() throws Exception {
        final Holder<List<String>> asListHolder = new Holder( Arrays.asList( "foo", "bar" ) );
        final Holder<List<String>> deserialized = deserialize( serialize( asListHolder ), Holder.class );
        assertDeepEquals( deserialized, asListHolder );
    }
    
    @SuppressWarnings( "unchecked" )
    @Test( enabled = true )
    public void testJavaUtilArraysAsListEmail() throws Exception {
        final Holder<List<Email>> asListHolder = new Holder( Arrays.asList( new Email( "foo", "foo@example.org" ) ) );
        final Holder<List<Email>> deserialized = deserialize( serialize( asListHolder ), Holder.class );
        assertDeepEquals( deserialized, asListHolder );
    }

    @Test( enabled = true )
    public void testJdkProxy() throws Exception {
        final Holder<SomeInterface> bean = new Holder<SomeInterface>( TestClasses.createProxy() );
        @SuppressWarnings( "unchecked" )
        final Holder<SomeInterface> deserialized = deserialize( serialize( bean ), Holder.class );
        assertDeepEquals( deserialized, bean );
    }

    @Test( enabled = true )
    public void testClassSerializer() throws Exception {
        final Holder<Class<?>> clazz = new Holder<Class<?>>( String.class );
        @SuppressWarnings( "unchecked" )
        final Holder<Class<?>> deserialized = deserialize( serialize( clazz ), Holder.class );
        assertDeepEquals( deserialized, clazz );
    }

    @Test( enabled = true )
    public void testInnerClass() throws Exception {
        // seems to be related to #15
        final Container container = TestClasses.createContainer();
        final Container deserialized = deserialize( serialize( container ), Container.class );
        assertDeepEquals( deserialized, container );
    }

    @Test( enabled = true )
    public <T> void testSharedObjectIdentity_CounterHolder() throws Exception {

        final AtomicInteger sharedObject = new AtomicInteger( 42 );
        final CounterHolder holder1 = new CounterHolder( sharedObject );
        final CounterHolder holder2 = new CounterHolder( sharedObject );
        final CounterHolderArray holderHolder = new CounterHolderArray( holder1, holder2 );
        
        final CounterHolderArray deserialized = deserialize( serialize( holderHolder ), CounterHolderArray.class );
        assertDeepEquals( deserialized, holderHolder );
        Assert.assertTrue( deserialized.holders[0].item == deserialized.holders[1].item );

    }

    @DataProvider( name = "sharedObjectIdentityProvider" )
    protected Object[][] createSharedObjectIdentityProviderData() {
        return new Object[][] {
                { AtomicInteger.class.getSimpleName(), new AtomicInteger( 42 ) },
                { Email.class.getSimpleName(), new Email( "foo bar", "foo.bar@example.com" ) } };
    }

    @SuppressWarnings( "unchecked" )
    @Test( enabled = true, dataProvider = "sharedObjectIdentityProvider" )
    public <T> void testSharedObjectIdentityWithArray( final String name, final T sharedObject ) throws Exception {
        final Holder<T> holder1 = new Holder<T>( sharedObject );
        final Holder<T> holder2 = new Holder<T>( sharedObject );
        final HolderArray<T> holderHolder = new HolderArray<T>( holder1, holder2 );
        
        final HolderArray<T> deserialized = deserialize( serialize( holderHolder ), HolderArray.class );
        assertDeepEquals( deserialized, holderHolder );
        Assert.assertTrue( deserialized.holders[0].item == deserialized.holders[1].item );
    }

    @SuppressWarnings( "unchecked" )
    @Test( enabled = true, dataProvider = "sharedObjectIdentityProvider" )
    public <T> void testSharedObjectIdentity( final String name, final T sharedObject ) throws Exception {
        final Holder<T> holder1 = new Holder<T>( sharedObject );
        final Holder<T> holder2 = new Holder<T>( sharedObject );
        final HolderList<T> holderHolder = new HolderList<T>( new ArrayList<Holder<T>>( Arrays.asList( holder1, holder2 ) ) );
        
        final HolderList<T> deserialized = deserialize( serialize( holderHolder ), HolderList.class );
        assertDeepEquals( deserialized, holderHolder );
        Assert.assertTrue( deserialized.holders.get( 0 ).item == deserialized.holders.get( 1 ).item );
    }

    @DataProvider( name = "typesAsSessionAttributesProvider" )
    protected Object[][] createTypesAsSessionAttributesData() {
        return new Object[][] {
                { Boolean.class, Boolean.TRUE },
                { String.class, "42" },
                { StringBuilder.class, new StringBuilder( "42" ) },
                { StringBuffer.class, new StringBuffer( "42" ) },
                { Class.class, String.class },
                { Long.class, new Long( 42 ) },
                { Integer.class, new Integer( 42 ) },
                { Character.class, new Character( 'c' ) },
                { Byte.class, new Byte( "b".getBytes()[0] ) },
                { Double.class, new Double( 42d ) },
                { Float.class, new Float( 42f ) },
                { Short.class, new Short( (short) 42 ) },
                { BigDecimal.class, new BigDecimal( 42 ) },
                { AtomicInteger.class, new AtomicInteger( 42 ) },
                { AtomicLong.class, new AtomicLong( 42 ) },
                { MutableInt.class, new MutableInt( 42 ) },
                { Integer[].class, new Integer[] { 42 } },
                { Date.class, new Date( System.currentTimeMillis() - 10000 ) },
                { Calendar.class, Calendar.getInstance() },
                { Currency.class, Currency.getInstance( "EUR" ) },
                { ArrayList.class, new ArrayList<String>( Arrays.asList( "foo" ) ) },
                { int[].class, new int[] { 1, 2 } },
                { long[].class, new long[] { 1, 2 } },
                { short[].class, new short[] { 1, 2 } },
                { float[].class, new float[] { 1, 2 } },
                { double[].class, new double[] { 1, 2 } },
                { int[].class, new int[] { 1, 2 } },
                { byte[].class, "42".getBytes() },
                { char[].class, "42".toCharArray() },
                { String[].class, new String[] { "23", "42" } },
                { Person[].class, new Person[] { createPerson( "foo bar", Gender.MALE, 42 ) } } };
    }

    @Test( enabled = true, dataProvider = "typesAsSessionAttributesProvider" )
    public <T> void testTypesAsSessionAttributes( final Class<T> type, final T instance ) throws Exception {
        @SuppressWarnings( "unchecked" )
        final T deserialized = (T) deserialize( serialize( instance ), instance.getClass() );
        assertDeepEquals( deserialized, instance );
    }

    @Test( enabled = true )
    public void testTypesInContainerClass() throws Exception {
        final MyContainer myContainer = new MyContainer();
        final MyContainer deserialized = deserialize( serialize( myContainer ), MyContainer.class );
        assertDeepEquals( deserialized, myContainer );
    }

    @Test( enabled = true )
    public void testClassWithoutDefaultConstructor() throws Exception {
        final ClassWithoutDefaultConstructor obj = TestClasses.createClassWithoutDefaultConstructor( "foo" );
        final ClassWithoutDefaultConstructor deserialized = deserialize( serialize( obj ), ClassWithoutDefaultConstructor.class );
        assertDeepEquals( deserialized, obj );
    }

    @Test( enabled = true )
    public void testPrivateClass() throws Exception {
        final Holder<?> holder = new Holder<Object>( TestClasses.createPrivateClass( "foo" ) );
        final Holder<?> deserialized = deserialize( serialize( holder ), Holder.class );
        assertDeepEquals( deserialized, holder );
    }

    @Test( enabled = true )
    public void testCollections() throws Exception {
        final EntityWithCollections obj = new EntityWithCollections();
        final EntityWithCollections deserialized = deserialize( serialize( obj ), EntityWithCollections.class );
        assertDeepEquals( deserialized, obj );
    }

    @Test( enabled = true )
    public void testCyclicDependencies() throws Exception {
        final Person p1 = createPerson( "foo bar", Gender.MALE, 42, "foo.bar@example.org", "foo.bar@example.com" );
        final Person p2 = createPerson( "bar baz", Gender.FEMALE, 42, "bar.baz@example.org", "bar.baz@example.com" );
        p1.addFriend( p2 );
        p2.addFriend( p1 );

        final Person deserialized = deserialize( serialize( p1 ), Person.class );
        assertDeepEquals( deserialized, p1 );
    }

    public static class EntityWithCollections {
        private final String[] _bars;
        private final List<String> _foos;
        private final Map<String, Integer> _bazens;

        public EntityWithCollections() {
            _bars = new String[] { "foo", "bar" };
            _foos = new ArrayList<String>( Arrays.asList( "foo", "bar" ) );
            _bazens = new HashMap<String, Integer>();
            _bazens.put( "foo", 1 );
            _bazens.put( "bar", 2 );
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( _bars );
            result = prime * result + ( ( _bazens == null )
                ? 0
                : _bazens.hashCode() );
            result = prime * result + ( ( _foos == null )
                ? 0
                : _foos.hashCode() );
            return result;
        }

        @Override
        public boolean equals( final Object obj ) {
            if ( this == obj ) {
                return true;
            }
            if ( obj == null ) {
                return false;
            }
            if ( getClass() != obj.getClass() ) {
                return false;
            }
            final EntityWithCollections other = (EntityWithCollections) obj;
            if ( !Arrays.equals( _bars, other._bars ) ) {
                return false;
            }
            if ( _bazens == null ) {
                if ( other._bazens != null ) {
                    return false;
                }
            } else if ( !_bazens.equals( other._bazens ) ) {
                return false;
            }
            if ( _foos == null ) {
                if ( other._foos != null ) {
                    return false;
                }
            } else if ( !_foos.equals( other._foos ) ) {
                return false;
            }
            return true;
        }
    }
    
    private void assertDeepEquals( final Object one, final Object another ) throws Exception {
        assertDeepEquals( one, another, new IdentityHashMap<Object, Object>() );
    }

    private void assertDeepEquals( final Object one, final Object another, final Map<Object, Object> alreadyChecked )
        throws Exception {
        if ( one == another ) {
            return;
        }
        if ( one == null && another != null || one != null && another == null ) {
            Assert.fail( "One of both is null: " + one + ", " + another );
        }
        if ( alreadyChecked.containsKey( one ) ) {
            return;
        }
        alreadyChecked.put( one, another );

        Assert.assertEquals( one.getClass(), another.getClass() );
        if ( one.getClass().isPrimitive() || one instanceof String || one instanceof Character || one instanceof Boolean
                || one instanceof Class<?> ) {
            Assert.assertEquals( one, another );
            return;
        }

        if ( Map.class.isAssignableFrom( one.getClass() ) ) {
            final Map<?, ?> m1 = (Map<?, ?>) one;
            final Map<?, ?> m2 = (Map<?, ?>) another;
            Assert.assertEquals( m1.size(), m2.size() );
            for ( final Map.Entry<?, ?> entry : m1.entrySet() ) {
                assertDeepEquals( entry.getValue(), m2.get( entry.getKey() ) );
            }
            return;
        }

        if ( Number.class.isAssignableFrom( one.getClass() ) ) {
            Assert.assertEquals( ( (Number) one ).longValue(), ( (Number) another ).longValue() );
            return;
        }

        if ( one instanceof Currency ) {
            // Check that the transient field defaultFractionDigits is initialized correctly (that was issue #34)
            final Currency currency1 = ( Currency) one;
            final Currency currency2 = ( Currency) another;
            Assert.assertEquals( currency1.getCurrencyCode(), currency2.getCurrencyCode() );
            Assert.assertEquals( currency1.getDefaultFractionDigits(), currency2.getDefaultFractionDigits() );
        }

        Class<? extends Object> clazz = one.getClass();
        while ( clazz != null ) {
            assertEqualDeclaredFields( clazz, one, another, alreadyChecked );
            clazz = clazz.getSuperclass();
        }

    }

    private void assertEqualDeclaredFields( final Class<? extends Object> clazz, final Object one, final Object another,
            final Map<Object, Object> alreadyChecked ) throws Exception, IllegalAccessException {
        for ( final Field field : clazz.getDeclaredFields() ) {
            field.setAccessible( true );
            if ( !Modifier.isTransient( field.getModifiers() ) ) {
                assertDeepEquals( field.get( one ), field.get( another ), alreadyChecked );
            }
        }
    }

    protected byte[] serialize( final Object o ) {
        if ( o == null ) {
            throw new NullPointerException( "Can't serialize null" );
        }
        return new ObjectBuffer(_kryo).writeObject( o );
        
    }

    protected <T> T deserialize( final byte[] in, final Class<T> clazz ) {
        return new ObjectBuffer( _kryo ).readObject( in, clazz );
    }

}
