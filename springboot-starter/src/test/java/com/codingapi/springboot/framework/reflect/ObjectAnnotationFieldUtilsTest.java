package com.codingapi.springboot.framework.reflect;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectAnnotationFieldUtilsTest {

    @Test
    void findFieldAnnotationValue() {
        String attribute2 = """
                {
                   "key":"123",
                   "label":"123",
                   "value":[
                      {
                        "data":"123"
                      },
                      {
                        "data":"123"
                      }
                   ]
                }
                """;


        FieldAttribute attributeObj1 =  new FieldAttribute();
        FieldAttribute attributeObj2 =  JSONObject.parseObject(attribute2,FieldAttribute.class);


        List<String> keys1 = ObjectAnnotationFieldUtils.findFieldAnnotationValue(attributeObj1,MyScript.class, String.class).getValues();
        assertEquals(0,keys1.size());

        List<String> keys2 = ObjectAnnotationFieldUtils.findFieldAnnotationValue(attributeObj2,MyScript.class, String.class).getValues();
        assertEquals(1,keys2.size());
    }
}