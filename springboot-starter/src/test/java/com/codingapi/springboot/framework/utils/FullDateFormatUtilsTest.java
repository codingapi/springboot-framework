package com.codingapi.springboot.framework.utils;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class FullDateFormatUtilsTest {

    @Test
    void convert() {
        long time =  1663590528793L;
        String data =FullDateFormatUtils.convert(time);
        assertEquals(data,"2022-09-19 20:28:48");
    }
}