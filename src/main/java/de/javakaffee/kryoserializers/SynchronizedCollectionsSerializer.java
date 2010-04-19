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

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serialize.IntSerializer;

/**
 * A kryo {@link Serializer} for synchronized {@link Collection}s and {@link Map}s
 * created via {@link Collections}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class SynchronizedCollectionsSerializer extends Serializer {
    
    private static final Field SOURCE_COLLECTION_FIELD;
    private static final Field SOURCE_MAP_FIELD;
    
    static {
        try {
            SOURCE_COLLECTION_FIELD = Class.forName("java.util.Collections$SynchronizedCollection" )
                .getDeclaredField( "c" );
            SOURCE_COLLECTION_FIELD.setAccessible( true );

            SOURCE_MAP_FIELD = Class.forName("java.util.Collections$SynchronizedMap" )
                .getDeclaredField( "m" );
            SOURCE_MAP_FIELD.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( "Could not access source collection" +
                    " field in java.util.Collections$SynchronizedCollection.", e );
        }
    }
    
    private final Kryo _kryo;
    
    /**
     * @param kryo the kryo instance
     */
    public SynchronizedCollectionsSerializer( final Kryo kryo ) {
        _kryo = kryo;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public <T> T readObjectData( final ByteBuffer buffer, final Class<T> clazz ) {
        final int ordinal = IntSerializer.get( buffer, true );
        final SynchronizedCollection collection = SynchronizedCollection.values()[ordinal];
        try {
            final Object sourceCollection = _kryo.readClassAndObject( buffer );
            return (T) collection.create( sourceCollection );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObjectData( final ByteBuffer buffer, final Object object ) {
        try {
            final SynchronizedCollection collection = SynchronizedCollection.valueOfType( object.getClass() );
            // the ordinal could be replaced by s.th. else (e.g. a explicitely managed "id")
            IntSerializer.put( buffer, collection.ordinal(), true );
            _kryo.writeClassAndObject( buffer, collection.sourceCollectionField.get( object ) );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }
    
    private static enum SynchronizedCollection {
        COLLECTION( Collections.synchronizedCollection( Arrays.asList( "" ) ).getClass(), SOURCE_COLLECTION_FIELD ){
            @Override
            public Object create( final Object sourceCollection ) {
                return Collections.synchronizedCollection( (Collection<?>) sourceCollection );
            }
        },
        RANDOM_ACCESS_LIST( Collections.synchronizedList( new ArrayList<Void>() ).getClass(), SOURCE_COLLECTION_FIELD ){
            @Override
            public Object create( final Object sourceCollection ) {
                return Collections.synchronizedList( (List<?>) sourceCollection );
            }
        },
        LIST( Collections.synchronizedList( new LinkedList<Void>() ).getClass(), SOURCE_COLLECTION_FIELD ){
            @Override
            public Object create( final Object sourceCollection ) {
                return Collections.synchronizedList( (List<?>) sourceCollection );
            }
        },
        SET( Collections.synchronizedSet( new HashSet<Void>() ).getClass(), SOURCE_COLLECTION_FIELD ){
            @Override
            public Object create( final Object sourceCollection ) {
                return Collections.synchronizedSet( (Set<?>) sourceCollection );
            }
        },
        SORTED_SET( Collections.synchronizedSortedSet( new TreeSet<Void>() ).getClass(), SOURCE_COLLECTION_FIELD ){
            @Override
            public Object create( final Object sourceCollection ) {
                return Collections.synchronizedSortedSet( (SortedSet<?>) sourceCollection );
            }
        },
        MAP( Collections.synchronizedMap( new HashMap<Void, Void>() ).getClass(), SOURCE_MAP_FIELD ) {

            @Override
            public Object create( final Object sourceCollection ) {
                return Collections.synchronizedMap( (Map<?, ?>) sourceCollection );
            }
            
        },
        SORTED_MAP( Collections.synchronizedSortedMap( new TreeMap<Void, Void>() ).getClass(), SOURCE_MAP_FIELD ) {
            @Override
            public Object create( final Object sourceCollection ) {
                return Collections.synchronizedSortedMap( (SortedMap<?, ?>) sourceCollection );
            }
        };
        
        private final Class<?> type;
        private final Field sourceCollectionField;
        
        private SynchronizedCollection( final Class<?> type, final Field sourceCollectionField ) {
            this.type = type;
            this.sourceCollectionField = sourceCollectionField;
        }
        
        /**
         * @param sourceCollection
         */
        public abstract Object create( Object sourceCollection );

        static SynchronizedCollection valueOfType( final Class<?> type ) {
            for( final SynchronizedCollection item : values() ) {
                if ( item.type.equals( type ) ) {
                    return item;
                }
            }
            throw new IllegalArgumentException( "The type " + type + " is not supported." );
        }
        
    }

    /**
     * Creates a new {@link SynchronizedCollectionsSerializer} and registers its serializer
     * for the several synchronized Collections that can be created via {@link Collections},
     * including {@link Map}s.
     * 
     * @param kryo the {@link Kryo} instance to set the serializer on.
     * 
     * @see Collections#synchronizedCollection(Collection)
     * @see Collections#synchronizedList(List)
     * @see Collections#synchronizedSet(Set)
     * @see Collections#synchronizedSortedSet(SortedSet)
     * @see Collections#synchronizedMap(Map)
     * @see Collections#synchronizedSortedMap(SortedMap)
     */
    public static void registerSerializers( final Kryo kryo ) {
        final SynchronizedCollectionsSerializer serializer = new SynchronizedCollectionsSerializer( kryo );
        SynchronizedCollection.values();
        for ( final SynchronizedCollection item : SynchronizedCollection.values() ) {
            kryo.register( item.type, serializer );
        }
    }

}
