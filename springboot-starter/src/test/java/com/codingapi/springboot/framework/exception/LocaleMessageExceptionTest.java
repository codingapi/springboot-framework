package com.codingapi.springboot.framework.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LocaleMessageExceptionTest {

    @Test
    void test(){
        LocaleMessageException exception = new LocaleMessageException("api.error");
        assertEquals(exception.getErrMessage(),"this is an error info");
    }

}