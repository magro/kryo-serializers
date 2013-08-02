package de.javakaffee.kryoserializers; 

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ArraysAsListSerializerTest {

    private Kryo kryo;

    @BeforeClass
    public void setUp() {
        kryo = new Kryo();
        kryo.register(Arrays.asList("").getClass(), new de.javakaffee.kryoserializers.ArraysAsListSerializer());
    }

    @Test(enabled = true)
    public void testArrayAsList() {
        TestEntity o = new TestEntity();
        o.list = Arrays.asList(1L, 2L, 3L);

        Output output = new Output(128, -1);
        kryo.writeObject(output, o);
        byte[] bytes = output.getBuffer();

        Input input = new Input(bytes);
        TestEntity deSerialised = kryo.readObject(input, TestEntity.class);

        assertTrue(deSerialised.list instanceof List);
    }

    public static class TestEntity {
        private Object list;
    }
}
