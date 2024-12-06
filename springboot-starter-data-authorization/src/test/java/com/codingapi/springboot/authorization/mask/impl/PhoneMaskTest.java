package com.codingapi.springboot.authorization.mask.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneMaskTest {

    @Test
    void test(){
        String phone = "15562581234";
        PhoneMask phoneMask = new PhoneMask();
        assertTrue(phoneMask.support(phone));
        assertEquals("155****1234", phoneMask.mask(phone));
    }

}
