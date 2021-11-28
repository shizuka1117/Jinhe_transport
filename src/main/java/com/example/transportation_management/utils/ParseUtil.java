package com.example.transportation_management.utils;

import org.neo4j.driver.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 使用泛型解析原生查询的返回值
 */
public class ParseUtil {

    public static <T> T solveValue(Value value, Class T){
        T t =  (T)value.asObject();
        return t;
    }

    public static <T> List<T> solveValues(Value value, Class T){
        List<T> res = new LinkedList<>();
        for(Value subValue:value.values()){
            res.add(solveValue(subValue, T));
        }
        return res;
    }

    public static Long getInterval(String begin, String end){
        Calendar cal1=Calendar.getInstance();
        cal1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(begin.split(":")[0]));
        cal1.set(Calendar.MINUTE, Integer.parseInt(begin.split(":")[1]));
        Calendar cal2=Calendar.getInstance();
        cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(end.split(":")[0]));
        cal2.set(Calendar.MINUTE, Integer.parseInt(end.split(":")[1]));
        return (cal2.getTime().getTime()-cal1.getTime().getTime())/1000/60;
    }

    public static String addTime(String begin, String interval){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(begin.split(":")[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(begin.split(":")[1]));
        cal.add(Calendar.MINUTE, Integer.parseInt(interval));
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int m = cal.get(Calendar.MINUTE);
        return h<10?"0"+h+":"+m:h+":"+m;
    }

    public static String parseLineName(String name){
        if(name.contains("路"))
            name = name.split("路")[0];
        return name;
    }

    public static String parseStationName(String name){
        if(name.contains("站"))
            name = name.split("站")[0];
        return name;
    }

    public static String parseTime(String time){
        if(time.split(":")[0].length()<2)
            time = "0"+time;
        return time;
    }
}
