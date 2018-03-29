package ru.sbt.jschool.session2;


import java.util.Map;
import java.util.stream.Stream;

public class TableFormaner {
    private String[] names;
    private Object[][] data;
    private Map<Class, TypeFormater> typeFormateres;

    public TableFormaner(TableData tableData, Map<Class, TypeFormater> typeFormateres) {
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
            TypeFormater<Object> typeFormater = typeFormateres.get(data[0][index].getClass());
//            Function<Object,String> function = typeFormater.getFunction();
            for(int i=0;i<data.length;i++){
                element = typeFormater.format(data[i][index]);
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
            String[][] strings = {lineSeparatorParts,names};
            return strings;
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
    private static String[][] transpose2DArray(String[][] input){
        String[][] result = new String[input[0].length][input.length];
        for(int i=0;i<input.length;i++)
            for(int j=0;j<input[i].length;j++)
                result[j][i] = input[i][j];
        return result;
    }




    public static void main(String[] args) {

//        String[][] strings = {{"1","2","3"},{"4","5","6"},{"7","8","9"}};

        String[] innerStrings = {"1","2","3"};
        String[][] strings = {innerStrings};
//        String[][] transposeStrings = transpose2DArray(strings);
//
//        arrayPrinter2D(strings);
//        arrayPrinter2D(transposeStrings);

    }
    public static void arrayPrinter2D(String[][] strings){
        for (String[] strs: strings) {
            for(String str:strs)
                System.out.print(str+" ");
            System.out.println();
        }
        System.out.println();
    }
}
