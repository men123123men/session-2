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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 2. Модифицировать программу для возможности добавления новых выводимых типов данных
 Выделить интерфейс, который отвечает за конкретный тип данных.
 Какие у него должны быть методы?

 * Добавить поддержку типа данных TIMESTAMP. Вывод даты в формате dd.MM.yyyy HH:mm.SS.sss
 * Добавить поддержку типа данных CUT_STR. Вывод строки фиксированной длины. Для более длиных строк в конце добавляется ...
 Пример. Максимальная длина - 15
 Hello, World!    - строка "Hello, World!" влезает
 Hello, beatif... - строка "Hello, beatifull World!" не влезает
 1234556789012345 - строка из ровано 15 символов влезает
 *
 *
 *
 */
public class OutputFormatter2 {
    {Locale.setDefault(Locale.FRANCE); }

    private PrintStream out;

    public OutputFormatter2(PrintStream out) {
        this.out = out;
    }

    public void output(String[] names, Object[][] data) {
        Map<Class,TypeFormater> typeFormaters = new HashMap<>();

        typeFormaters.put(Integer.class, new TypeFormater<Integer>(new DecimalFormat("###,##0")::format,"-",Shift.RIGHT));
        typeFormaters.put( Double.class, new TypeFormater<Double>(new DecimalFormat("###,##0.00")::format,"-",Shift.RIGHT));
        typeFormaters.put( String.class, new TypeFormater<String>(s->s.length()>15? s.substring(0,12)+"...":s,"-",Shift.LEFT));
        typeFormaters.put(   Date.class, new TypeFormater<Date>(t->String.format("%td.%<tm.00%<ty",t),"-",Shift.RIGHT));
        typeFormaters.put(LocalDateTime.class, new TypeFormater<LocalDateTime>(ldt-> ldt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm.ss.SSS")),"-",Shift.RIGHT));
        //typeFormaters.put( String.class, new TypeFormater<String>(s->s,"-",Shift.LEFT));

        TableData tableData = new TableData(names,data);
        TableFormaner tableFormaner = new TableFormaner(tableData,typeFormaters);

        out.print(tableFormaner.getBigString());
    }

//        Map<String,TypeFormater> formaterMap = new HashMap<>();

//        private DecimalFormat forDouble = new DecimalFormat("###,##0.00");
//        private DecimalFormat forInteger = new DecimalFormat("###,##0");
//        switch (clazz.getSimpleName()){
//            case "Integer": return o-> o==null ? "-" : forInteger.format(o);
//            case "Double":  return o-> o==null ? "-" : forDouble.format(o);
//            case "String":  return o-> o==null ? "-" : String.format("%s",o);
//            case "Date":    return o-> o==null ? "-" : String.format("%td.%<tm.00%<ty",o);
//            default: return Object::toString;
//        }
    public static void main(String[] args){

        OutputFormatter2 formatter = new OutputFormatter2(System.out);
        int i = 4;
//        for(int i = 0;i<5;i++)
            try {
                test(formatter, i);
            } catch (Exception e){
                System.err.println(i+" ERROR!");
            }
//        Date date = new Date();
//        System.out.println(date);




        LocalDate date = LocalDate.now();
        // стандартный формат даты
        System.out.println("стандартный формат даты для LocalDate : " + date);
        // приименяем свой формат даты
        System.out.println(date.format(DateTimeFormatter.ofPattern("d::MMM::uuuu")));
        System.out.println(date.format(DateTimeFormatter.BASIC_ISO_DATE));


        LocalDateTime dateTime = LocalDateTime.now();
        //стандартный формат даты
        System.out.println("стандартный формат даты LocalDateTime : " + dateTime);
        //приименяем свой формат даты
        System.out.println(dateTime.format(DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss")));
        System.out.println(dateTime.format(DateTimeFormatter.BASIC_ISO_DATE));

        Instant timestamp = Instant.now();
        //стандартный формат даты
        System.out.println("стандартный формат : " + timestamp);

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss");
        OffsetDateTime odt = OffsetDateTime.of(2015, 11, 2, 12, 38, 0, 123456789, ZoneOffset.UTC);
        System.out.println(odt.format(formatter1));

        String string = "Hello, beatifull World!";
        System.out.println(string.length()>15? string.substring(0,12)+"...":string);



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
    private static void test(OutputFormatter2 exeplair, int fileNumber) throws Exception {

        String pathStr = "/Users/aslanbek/IdeaProjects/session-2/src/test/resources/"+fileNumber+"/input.csv";
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