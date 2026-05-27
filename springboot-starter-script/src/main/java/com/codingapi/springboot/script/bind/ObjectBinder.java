package com.codingapi.springboot.script.bind;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时绑定对象
 */
@Getter
@AllArgsConstructor
public class ObjectBinder {

    /**
     * 绑定的访问对象
     */
    private String name;
    /**
     * 绑定的对象实例
     */
    private Object object;


    public static List<ObjectBinder> of(String key, Object value) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key, value));
        return list;
    }

    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        return list;
    }

    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        list.add(new ObjectBinder(key3, value3));
        return list;
    }

    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        list.add(new ObjectBinder(key3, value3));
        list.add(new ObjectBinder(key4, value4));
        return list;
    }

    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        list.add(new ObjectBinder(key3, value3));
        list.add(new ObjectBinder(key4, value4));
        list.add(new ObjectBinder(key5, value5));
        return list;
    }


    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        list.add(new ObjectBinder(key3, value3));
        list.add(new ObjectBinder(key4, value4));
        list.add(new ObjectBinder(key5, value5));
        list.add(new ObjectBinder(key6, value6));
        return list;
    }

    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        list.add(new ObjectBinder(key3, value3));
        list.add(new ObjectBinder(key4, value4));
        list.add(new ObjectBinder(key5, value5));
        list.add(new ObjectBinder(key6, value6));
        list.add(new ObjectBinder(key7, value7));
        return list;
    }

    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        list.add(new ObjectBinder(key3, value3));
        list.add(new ObjectBinder(key4, value4));
        list.add(new ObjectBinder(key5, value5));
        list.add(new ObjectBinder(key6, value6));
        list.add(new ObjectBinder(key7, value7));
        list.add(new ObjectBinder(key8, value8));
        return list;
    }

    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8, String key9, Object value9) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        list.add(new ObjectBinder(key3, value3));
        list.add(new ObjectBinder(key4, value4));
        list.add(new ObjectBinder(key5, value5));
        list.add(new ObjectBinder(key6, value6));
        list.add(new ObjectBinder(key7, value7));
        list.add(new ObjectBinder(key8, value8));
        list.add(new ObjectBinder(key9, value9));
        return list;
    }

    public static List<ObjectBinder> of(String key1, Object value1, String key2, Object value2, String key3, Object value3, String key4, Object value4, String key5, Object value5, String key6, Object value6, String key7, Object value7, String key8, Object value8, String key9, Object value9, String key10, Object value10) {
        List<ObjectBinder> list = new ArrayList<>();
        list.add(new ObjectBinder(key1, value1));
        list.add(new ObjectBinder(key2, value2));
        list.add(new ObjectBinder(key3, value3));
        list.add(new ObjectBinder(key4, value4));
        list.add(new ObjectBinder(key5, value5));
        list.add(new ObjectBinder(key6, value6));
        list.add(new ObjectBinder(key7, value7));
        list.add(new ObjectBinder(key8, value8));
        list.add(new ObjectBinder(key9, value9));
        list.add(new ObjectBinder(key10, value10));
        return list;
    }

}
