package com.codingapi.springboot.script.bind;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 绑定数据对象
 */
@Getter
@AllArgsConstructor
public class ClassBinder {

    /**
     * 属性名称
     */
    private String name;
    /**
     * 绑定类型
     */
    private Class<?> clazz;



    public static List<ClassBinder> of(String key, Class<?> value) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key, value));
        return list;
    }

    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        return list;
    }

    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2, String key3, Class<?>  value3) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        list.add(new ClassBinder(key3, value3));
        return list;
    }

    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2, String key3, Class<?>  value3, String key4, Class<?>  value4) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        list.add(new ClassBinder(key3, value3));
        list.add(new ClassBinder(key4, value4));
        return list;
    }

    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2, String key3, Class<?>  value3, String key4, Class<?>  value4, String key5, Class<?>  value5) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        list.add(new ClassBinder(key3, value3));
        list.add(new ClassBinder(key4, value4));
        list.add(new ClassBinder(key5, value5));
        return list;
    }


    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2, String key3, Class<?>  value3, String key4, Class<?>  value4, String key5, Class<?>  value5, String key6, Class<?>  value6) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        list.add(new ClassBinder(key3, value3));
        list.add(new ClassBinder(key4, value4));
        list.add(new ClassBinder(key5, value5));
        list.add(new ClassBinder(key6, value6));
        return list;
    }

    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2, String key3, Class<?>  value3, String key4, Class<?>  value4, String key5, Class<?>  value5, String key6, Class<?>  value6, String key7, Class<?>  value7) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        list.add(new ClassBinder(key3, value3));
        list.add(new ClassBinder(key4, value4));
        list.add(new ClassBinder(key5, value5));
        list.add(new ClassBinder(key6, value6));
        list.add(new ClassBinder(key7, value7));
        return list;
    }

    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2, String key3, Class<?>  value3, String key4, Class<?>  value4, String key5, Class<?>  value5, String key6, Class<?>  value6, String key7, Class<?>  value7, String key8, Class<?>  value8) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        list.add(new ClassBinder(key3, value3));
        list.add(new ClassBinder(key4, value4));
        list.add(new ClassBinder(key5, value5));
        list.add(new ClassBinder(key6, value6));
        list.add(new ClassBinder(key7, value7));
        list.add(new ClassBinder(key8, value8));
        return list;
    }

    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2, String key3, Class<?>  value3, String key4, Class<?>  value4, String key5, Class<?>  value5, String key6, Class<?>  value6, String key7, Class<?>  value7, String key8, Class<?>  value8, String key9, Class<?>  value9) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        list.add(new ClassBinder(key3, value3));
        list.add(new ClassBinder(key4, value4));
        list.add(new ClassBinder(key5, value5));
        list.add(new ClassBinder(key6, value6));
        list.add(new ClassBinder(key7, value7));
        list.add(new ClassBinder(key8, value8));
        list.add(new ClassBinder(key9, value9));
        return list;
    }

    public static List<ClassBinder> of(String key1, Class<?>  value1, String key2, Class<?>  value2, String key3, Class<?>  value3, String key4, Class<?>  value4, String key5, Class<?>  value5, String key6, Class<?>  value6, String key7, Class<?>  value7, String key8, Class<?>  value8, String key9, Class<?>  value9, String key10, Class<?>  value10) {
        List<ClassBinder> list = new ArrayList<>();
        list.add(new ClassBinder(key1, value1));
        list.add(new ClassBinder(key2, value2));
        list.add(new ClassBinder(key3, value3));
        list.add(new ClassBinder(key4, value4));
        list.add(new ClassBinder(key5, value5));
        list.add(new ClassBinder(key6, value6));
        list.add(new ClassBinder(key7, value7));
        list.add(new ClassBinder(key8, value8));
        list.add(new ClassBinder(key9, value9));
        list.add(new ClassBinder(key10, value10));
        return list;
    }

}
