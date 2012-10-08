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

import java.util.Collections;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A kryo {@link Serializer} for {@link List}s created via {@link Collections#singletonList(Object)}.
 * <p>
 * Note: This serializer does not support cyclic references, if a serialized object
 * is part of a cycle this might cause an error during deserialization.
 * </p>
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class CollectionsSingletonListSerializer extends Serializer<List<?>> {

    @Override
    public List<?> read(final Kryo kryo, final Input input, final Class<List<?>> type) {
        final Object obj = kryo.readClassAndObject( input );
        return Collections.singletonList( obj );
    }

    @Override
    public void write(final Kryo kryo, final Output output, final List<?> list) {
        kryo.writeClassAndObject(output, list.get( 0 ));
    }


}
