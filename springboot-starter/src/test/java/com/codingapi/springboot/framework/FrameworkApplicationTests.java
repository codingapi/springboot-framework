package com.codingapi.springboot.framework;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class FrameworkApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void hello() throws Exception {
        mockMvc.perform(get("/open/hello").contentType(MediaType.APPLICATION_JSON)).andExpect(result -> {
            JSONObject jsonObject = JSONObject.parseObject(result.getResponse().getContentAsString());
            assertEquals(jsonObject.getJSONObject("data").getString("hello"),"hello");
        });
    }

}
