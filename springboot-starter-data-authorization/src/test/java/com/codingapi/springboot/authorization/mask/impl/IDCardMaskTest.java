package com.codingapi.springboot.authorization.mask.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IDCardMaskTest {

    @Test
    void test(){
        String idCard = "110101199003074012";
        IDCardMask mask = new IDCardMask();
        assertTrue(mask.support(idCard));
        assertEquals("110101********4012", mask.mask(idCard));
    }
}
