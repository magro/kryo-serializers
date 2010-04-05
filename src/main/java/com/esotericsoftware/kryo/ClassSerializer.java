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
package com.esotericsoftware.kryo;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.serialize.SimpleSerializer;

/**
 * A kryo {@link Serializer} for fields of type {@link Class}.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class ClassSerializer extends SimpleSerializer<Class<?>> {

    private final Kryo _kryo;

    public ClassSerializer( final Kryo kryo ) {
        _kryo = kryo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> read( final ByteBuffer buffer ) {
        return _kryo.readClass( buffer ).getClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write( final ByteBuffer buffer, final Class<?> clazz ) {
        _kryo.writeClass( buffer, clazz );
    }

}
