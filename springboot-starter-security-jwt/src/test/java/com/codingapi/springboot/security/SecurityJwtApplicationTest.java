package com.codingapi.springboot.security;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SecurityJwtApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login() throws Exception {
        JSONObject json = new JSONObject();
        json.put("username","admin");
        json.put("password","123456");
        mockMvc.perform(post("/user/login").content(json.toJSONString().getBytes(StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void noToken() throws Exception {
        mockMvc.perform(get("/api/hello").contentType(MediaType.APPLICATION_JSON)).andExpect(result -> {
            String body = result.getResponse().getContentAsString();
            JSONObject jsonObject = JSONObject.parseObject(body);
            log.info("body:{}",jsonObject);
            assertEquals(jsonObject.getString("errCode"),"token.error","token authentication error");
        });
    }

    @Test
    void haveToken() throws Exception {

        JSONObject json = new JSONObject();
        json.put("username","admin");
        json.put("password","123456");
        MvcResult mvcResult =  mockMvc.perform(post("/user/login").content(json.toJSONString().getBytes(StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON)).andReturn();
        JSONObject loginData = JSONObject.parseObject(mvcResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/hello").header("Authorization",loginData.getJSONObject("data").getString("token")).contentType(MediaType.APPLICATION_JSON)).andExpect(result -> {
            String body = result.getResponse().getContentAsString();
            assertEquals(body,"hello","token authentication error");
        });
    }


    @Test
    void resetToken() throws Exception {

        JSONObject json = new JSONObject();
        json.put("username","admin");
        json.put("password","123456");
        MvcResult mvcResult =  mockMvc.perform(post("/user/login").content(json.toJSONString().getBytes(StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON)).andReturn();
        JSONObject loginData = JSONObject.parseObject(mvcResult.getResponse().getContentAsString());

        Thread.sleep(1000*5);

        mockMvc.perform(get("/api/hello").header("Authorization",loginData.getJSONObject("data").getString("token")).contentType(MediaType.APPLICATION_JSON)).andExpect(result -> {
            String body = result.getResponse().getContentAsString();
            String newToken = result.getResponse().getHeader("Authorization");

            assertTrue(StringUtils.hasText(newToken),"token reset error");
            assertEquals(body,"hello","token authentication error");
        });
    }

    @Test
    void expireToken() throws Exception {

        JSONObject json = new JSONObject();
        json.put("username","admin");
        json.put("password","123456");
        MvcResult mvcResult =  mockMvc.perform(post("/user/login").content(json.toJSONString().getBytes(StandardCharsets.UTF_8)).contentType(MediaType.APPLICATION_JSON)).andReturn();
        JSONObject loginData = JSONObject.parseObject(mvcResult.getResponse().getContentAsString());

        Thread.sleep(1000*10);

        mockMvc.perform(get("/api/hello").header("Authorization",loginData.getJSONObject("data").getString("token")).contentType(MediaType.APPLICATION_JSON)).andExpect(result -> {
            String body = result.getResponse().getContentAsString();
            JSONObject data = JSONObject.parseObject(body);
            assertEquals(data.getString("errCode"),"token.expire","token authentication error");
        });
    }

}
