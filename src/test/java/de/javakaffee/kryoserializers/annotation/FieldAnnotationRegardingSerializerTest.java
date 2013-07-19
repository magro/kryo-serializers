package de.javakaffee.kryoserializers.annotation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class FieldAnnotationRegardingSerializerTest extends AbstractAnnotationAwareSerializerTest {

    private Kryo kryo;

    @BeforeTest
    protected void beforeTest() {
        kryo = new Kryo();
        FieldAnnotationRegardingSerializer.getMarks().add(CustomMark.class);
        kryo.addDefaultSerializer(CustomBean.class, FieldAnnotationRegardingSerializer.class);
    }

    @Test
    public void testSerialization() throws Exception {

        byte[] buffer = makeBuffer();

        CustomBean outputBean = makeBean();
        Output output = new Output(buffer);
        kryo.writeObject(output, outputBean);

        Input input = new Input(buffer);
        CustomBean inputBean = kryo.readObject(input, CustomBean.class);

        assertEquals(inputBean.getFirstValue(), outputBean.getFirstValue());
        assertTrue(new String(buffer).contains(outputBean.getFirstValue()));
        assertFalse(new String(buffer).contains(outputBean.getSecondValue()));
        assertNull(inputBean.getSecondValue());
    }
}
