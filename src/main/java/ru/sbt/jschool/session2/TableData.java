package ru.sbt.jschool.session2;

public class TableData {
    private final String[] names;
    private final Object[][] data;

    public String[] getNames() {
        return names;
    }

    public Object[][] getData() {
        return data;
    }

    public TableData(String[] names, Object[][] data) {
        this.names = names;
        this.data = data;
    }
}
