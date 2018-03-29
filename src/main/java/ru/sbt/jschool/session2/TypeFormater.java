package ru.sbt.jschool.session2;

import java.util.Objects;
import java.util.function.Function;

public class TypeFormater<T>{
    private Function<T,String> function;
    private String defaultValue;
    private Shift shift;

    public TypeFormater(Function<T, String> function, String defaultValue, Shift shift) {
        this.function = function;
        this.defaultValue = defaultValue;
        this.shift = shift;
    }

    public String format(T value) {
        return Objects.isNull(value)? defaultValue : function.apply(value);
    }

    public Function<T, String> getFunction() {
        return function;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public Shift getShift() {
        return shift;
    }
}
