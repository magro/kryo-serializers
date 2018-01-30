package de.javakaffee.kryoserializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.factories.SerializerFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * A test case for the {@link FieldAnnotationAwareSerializer}.
 *
 * @author <a href="mailto:rafael.wth@web.de">Rafael Winterhalter</a>
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class FieldAnnotationAwareSerializerTest {

    // Use Non-ASCII characters in order to be able to check the byte buffer for
    // the existence of the string values.
    protected static final String FIRST_VALUE = "\u00e5\u00e6\u00f8 first value";
    protected static final String SECOND_VALUE = "\u00e4\u00f6\u00fc second value";

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
        @SuppressWarnings("unchecked")
		final SerializerFactory disregardingSerializerFactory = new FieldAnnotationAwareSerializer.Factory(
                Arrays.<Class<? extends Annotation>>asList(CustomMark.class), true);
        kryo.addDefaultSerializer(CustomBean.class, disregardingSerializerFactory);

        final byte[] buffer = makeBuffer();

        final CustomBean outputBean = makeBean();
        final Output output = new Output(buffer);
        kryo.writeObject(output, outputBean);

        final Input input = new Input(buffer);
        final CustomBean inputBean = kryo.readObject(input, CustomBean.class);

        String decodedBuffer = UTF_8.decode(ByteBuffer.wrap(buffer)).toString();

        assertEquals(inputBean.getSecondValue(), outputBean.getSecondValue());
        assertFalse(decodedBuffer.contains(outputBean.getFirstValue()));
        assertTrue(decodedBuffer.contains(outputBean.getSecondValue()));
        assertNull(inputBean.getFirstValue());
    }

    @Test
    public void testIncludeFields() throws Exception {

        final Kryo kryo = new Kryo();
        @SuppressWarnings("unchecked")
		final SerializerFactory regardingSerializerFactory = new FieldAnnotationAwareSerializer.Factory(
                Arrays.<Class<? extends Annotation>>asList(CustomMark.class), false);
        kryo.addDefaultSerializer(CustomBean.class, regardingSerializerFactory);

        final byte[] buffer = makeBuffer();

        final CustomBean outputBean = makeBean();
        final Output output = new Output(buffer);
        kryo.writeObject(output, outputBean);

        final Input input = new Input(buffer);
        final CustomBean inputBean = kryo.readObject(input, CustomBean.class);

        String decodedBuffer = UTF_8.decode(ByteBuffer.wrap(buffer)).toString();

        assertEquals(inputBean.getFirstValue(), outputBean.getFirstValue());
        assertTrue(decodedBuffer.contains(outputBean.getFirstValue()));
        assertFalse(decodedBuffer.contains(outputBean.getSecondValue()));
        assertNull(inputBean.getSecondValue());
    }

    private static class CustomBean {

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
    private static @interface CustomMark {
    }
}
