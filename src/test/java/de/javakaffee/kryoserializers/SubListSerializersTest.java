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

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;

/**
 * Test for {@link SubListSerializers}.
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class SubListSerializersTest {
    
    private Kryo _kryo;

    @BeforeClass
    public void beforeClass() {
        _kryo = new KryoReflectionFactorySupport() {
            
            @Override
            @SuppressWarnings("rawtypes")
            public Serializer newSerializer(final Class<? extends Serializer> serializerClass, final Class type) {
                final Serializer<List<?>> subListSerializer = SubListSerializers.createFor(type);
                if ( subListSerializer != null ) {
                    return subListSerializer;
                }
                return super.newSerializer( serializerClass, type );
            }
        };
    }

    private void doTest(final List<TestEnum> subList) {
        final byte[] serialized = serialize( _kryo, subList );
        @SuppressWarnings( "unchecked" )
        final List<TestEnum> deserialized = deserialize( _kryo, serialized, subList.getClass() );

        assertEquals( deserialized, subList );
        assertEquals( deserialized.remove( 0 ), subList.remove( 0 ) );
    }

    @Test( enabled = true )
    public void testSubList () throws Exception {
        final List<TestEnum> subList = new LinkedList<TestEnum>( Arrays.asList( TestEnum.values() ) ).subList( 1, 2 );
        doTest(subList);
    }

    @Test( enabled = true )
    public void testSubListSubList () throws Exception {
        final List<TestEnum> subList = new LinkedList<TestEnum>( Arrays.asList( TestEnum.values() ) ).subList( 1, 3 ).subList(1, 2);
        doTest(subList);
    }

    @Test( enabled = true )
    public void testArrayListSubList () throws Exception {
        final List<TestEnum> subList = new ArrayList<TestEnum>( Arrays.asList( TestEnum.values() ) ).subList( 1, 2 );
        doTest(subList);
    }

    @Test( enabled = true )
    public void testArrayListSubListSubList () throws Exception {
        final List<TestEnum> subList = new ArrayList<TestEnum>( Arrays.asList( TestEnum.values() ) ).subList( 1, 3 ).subList(1, 2);
        doTest(subList);
    }

    @Test( enabled = true )
    public void testArrayListSubListWithSharedItems () throws Exception {
        final ArrayList<String> mylist = new ArrayList<String>();
        mylist.add("1");
        mylist.add("1");
        mylist.add("2");
        mylist.add("1");
        mylist.add("1");
        final List<String> subList = mylist.subList(0, 5);

        final byte[] serialized = serialize( _kryo, subList );
        @SuppressWarnings( "unchecked" )
        final List<String> deserialized = deserialize( _kryo, serialized, subList.getClass() );

        assertEquals( deserialized, subList );
        assertEquals( deserialized, mylist );
    }
    
    static enum TestEnum {
        ITEM1, ITEM2, ITEM3;
    }

}