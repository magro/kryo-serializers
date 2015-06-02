package de.javakaffee.kryoserializers.protobuf;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import de.javakaffee.kryoserializers.protobuf.SampleProtoAOuterClass.SampleProtoA;
import de.javakaffee.kryoserializers.protobuf.SampleProtoBOuterClass.SampleProtoB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.testng.Assert.*;

public class ProtobufSerializerTest {

    private Kryo _kryo;

    @BeforeTest
    public void setUp() throws Exception {
        _kryo = new Kryo();
        _kryo.register(SampleProtoA.class, new ProtobufSerializer());
        _kryo.register(SampleProtoB.class, new ProtobufSerializer());
    }

    @Test
    /**
     * Verifies that the Serializer works over a single Protobuf object
     */
    public void testSerializerWithProtoA() throws Exception {
        Integer expectedMessageId = 12332;
        String expectedName = "Esteban";

        SampleProtoA protoA = createSampleProtoA(expectedMessageId, expectedName);

        // Attempt to serialize
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output o = new Output(outStream, 4096);
        _kryo.getSerializer(protoA.getClass()).write(_kryo, o, protoA);

        // Attempt to deserialize
        Input i = new Input(new ByteArrayInputStream(outStream.toByteArray()), 4096);
        SampleProtoA verifyProtoA = (SampleProtoA) _kryo.getSerializer(protoA.getClass()).read(_kryo, i, SampleProtoA.class);

        // Verify it
        assertEquals((Integer)expectedMessageId, (Integer)verifyProtoA.getMessageId(), "MessageId is correct");
        assertEquals((String)expectedName, (String)verifyProtoA.getName(), "Name is correct");
    }

    @Test
    /**
     * Verifies that the Serializer works back to back with multiple protobufs instances of the same type (SampleProtoA)
     */
    public void testSerializerWithMultipleInstances() throws Exception {
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
        SampleProtoA testProtoA1 = (SampleProtoA) _kryo.getSerializer(protoA1.getClass()).read(_kryo, i, SampleProtoA.class);

        // Attempt to deserialize instance 2
        i = new Input(new ByteArrayInputStream(serializedProtoA2), 4096);
        SampleProtoA testProtoA2 = (SampleProtoA) _kryo.getSerializer(protoA1.getClass()).read(_kryo, i, SampleProtoA.class);

        // Verify instance 1
        assertEquals((Integer)expectedMessageId1, (Integer)testProtoA1.getMessageId(), "MessageId is correct");
        assertEquals((String)expectedName1, (String)testProtoA1.getName(), "Name is correct");

        // Verify instance 2
        assertEquals((Integer)expectedMessageId2, (Integer)testProtoA2.getMessageId(), "MessageId is correct");
        assertEquals((String)expectedName2, (String)testProtoA2.getName(), "Name is correct");
    }

    @Test
    /**
     * Verifies that the Serializer works back to back with multiple protobufs instances of different types (SampleProtoA and SampleProtoB)
     */
    public void testSerializerWithMultipleInstancesDifferentTypes() throws Exception {
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
        ProtobufSerializer protoBSerializer = new ProtobufSerializer<SampleProtoB>();
        _kryo.getSerializer(protoB.getClass()).write(_kryo, o, protoB);
        byte[] serializedProtoB = outStream.toByteArray();

        // Attempt to deserialize instance 1
        Input i = new Input(new ByteArrayInputStream(serializedProtoA), 4096);
        SampleProtoA testProtoA = (SampleProtoA) _kryo.getSerializer(protoA.getClass()).read(_kryo, i, SampleProtoA.class);

        // Attempt to deserialize instance 2
        i = new Input(new ByteArrayInputStream(serializedProtoB), 4096);
        SampleProtoB testProtoB = (SampleProtoB) _kryo.getSerializer(protoB.getClass()).read(_kryo, i, SampleProtoB.class);

        // Verify instance 1
        assertEquals((Integer)expectedMessageId, (Integer)testProtoA.getMessageId(), "MessageId is correct");
        assertEquals((String)expectedName, (String)testProtoA.getName(), "Name is correct");

        // Verify instance 2
        assertEquals((Integer)expectedIdentifier, (Integer)testProtoB.getIdentifier(), "Identifier is correct");
        assertEquals((String)expectedCity, (String)testProtoB.getCity(), "City is correct");
        assertEquals((String)expectedState, (String)testProtoB.getState(), "State is correct");
    }

    @Test
    public void testGetAcceptsNull() throws Exception {
        ProtobufSerializer serializer = new ProtobufSerializer();
        assertTrue(serializer.getAcceptsNull(), "Accepts null");
    }

    @Test
    public void testNull() {
        SampleProtoA sampleProtoA = null;

        // Attempt to serialize null
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output o = new Output(outStream, 4096);
        _kryo.getSerializer(SampleProtoA.class).write(_kryo, o, sampleProtoA);
        byte[] serializedNullValue = outStream.toByteArray();

        // Attempt to deserialize
        Input i = new Input(new ByteArrayInputStream(serializedNullValue), 4096);
        SampleProtoA testNullProto = (SampleProtoA) _kryo.getSerializer(SampleProtoA.class).read(_kryo, i, SampleProtoA.class);
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