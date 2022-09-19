package com.codingapi.springboot.framework.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

public class FullDateFormatUtils {
    private final static SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String convert(long timestamp){
        return convert(new Date(timestamp));
    }

    public static String convert(Date date){
        return defaultFormat.format(date);
    }
}

