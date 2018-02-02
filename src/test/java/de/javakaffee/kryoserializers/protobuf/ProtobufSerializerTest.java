/*
 * Copyright 2018 Martin Grotzke
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
package de.javakaffee.kryoserializers.protobuf;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.javakaffee.kryoserializers.protobuf.SampleProtoAOuterClass.SampleProtoA;
import de.javakaffee.kryoserializers.protobuf.SampleProtoBOuterClass.SampleProtoB;

@SuppressWarnings("unchecked")
public class ProtobufSerializerTest {

	private Kryo _kryo;

	@BeforeTest
	public void setUp() {
		_kryo = new Kryo();
		_kryo.register(SampleProtoA.class, new ProtobufSerializer());
		_kryo.register(SampleProtoB.class, new ProtobufSerializer());
	}

	/**
	 * Verifies that the Serializer works over a single Protobuf object
	 */
	@Test
	public void testSerializerWithProtoA() {
		Integer expectedMessageId = 12332;
		String expectedName = "Esteban";

		SampleProtoA protoA = createSampleProtoA(expectedMessageId, expectedName);

		// Attempt to serialize
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Output o = new Output(outStream, 4096);
		_kryo.getSerializer(protoA.getClass()).write(_kryo, o, protoA);

		// Attempt to deserialize
		Input i = new Input(new ByteArrayInputStream(outStream.toByteArray()), 4096);
		SampleProtoA verifyProtoA =
				(SampleProtoA) _kryo.getSerializer(protoA.getClass()).read(_kryo, i, SampleProtoA.class);

		// Verify it
		assertEquals(expectedMessageId, (Integer) verifyProtoA.getMessageId(), "MessageId is correct");
		assertEquals(expectedName, verifyProtoA.getName(), "Name is correct");
	}

	/**
	 * Verifies that the Serializer works back to back with multiple protobufs instances of the same type (SampleProtoA)
	 */
	@Test
	public void testSerializerWithMultipleInstances() {
		// Create first instance
		Integer expectedMessageId1 = 12332;
		String expectedName1 = "Esteban";
		SampleProtoA protoA1 = createSampleProtoA(expectedMessageId1, expectedName1);

		// Create 2nd instance
		Integer expectedMessageId2 = 531;
		String expectedName2 = "Bergo";
		SampleProtoA protoA2 = createSampleProtoA(expectedMessageId2, expectedName2);

		// Attempt to serialize instance 1
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Output o = new Output(outStream, 4096);
		_kryo.getSerializer(protoA1.getClass()).write(_kryo, o, protoA1);
		byte[] serializedProtoA1 = outStream.toByteArray();

		// Attempt to serialize instance 2
		outStream = new ByteArrayOutputStream();
		o = new Output(outStream, 4096);
		_kryo.getSerializer(protoA1.getClass()).write(_kryo, o, protoA2);
		byte[] serializedProtoA2 = outStream.toByteArray();

		// Attempt to deserialize instance 1
		Input i = new Input(new ByteArrayInputStream(serializedProtoA1), 4096);
		SampleProtoA testProtoA1 =
				(SampleProtoA) _kryo.getSerializer(protoA1.getClass()).read(_kryo, i, SampleProtoA.class);

		// Attempt to deserialize instance 2
		i = new Input(new ByteArrayInputStream(serializedProtoA2), 4096);
		SampleProtoA testProtoA2 =
				(SampleProtoA) _kryo.getSerializer(protoA1.getClass()).read(_kryo, i, SampleProtoA.class);

		// Verify instance 1
		assertEquals(expectedMessageId1, (Integer) testProtoA1.getMessageId(), "MessageId is correct");
		assertEquals(expectedName1, testProtoA1.getName(), "Name is correct");

		// Verify instance 2
		assertEquals(expectedMessageId2, (Integer) testProtoA2.getMessageId(), "MessageId is correct");
		assertEquals(expectedName2, testProtoA2.getName(), "Name is correct");
	}

	/**
	 * Verifies that the Serializer works back to back with multiple protobufs instances of different types (SampleProtoA and SampleProtoB)
	 */
	@Test
	public void testSerializerWithMultipleInstancesDifferentTypes() {
		// Create first instance
		Integer expectedMessageId = 12332;
		String expectedName = "Esteban";
		SampleProtoA protoA = createSampleProtoA(expectedMessageId, expectedName);

		// Create 2nd instance
		Integer expectedIdentifier = 543;
		String expectedCity = "Atlanta";
		String expectedState = "Georgia";
		SampleProtoB protoB = createSampleProtoB(expectedIdentifier, expectedCity, expectedState);

		// Attempt to serialize instance 1
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Output o = new Output(outStream, 4096);
		_kryo.getSerializer(protoA.getClass()).write(_kryo, o, protoA);
		byte[] serializedProtoA = outStream.toByteArray();

		// Attempt to serialize instance 2
		outStream = new ByteArrayOutputStream();
		o = new Output(outStream, 4096);
		_kryo.getSerializer(protoB.getClass()).write(_kryo, o, protoB);
		byte[] serializedProtoB = outStream.toByteArray();

		// Attempt to deserialize instance 1
		Input i = new Input(new ByteArrayInputStream(serializedProtoA), 4096);
		SampleProtoA testProtoA =
				(SampleProtoA) _kryo.getSerializer(protoA.getClass()).read(_kryo, i, SampleProtoA.class);

		// Attempt to deserialize instance 2
		i = new Input(new ByteArrayInputStream(serializedProtoB), 4096);
		SampleProtoB testProtoB =
				(SampleProtoB) _kryo.getSerializer(protoB.getClass()).read(_kryo, i, SampleProtoB.class);

		// Verify instance 1
		assertEquals(expectedMessageId, (Integer) testProtoA.getMessageId(), "MessageId is correct");
		assertEquals(expectedName, testProtoA.getName(), "Name is correct");

		// Verify instance 2
		assertEquals(expectedIdentifier, (Integer) testProtoB.getIdentifier(), "Identifier is correct");
		assertEquals(expectedCity, testProtoB.getCity(), "City is correct");
		assertEquals(expectedState, testProtoB.getState(), "State is correct");
	}

	@Test
	public void testGetAcceptsNull() {
		ProtobufSerializer serializer = new ProtobufSerializer();
		assertTrue(serializer.getAcceptsNull(), "Accepts null");
	}

	@Test
	public void testNull() {
		// Attempt to serialize null
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Output o = new Output(outStream, 4096);
		_kryo.getSerializer(SampleProtoA.class).write(_kryo, o, null);
		byte[] serializedNullValue = outStream.toByteArray();

		// Attempt to deserialize
		Input i = new Input(new ByteArrayInputStream(serializedNullValue), 4096);
		SampleProtoA testNullProto =
				(SampleProtoA) _kryo.getSerializer(SampleProtoA.class).read(_kryo, i, SampleProtoA.class);
		assertNull(testNullProto);
	}

	private SampleProtoA createSampleProtoA(Integer messageId, String name) {
		// Create builder
		SampleProtoA.Builder builder = SampleProtoA.newBuilder();
		builder.setMessageId(messageId);
		builder.setName(name);

		// Build protobuf
		return builder.build();
	}

	private SampleProtoB createSampleProtoB(Integer identifier, String city, String state) {
		// Create builder
		SampleProtoB.Builder builder = SampleProtoB.newBuilder();
		builder.setIdentifier(identifier);
		builder.setCity(city);
		builder.setState(state);

		// Build protobuf
		return builder.build();
	}
}
