package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class FieldAnnotationDisregardingSerializerTest extends AbstractAnnotationAwareSerializerTest {

    private Kryo kryo;

    @BeforeTest
    protected void beforeTest() {
        kryo = new Kryo();
        FieldAnnotationDisregardingSerializer.getMarks().add(CustomMark.class);
        kryo.addDefaultSerializer(CustomBean.class, FieldAnnotationDisregardingSerializer.class);
    }

    @Test
    public void testSerialization() throws Exception {

        byte[] buffer = makeBuffer();

        CustomBean outputBean = makeBean();
        Output output = new Output(buffer);
        kryo.writeObject(output, outputBean);

        Input input = new Input(buffer);
        CustomBean inputBean = kryo.readObject(input, CustomBean.class);

        assertEquals(inputBean.getSecondValue(), outputBean.getSecondValue());
        assertFalse(new String(buffer).contains(outputBean.getFirstValue()));
        assertTrue(new String(buffer).contains(outputBean.getSecondValue()));
        assertNull(inputBean.getFirstValue());
    }
}
