/*
 * Copyright 2015 Rennie Petersen
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
package de.javakaffee.kryoserializers.jodatime;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;

import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;

/**
 * Test for {@link JodaLocalDateSerializer}.
 *
 * @author <a href="mailto:rp@merlinia.com">Rennie Petersen</a>
 */
public class JodaLocalDateSerializerTest {

   private Kryo _kryo;

   @BeforeTest
   protected void beforeTest() {
      _kryo = new Kryo();
      _kryo.register(LocalDate.class, new JodaLocalDateSerializer());
   }

   @Test(enabled = true)
   public void testJodaLocalDate() {
      final LocalDate obj = new LocalDate().withDayOfYear(42);
      final byte[] serialized = serialize(_kryo, obj);
      final LocalDate deserialized = deserialize(_kryo, serialized, LocalDate.class);
      Assert.assertEquals(deserialized, obj);
   }

   @Test(enabled = true)
   public void testCopyJodaLocalDate() {
      final LocalDate obj = new LocalDate().withDayOfYear(42);
      final LocalDate copy = _kryo.copy(obj);
      Assert.assertEquals(copy, obj);
   }

}
