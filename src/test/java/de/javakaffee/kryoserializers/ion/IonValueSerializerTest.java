package de.javakaffee.kryoserializers.ion;

import com.amazon.ion.IonBlob;
import com.amazon.ion.IonBool;
import com.amazon.ion.IonClob;
import com.amazon.ion.IonDecimal;
import com.amazon.ion.IonInt;
import com.amazon.ion.IonList;
import com.amazon.ion.IonSexp;
import com.amazon.ion.IonString;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonSymbol;
import com.amazon.ion.IonSystem;
import com.amazon.ion.IonTimestamp;
import com.amazon.ion.IonValue;
import com.amazon.ion.Timestamp;
import com.amazon.ion.system.IonSystemBuilder;
import com.esotericsoftware.kryo.Kryo;
import org.testng.annotations.Test;

import java.util.Objects;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.AssertJUnit.assertEquals;

public class IonValueSerializerTest {
    private static IonSystem ION = IonSystemBuilder.standard().build();

    @Test
    public void testUnregistered() {
        Kryo kryo = new Kryo();
        kryo.addDefaultSerializer(IonValue.class, new IonValueSerializer(ION));
        kryo.setRegistrationRequired(false);

        testIonNullPrimitives(kryo);
        testIonPrimitives(kryo);
        testIonNullContainers(kryo);
        testIonEmptyContainers(kryo);
        testIonContainers(kryo);
        testIonLobs(kryo);
        testComposite(kryo);
    }

    @Test
    public void testRegisteredIonPrimitives() {
        final Kryo kryo = new Kryo();
        kryo.addDefaultSerializer(IonValue.class, new IonValueSerializer(ION));
        for (Class<? extends IonValue> cls : IonValueSerializer.getAllSubclasses()) {
            kryo.register(cls);
        }
        kryo.register(CompositeClass.class);
        kryo.setRegistrationRequired(true);

        testIonNullPrimitives(kryo);
        testIonPrimitives(kryo);
        testIonNullContainers(kryo);
        testIonEmptyContainers(kryo);
        testIonContainers(kryo);
        testIonLobs(kryo);
        testComposite(kryo);
    }

    private void testIonNullPrimitives(Kryo kryo) {
        roundTrip(kryo, ION.newNullInt(), IonInt.class);
        roundTrip(kryo, ION.newNullDecimal(), IonDecimal.class);
        roundTrip(kryo, ION.newNullBool(), IonBool.class);
        roundTrip(kryo, ION.newNullSymbol(), IonSymbol.class);
        roundTrip(kryo, ION.newNullString(), IonString.class);
        roundTrip(kryo, ION.newNullTimestamp(), IonTimestamp.class);
    }

    private void testIonPrimitives(Kryo kryo) {
        roundTrip(kryo, ION.newInt(10), IonInt.class);
        roundTrip(kryo, ION.newDecimal(10.09001), IonDecimal.class);
        roundTrip(kryo, ION.newBool(false), IonBool.class);
        roundTrip(kryo, ION.newSymbol("symbol"), IonSymbol.class);
        roundTrip(kryo, ION.newString("blah"), IonString.class);
        roundTrip(kryo, ION.newTimestamp(Timestamp.valueOf("2020-12-31T23:59:59.000Z")), IonTimestamp.class);
    }

    private void testIonNullContainers(Kryo kryo) {
        roundTrip(kryo, ION.newNullStruct(), IonStruct.class);
        roundTrip(kryo, ION.newNullList(), IonList.class);
        roundTrip(kryo, ION.newNullSexp(), IonSexp.class);
    }

    private void testIonEmptyContainers(Kryo kryo) {
        roundTrip(kryo, ION.newEmptyStruct(), IonStruct.class);
        roundTrip(kryo, ION.newEmptyList(), IonList.class);
        roundTrip(kryo, ION.newEmptySexp(), IonSexp.class);
    }

    private void testIonContainers(Kryo kryo) {
        IonStruct struct1 = ION.newEmptyStruct();
        struct1.add("i", ION.newInt(10));
        struct1.add("s", ION.newString("str"));
        struct1.add("ss", ION.newSexp(ION.newBool(true)));
        roundTrip(kryo, struct1, IonStruct.class);

        IonList list1 = ION.newEmptyList();
        list1.add(struct1.clone());
        roundTrip(kryo, list1, IonList.class);
    }

    private void testIonLobs(Kryo kryo) {
        byte[] bytes = "som random string".getBytes();
        IonBlob blob = ION.newBlob(bytes);
        roundTrip(kryo, blob, IonBlob.class);

        IonClob clob = ION.newClob(bytes);
        roundTrip(kryo, clob, IonClob.class);
    }

    private void testComposite(Kryo kryo) {
        CompositeClass composite = new CompositeClass();
        composite.name = "my name";
        composite.age = 13;
        composite.details = (IonStruct) ION.singleValue("{address: \"\", city: \"Seattle\", zip: 11111}");

        byte[] bytes = serialize(kryo, composite);
        CompositeClass result = deserialize(kryo, bytes, CompositeClass.class);
        assertEquals(composite, result);
    }

    private void roundTrip(Kryo kryo, IonValue value, Class<? extends IonValue> type) {
        byte[] bytes = serialize(kryo, value);
        IonValue result = deserialize(kryo, bytes, type);
        assertIonValue(value, result);
    }

    private void assertIonValue(IonValue expected, IonValue actual) {
        assertEquals(expected, actual);
    }

    static class CompositeClass {
        String name;
        int age;
        IonStruct details;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompositeClass that = (CompositeClass) o;
            return age == that.age &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(details, that.details);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age, details);
        }
    }
}
