package com.codingapi.springboot.script.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时绑定对象
 */
@Getter
@AllArgsConstructor
public class GroovyBindObject {

    /**
     * 绑定的访问对象
     */
    private String name;
    /**
     * 绑定的对象实例
     */
    private Object object;


    public static List<GroovyBindObject> of(String key, Object value) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key, value));
        return list;
    }

    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        return list;
    }

    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        list.add(new GroovyBindObject(key3, value3));
        return list;
    }

    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        list.add(new GroovyBindObject(key3, value3));
        list.add(new GroovyBindObject(key4, value4));
        return list;
    }

    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        list.add(new GroovyBindObject(key3, value3));
        list.add(new GroovyBindObject(key4, value4));
        list.add(new GroovyBindObject(key5, value5));
        return list;
    }


    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        list.add(new GroovyBindObject(key3, value3));
        list.add(new GroovyBindObject(key4, value4));
        list.add(new GroovyBindObject(key5, value5));
        list.add(new GroovyBindObject(key6, value6));
        return list;
    }

    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        list.add(new GroovyBindObject(key3, value3));
        list.add(new GroovyBindObject(key4, value4));
        list.add(new GroovyBindObject(key5, value5));
        list.add(new GroovyBindObject(key6, value6));
        list.add(new GroovyBindObject(key7, value7));
        return list;
    }

    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        list.add(new GroovyBindObject(key3, value3));
        list.add(new GroovyBindObject(key4, value4));
        list.add(new GroovyBindObject(key5, value5));
        list.add(new GroovyBindObject(key6, value6));
        list.add(new GroovyBindObject(key7, value7));
        list.add(new GroovyBindObject(key8, value8));
        return list;
    }

    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8, String key9, Object value9) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        list.add(new GroovyBindObject(key3, value3));
        list.add(new GroovyBindObject(key4, value4));
        list.add(new GroovyBindObject(key5, value5));
        list.add(new GroovyBindObject(key6, value6));
        list.add(new GroovyBindObject(key7, value7));
        list.add(new GroovyBindObject(key8, value8));
        list.add(new GroovyBindObject(key9, value9));
        return list;
    }

    public static List<GroovyBindObject> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8, String key9, Object value9, String key10, Object value10) {
        List<GroovyBindObject> list = new ArrayList<>();
        list.add(new GroovyBindObject(key1, value1));
        list.add(new GroovyBindObject(key2, value2));
        list.add(new GroovyBindObject(key3, value3));
        list.add(new GroovyBindObject(key4, value4));
        list.add(new GroovyBindObject(key5, value5));
        list.add(new GroovyBindObject(key6, value6));
        list.add(new GroovyBindObject(key7, value7));
        list.add(new GroovyBindObject(key8, value8));
        list.add(new GroovyBindObject(key9, value9));
        list.add(new GroovyBindObject(key10, value10));
        return list;
    }

}
