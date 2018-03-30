package ru.sbt.jschool.session2;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OutputFormatter {
    {Locale.setDefault(Locale.FRANCE); }

    private PrintStream out;

    public OutputFormatter(PrintStream out) {
        this.out = out;
    }

    public void output(String[] names, Object[][] data) {
        Map<Class,TypeFormater> typeFormaters = new HashMap<>();

        typeFormaters.put(Integer.class, new TypeFormater<Integer>(new DecimalFormat("###,##0")::format,"-",Shift.RIGHT));
        typeFormaters.put( Double.class, new TypeFormater<Double>(new DecimalFormat("###,##0.00")::format,"-",Shift.RIGHT));
        typeFormaters.put( String.class, new TypeFormater<String>(s->s.length()>15? s.substring(0,12)+"...":s,"-",Shift.LEFT));
        typeFormaters.put(   Date.class, new TypeFormater<Date>(new SimpleDateFormat("dd.MM.yyyy HH:mm.ss.SSS")::format,"-",Shift.RIGHT));
        typeFormaters.put(LocalDateTime.class, new TypeFormater<LocalDateTime>(ldt-> ldt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm.ss.SSS")),"-",Shift.RIGHT));

        // !!!! чтобы проходили старые тесты нужно раскомментировать эти две строки
        //typeFormaters.put( String.class, new TypeFormater<String>(s->s,"-",Shift.LEFT));
        //typeFormaters.put(   Date.class, new TypeFormater<Date>(t->String.format("%td.%<tm.00%<ty",t),"-",Shift.RIGHT));

        TableData tableData = new TableData(names,data);
        TableFormaner tableFormaner = new TableFormaner(tableData,typeFormaters);

        out.print(tableFormaner.getBigString());
    }
}