package com.codingapi.springboot.framework.exception;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EventExceptionTest {

    @Test
    void test(){
        List<Exception> exceptions = new ArrayList<>();
        exceptions.add(new RuntimeException("123123"));
        exceptions.add(new RuntimeException("234234"));
        EventException eventException = new EventException(exceptions);
        assertThrows(EventException.class,()->{
           throw eventException;
        });

        eventException.printStackTrace();
    }
}
