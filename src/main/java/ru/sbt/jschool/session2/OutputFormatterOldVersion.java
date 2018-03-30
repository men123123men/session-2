package ru.sbt.jschool.session2;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

public class OutputFormatterOldVersion {

	{Locale.setDefault(Locale.FRANCE);}

    private PrintStream out;
    private DecimalFormat forDouble = new DecimalFormat("###,##0.00");
    private DecimalFormat forInteger = new DecimalFormat("###,##0");

    public OutputFormatterOldVersion(PrintStream out) {
        this.out = out;
    }

    public void output(String[] names, Object[][] data) {

        int height = data.length; //  first index   высота   по оси Y
        int width = names.length; // second index   ширина   по оси Х

        int[] elementLettersCount = Arrays.stream(names)
                .mapToInt(String::length)
                .toArray();

        String currentLineFormater = null;
        String[][] dataStr = new String[height][width];

        if (height>0) {
            Class[] classes = Arrays.stream(data[0])
                    .map(Object::getClass)
                    .toArray(Class[]::new);

            Function<Object, String> currentObjectToStringFunction;
            String currentStr;

            for (int i = 0; i < width; i++) {      // итерируемся по столбцам (Object[j][i])
                currentObjectToStringFunction = getObjectToStringFunction(classes[i]);
                for (int j = 0; j < height; j++) {  // итерируемся по значениям в столбце
                    currentStr = currentObjectToStringFunction.apply(data[j][i]);
                    if (currentStr.length() > elementLettersCount[i])
                        elementLettersCount[i] = currentStr.length();
                    dataStr[j][i] = currentStr;
                }
            }
            currentLineFormater = makeLineFormater(elementLettersCount, classes);
        }

        String linesSeparator = makeLineSeparator(elementLettersCount, "-", "+");
        String header = makeHeader(names, elementLettersCount);

        out.printf("%s%n%s%n%1$s%n",linesSeparator,header);

        for (int i = 0; i < dataStr.length; i++) {
            out.printf(currentLineFormater, dataStr[i]);
            out.println(linesSeparator);
        }
    }

    private String makeHeader(String[] names, int[] elementLettersCount) {
        StringBuilder result = new StringBuilder("|");
        int index;
        for(int i=0;i<names.length;i++){
            index = elementLettersCount[i]-(elementLettersCount[i]-names[i].length())/2;
            result.append(String.format("%"+elementLettersCount[i]+"s" ,String.format("%-"+index+"s",names[i])));
            result.append("|");
        }
        return result.toString();
    }

    private Function<Object,String> getObjectToStringFunction(Class clazz){
        switch (clazz.getSimpleName()){
            case "Integer": return o-> o==null ? "-" : forInteger.format(o);
            case "Double":  return o-> o==null ? "-" : forDouble.format(o);
            case "String":  return o-> o==null ? "-" : String.format("%s",o);
            case "Date":    return o-> o==null ? "-" : String.format("%td.%<tm.00%<ty",o);
            default: return Object::toString;
        }
    }

    private static String makeLineSeparator(int[] lengths,String lineElement, String crosshair){
        StringBuilder resultLine = new StringBuilder(crosshair);
        for(int wigthOfCurrentColumn:lengths){
            for(int i=0;i<wigthOfCurrentColumn;i++)
                resultLine.append(lineElement);
            resultLine.append(crosshair);
        }
        return resultLine.toString();
    }

    private static String makeLineFormater(int[] lengths,Class[] classes){
        if (lengths.length!=classes.length)
            throw new RuntimeException("Где-то ошибка");
        StringBuilder result = new StringBuilder("|");
        for (int i=0;i<classes.length;i++)
            result.append("%"+(classes[i]==String.class?"-":"")+lengths[i]+"s|");
        return result.append("%n").toString();
    }
}
