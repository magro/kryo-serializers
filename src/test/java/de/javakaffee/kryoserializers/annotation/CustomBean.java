package de.javakaffee.kryoserializers.annotation;

public class CustomBean {

    @CustomMark
    private String firstValue;

    private String secondValue;

    public String getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(String secondValue) {
        this.secondValue = secondValue;
    }

    public String getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(String firstValue) {
        this.firstValue = firstValue;
    }
}
