package com.codingapi.springboot.framework.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageRequestTest {

    @Test
    void test(){
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(2);
        pageRequest.setPageSize(10);

        assertEquals(pageRequest.getCurrent(),1);
        assertEquals(pageRequest.getPageSize(),10);
    }



}