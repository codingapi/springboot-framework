package com.codingapi.springboot.framework.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageRequestTest {

    @Test
    void test(){
        System.setProperty(PageRequest.CURRENT_FIX_VALUE,"1");
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(2);

        assertEquals(pageRequest.getPageNumber(),1);
    }
}