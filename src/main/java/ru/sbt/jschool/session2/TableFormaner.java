package ru.sbt.jschool.session2;

import java.util.Map;
import java.util.stream.Stream;

public class TableFormaner<T> {
    private String[] names;
    private Object[][] data;
    private Map<Class<T>, TypeFormater<T>> typeFormateres;

    public TableFormaner(TableData tableData, Map<Class<T>, TypeFormater<T>> typeFormateres) {
        names = tableData.getNames();
        data = tableData.getData();
        this.typeFormateres = typeFormateres;
    }

    private  String[] prepaerColaum(int index){
        String[] result = new String[data.length+2];
        int columnWidth = names[index].length();
        result[1] = names[index];

        String element;

        if (data.length>0){
            TypeFormater<T> typeFormater = typeFormateres.get(data[0][index].getClass());
            for(int i=0;i<data.length;i++){
                element = typeFormater.format((T)data[i][index]);
                result[i+2] = element;
                if (element.length()>columnWidth)
                    columnWidth = element.length();
            }
            result[1] = changeHeaderElement(result[1],columnWidth);
            String stringWhithFormat = getStringFormaterOfColumnElement(columnWidth,typeFormater.getShift());
            for(int i=1;i<result.length;i++)
                result[i] = String.format(stringWhithFormat,result[i]);
        }
        char lineSeparatorAtom = '-';
        StringBuilder lineSeparatorStrBuilder = new StringBuilder();
        for (int i=0;i<columnWidth;i++)
            lineSeparatorStrBuilder.append(lineSeparatorAtom);
        result[0] = lineSeparatorStrBuilder.toString();

        return result;

    }

    private String changeHeaderElement(String oldHeader, int size){
        int index = (size+oldHeader.length())/2;
        return String.format("%-"+size+"s" ,String.format("%"+index+"s",oldHeader));
    }

    private String getStringFormaterOfColumnElement(int columnWidth, Shift shift){
        return "%"+shift.formatPart+columnWidth+"s";
    }

    private String[][] getFormatedTableElements(){
        if(data.length==0){
            String[] lineSeparatorParts = Stream.of(names).map(s->s.replaceAll(".","-")).toArray(String[]::new);
            return new String[][]{lineSeparatorParts,names};
        }

        String[][] transposeResult = new String[data[0].length][];
        for(int i = 0;i<transposeResult.length;i++)
            transposeResult[i] = prepaerColaum(i);
        return transpose2DArray(transposeResult);
    }

    public String getBigString(){
        String [][] input = getFormatedTableElements();
        StringBuilder result = new StringBuilder();
        String crosshair = "+";

        StringBuilder lineSeparator = new StringBuilder(crosshair);
        for (String linePatrt:input[0]) {
            lineSeparator.append(linePatrt);
            lineSeparator.append(crosshair);
        }
        lineSeparator.append("\n");
        result.append(lineSeparator);

        for (int i = 1;i<input.length;i++){
            result.append("|");
            for(int j=0;j<input[i].length;j++) {
                result.append(input[i][j]);
                result.append("|");
            }
            result.append("\n");
            result.append(lineSeparator);
        }
        return result.toString();
    }

    // how to make it parametread?
    public static String[][] transpose2DArray(String[][] input){
        String[][] result = new String[input[0].length][input.length];
        for(int i=0;i<input.length;i++)
            for(int j=0;j<input[i].length;j++)
                result[j][i] = input[i][j];
        return result;
    }
}
