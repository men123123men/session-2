package ru.sbt.jschool.session2;

public enum Shift {
    RIGHT(""), LEFT("-");

    Shift(String formatPart) {
        this.formatPart = formatPart;
    }
    String formatPart;
    public String getFormatPart(){
        return formatPart;
    }
}
