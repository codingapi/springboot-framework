package com.codingapi.springboot.framework.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FullDateFormatUtils {
    private final static SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        defaultFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    }

    public static String convert(long timestamp){
        return convert(new Date(timestamp));
    }

    public static String convert(Date date){
        return defaultFormat.format(date);
    }
}

