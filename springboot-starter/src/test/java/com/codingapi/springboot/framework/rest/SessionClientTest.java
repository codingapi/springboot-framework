package com.codingapi.springboot.framework.rest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class SessionClientTest {

    @Test
    void test(){
        SessionClient client = new SessionClient();
        String html = client.getHtml("https://www.baidu.com");
        assertNotNull(html);
    }
}
