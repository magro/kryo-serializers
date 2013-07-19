package de.javakaffee.kryoserializers.annotation;

public abstract class AbstractAnnotationAwareSerializerTest {

    // Use Non-ASCII characters in order to be able to check the byte buffer for
    // the existence of the string values.
    protected static final String FIRST_VALUE = "åæø first value";
    protected static final String SECOND_VALUE = "äöü second value";

    private static final int BUFFER_SIZE = 1024;

    protected CustomBean makeBean() {
        CustomBean customBean = new CustomBean();
        customBean.setFirstValue(FIRST_VALUE);
        customBean.setSecondValue(SECOND_VALUE);
        return customBean;
    }

    protected byte[] makeBuffer() {
        return new byte[BUFFER_SIZE];
    }
}
