/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.sbt.jschool.session2;


import java.io.FileInputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;

/**
 */
public class OutputFormatter {
	{Locale.setDefault(Locale.FRANCE);}
    private PrintStream out;
    public OutputFormatter(PrintStream out) {
        this.out = out;
    }

    public void output(String[] names, Object[][] data) {
        //TODO: implement me.

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

            Function<Object, String> current_ObjectToString_Function;
            String currentStr;

            for (int i = 0; i < width; i++) {      // итерируемся по столбцам (Object[j][i])
                current_ObjectToString_Function = get_ObjectToString_Function(classes[i]);
                for (int j = 0; j < height; j++) {  // итерируемся по значениям в столбце
                    currentStr = current_ObjectToString_Function.apply(data[j][i]);
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

    private DecimalFormat forDouble = new DecimalFormat("###,##0.00");
    private DecimalFormat forInteger = new DecimalFormat("###,##0");
    		
    		;
    private Function<Object,String> get_ObjectToString_Function(Class clazz){
        switch (clazz.getSimpleName()){
            case "Integer": return o-> o==null ? "-" : forInteger.format(o);
//            	String.format("%,d",o).replace(',',' ');
            case "Double":  return o-> o==null ? "-" :forDouble.format(o);
//            	String.format("%,.2f",Double.valueOf(o.toString())).replace(',',' ').replace('.',',');
            case "String":  return o-> o==null ? "-" :String.format("%s",o);
            case "Date":    return o-> o==null ? "-" :String.format("%td.%<tm.00%<ty",o);
            default: return Object::toString;
        }
    }
    /**
     * @param lengths     массив с требуемыми ширинами колонок
     * @param lineElement элементы из которых будет конструироваться линия
     * @param crosshair   элемент на перекрестии
     * @return String of lineElement divided by crosshair.
     */
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



    public static void main(String[] args) throws Exception {
//        OutputFormatter formatter = new OutputFormatter(System.out);
//        for(int i = 0;i<3;i++)
//            test(formatter,i);
    }
    private static Object format(String str, String type) throws ParseException {
        if ("".equals(str))
            return null;

        switch (type) {
            case "string":
                return str;
            case "number":
                return Integer.valueOf(str);
            case "date":
                return new SimpleDateFormat("dd.MM.yyyy").parse(str);
            case "money":
                return Double.valueOf(str);
        }

        throw new RuntimeException("Unknown data type: " + type);
    }
    private static void test(OutputFormatter exeplair, int fileNumber) throws Exception {

        String pathStr = "/Users/admin/eclipse-workspace/session-2/src/test/resources/"+fileNumber+"/input.csv";
        System.out.println(pathStr);

        Scanner sc = new Scanner(new FileInputStream(pathStr));
        int size = Integer.valueOf(sc.nextLine());      // число строк
        String[] types = sc.nextLine().split(",");      // типы столбцов (number,string,money,date)
        String[] names = sc.nextLine().split(",");      // шапка таблиц(#,Дата,Число,ФИО,Зарплата )  приходит первым аргументом
        Object[][] data = new Object[size][];           // колличество строк в 2D массиве берется из файла
        for (int i=0; i<size; i++) {
            String[] strLine = sc.nextLine().split(",", -1);
            Object[] line = new Object[strLine.length];
            for (int j = 0; j < strLine.length; j++)
                line[j] = format(strLine[j], types[j]);
            data[i] = line;
        }
        exeplair.output(names, data);
    }
}
