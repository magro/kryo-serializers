/*
 * $Id$
 * (c) Copyright 2009 freiheit.com technologies GmbH
 *
 * Created on Apr 4, 2010 by Martin Grotzke (martin.grotzke@freiheit.com)
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies GmbH. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies GmbH.
 */
package com.esotericsoftware.kryo;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.esotericsoftware.kryo.serialize.IntSerializer;

/**
 * A kryo {@link Serializer} for lists created via {@link Arrays#asList(Object...)}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
@SuppressWarnings( "unchecked" )
public class ArraysAsListSerializer extends Serializer {

    private final Kryo _kryo;
    private Field _arrayField;

    public ArraysAsListSerializer( final Kryo kryo ) {
        _kryo = kryo;
        try {
            _arrayField = Class.forName( "java.util.Arrays$ArrayList" ).getDeclaredField( "a" );
            _arrayField.setAccessible( true );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T readObjectData( final ByteBuffer buffer, final Class<T> clazz ) {
        final int length = IntSerializer.get( buffer, true );
        final Class<?> componentType = _kryo.readClass( buffer ).getType();
        try {
            final Object[] items = (Object[]) Array.newInstance( componentType, length );
            for( int i = 0; i < length; i++ ) {
                items[i] = _kryo.readClassAndObject( buffer );
            }
            return (T) Arrays.asList( items );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObjectData( final ByteBuffer buffer, final Object obj ) {
         try {
            final Object[] array = (Object[]) _arrayField.get( obj );
            IntSerializer.put( buffer, array.length, true );
            final Class<?> componentType = array.getClass().getComponentType();
            _kryo.writeClass( buffer, componentType );
            for( final Object item : array ) {
                _kryo.writeClassAndObject( buffer, item );
            }
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
    }

}
