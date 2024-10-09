package com.codingapi.springboot.flow.utils;

import java.util.UUID;

public class IDGenerator {

    public static String generator() {
        return UUID.randomUUID().toString();
    }

}
