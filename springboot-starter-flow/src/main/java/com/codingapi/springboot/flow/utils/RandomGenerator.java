package com.codingapi.springboot.flow.utils;

import java.util.UUID;

public class RandomGenerator {

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }


    public static String randomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = (int) (Math.random() * str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
