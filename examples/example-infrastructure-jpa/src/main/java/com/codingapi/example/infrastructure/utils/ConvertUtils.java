package com.codingapi.example.infrastructure.utils;

import java.util.Arrays;
import java.util.List;

public class ConvertUtils {

    public static List<String> string2List(String value) {
        return Arrays.asList(value.split(","));
    }

    public static String list2String(List<String> value) {
        return String.join(",", value);
    }


}
