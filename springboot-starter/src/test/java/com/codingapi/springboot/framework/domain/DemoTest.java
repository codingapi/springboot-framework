package com.codingapi.springboot.framework.domain;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.convert.BeanConvertor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DemoTest {

    @Test
    void eventTest() {
        Demo demo = new Demo("xiaoming");
        demo.changeName("xiaomi");
    }

    @Test
    void jsonTest() {
        Demo demo = new Demo("xiaoming");
        JSONObject json = JSONObject.parseObject(demo.toJson());
        assertEquals(json.getString("name"), demo.getName(), "json serializable error");
    }

    @Test
    void mapTest() {
        Demo demo = new Demo("xiaoming");
        Map<String, Object> map = demo.toMap();
        assertEquals(map.get("name"), demo.getName(), "map serializable error");
    }

    @Test
    void convertTest() {
        Demo demo = new Demo("xiaoming");
        DemoEntity entity = BeanConvertor.convert(demo, DemoEntity.class);
        assertEquals(entity.getName(), demo.getName(), "convert serializable error");
    }

}
