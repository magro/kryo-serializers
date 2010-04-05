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
 * A kryo {@link Serializer} for {@link StringBuffer} that serializes the {@link String}
 * representation, so that not the internal <code>char</code> array is serialized. This
 * reduces the number of serialized bytes.
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class StringBufferSerializer extends SimpleSerializer<StringBuffer> {

    private final Kryo _kryo;

    public StringBufferSerializer( final Kryo kryo ) {
        _kryo = kryo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringBuffer read( final ByteBuffer buffer ) {
        return new StringBuffer( _kryo.readObject( buffer, String.class ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write( final ByteBuffer buffer, final StringBuffer sb ) {
        _kryo.writeObject( buffer, sb.toString() );
    }

}
