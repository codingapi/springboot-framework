package com.codingapi.springboot.flow.utils;

import java.util.UUID;

public class IDGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
