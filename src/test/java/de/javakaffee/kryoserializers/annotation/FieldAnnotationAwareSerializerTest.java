package de.javakaffee.kryoserializers.annotation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class FieldAnnotationAwareSerializerTest {

    // Use Non-ASCII characters in order to be able to check the byte buffer for
    // the existence of the string values.
    protected static final String FIRST_VALUE = "åæø first value";
    protected static final String SECOND_VALUE = "äöü second value";

    private static final int BUFFER_SIZE = 1024;

    private CustomBean makeBean() {
        final CustomBean customBean = new CustomBean();
        customBean.setFirstValue(FIRST_VALUE);
        customBean.setSecondValue(SECOND_VALUE);
        return customBean;
    }

    private byte[] makeBuffer() {
        return new byte[BUFFER_SIZE];
    }

    @Test
    public void testExcludeFields() throws Exception {

        final Kryo kryo = new Kryo();
        final FieldAnnotationAwareSerializer<CustomBean> disregardingSerializer = new FieldAnnotationAwareSerializer<CustomBean>(kryo, CustomBean.class, true);
        disregardingSerializer.addAnnotation(CustomMark.class);
        kryo.addDefaultSerializer(CustomBean.class, disregardingSerializer);

        final byte[] buffer = makeBuffer();

        final CustomBean outputBean = makeBean();
        final Output output = new Output(buffer);
        kryo.writeObject(output, outputBean);

        final Input input = new Input(buffer);
        final CustomBean inputBean = kryo.readObject(input, CustomBean.class);

        assertEquals(inputBean.getSecondValue(), outputBean.getSecondValue());
        assertFalse(new String(buffer).contains(outputBean.getFirstValue()));
        assertTrue(new String(buffer).contains(outputBean.getSecondValue()));
        assertNull(inputBean.getFirstValue());
    }

    @Test
    public void testIncludeFields() throws Exception {

        final Kryo kryo = new Kryo();
        final FieldAnnotationAwareSerializer<CustomBean> regardingSerializer = new FieldAnnotationAwareSerializer<CustomBean>(kryo, CustomBean.class, false);
        regardingSerializer.addAnnotation(CustomMark.class);
        kryo.addDefaultSerializer(CustomBean.class, regardingSerializer);

        final byte[] buffer = makeBuffer();

        final CustomBean outputBean = makeBean();
        final Output output = new Output(buffer);
        kryo.writeObject(output, outputBean);

        final Input input = new Input(buffer);
        final CustomBean inputBean = kryo.readObject(input, CustomBean.class);

        assertEquals(inputBean.getFirstValue(), outputBean.getFirstValue());
        assertTrue(new String(buffer).contains(outputBean.getFirstValue()));
        assertFalse(new String(buffer).contains(outputBean.getSecondValue()));
        assertNull(inputBean.getSecondValue());
    }
    
    static class CustomBean {

        @CustomMark
        private String firstValue;

        private String secondValue;

        public String getSecondValue() {
            return secondValue;
        }

        public void setSecondValue(final String secondValue) {
            this.secondValue = secondValue;
        }

        public String getFirstValue() {
            return firstValue;
        }

        public void setFirstValue(final String firstValue) {
            this.firstValue = firstValue;
        }
    }
    
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    static @interface CustomMark {
    }

}
