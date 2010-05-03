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

import sun.reflect.ReflectionFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.ReferenceFieldSerializer;

/**
 * A {@link ReferenceFieldSerializer} specialization that uses sun's {@link ReflectionFactory} to create
 * new instances for classes without a default constructor (via reuse of
 * {@link KryoReflectionFactorySupport#newInstanceFromReflectionFactory(Class)}).
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class ReferenceFieldSerializerReflectionFactorySupport extends ReferenceFieldSerializer {

    /**
     * Creates a new instance.
     * @param kryo the kryo instance that is passed to {@link ReferenceFieldSerializer#ReferenceFieldSerializer(Kryo, Class)}.
     * @param type the type to serialize.
     */
    public ReferenceFieldSerializerReflectionFactorySupport( final Kryo kryo, final Class<?> type ) {
        super( kryo, type );
    }
    
    @Override
    public <T> T newInstance( final Kryo kryo, final Class<T> type ) {
        return KryoReflectionFactorySupport.newInstanceFromReflectionFactory( type );
    }
    
}