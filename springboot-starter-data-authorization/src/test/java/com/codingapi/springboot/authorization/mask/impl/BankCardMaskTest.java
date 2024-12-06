package com.codingapi.springboot.authorization.mask.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankCardMaskTest {

    @Test
    void test() {
        String bankCard = "6222021001111111111";
        BankCardMask mask = new BankCardMask();
        assertTrue(mask.support(bankCard));
        assertEquals("622202*********1111", mask.mask(bankCard));
    }
}
