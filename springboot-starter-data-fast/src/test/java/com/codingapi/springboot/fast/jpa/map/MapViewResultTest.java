package com.codingapi.springboot.fast.jpa.map;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * MapViewResult 拥有 253 个构造器重载（1~253 个值参数），
 * 通过反射逐个 arity 调用，确保每个构造器与 build 逻辑都被覆盖。
 *
 * 注意：build(key, values) 按 QueryColumns 的列数量遍历取值，
 * 因此每个 arity 需要注册一个列数量与之匹配的 QueryColumns。
 */
class MapViewResultTest {

    private static final int MAX_ARITY = 253;

    private String[] buildColumns(int count) {
        String[] columns = new String[count];
        for (int i = 0; i < count; i++) {
            columns[i] = "c" + i;
        }
        return columns;
    }

    private Object[] buildValues(int count) {
        Object[] values = new Object[count];
        for (int i = 0; i < count; i++) {
            values[i] = "v" + i;
        }
        return values;
    }

    /**
     * 参数化遍历全部 253 个构造器重载
     */
    @Test
    void allConstructorOverloads() throws Exception {
        for (int arity = 1; arity <= MAX_ARITY; arity++) {
            QueryColumns queryColumns = QueryColumnsContext.build(buildColumns(arity));
            String key = queryColumns.getKey();
            try {
                Class<?>[] parameterTypes = new Class<?>[arity + 1];
                parameterTypes[0] = String.class;
                Arrays.fill(parameterTypes, 1, arity + 1, Object.class);
                Constructor<MapViewResult> constructor = MapViewResult.class.getConstructor(parameterTypes);

                Object[] args = new Object[arity + 1];
                args[0] = key;
                Object[] values = buildValues(arity);
                System.arraycopy(values, 0, args, 1, arity);

                MapViewResult result = constructor.newInstance(args);

                assertEquals(arity, result.size(), "arity=" + arity);
                assertEquals("v0", result.get("c0"), "arity=" + arity);
                assertEquals("v" + (arity - 1), result.get("c" + (arity - 1)), "arity=" + arity);
                assertNull(result.get("not-exist"), "arity=" + arity);
            } finally {
                QueryColumnsContext.getInstance().clearCache(key);
            }
        }
    }

    /**
     * 直接调用部分常用构造器，验证列名映射与值顺序
     */
    @Test
    void directConstructors() {
        QueryColumns two = QueryColumnsContext.build("u.id as iii", "u.name");
        MapViewResult result2 = new MapViewResult(two.getKey(), 1, "tom");
        assertEquals(2, result2.size());
        assertEquals(1, result2.get("iii"));
        assertEquals("tom", result2.get("name"));
        QueryColumnsContext.getInstance().clearCache(two.getKey());

        QueryColumns three = QueryColumnsContext.build("a", "b", "c");
        MapViewResult result3 = new MapViewResult(three.getKey(), 1, 2L, 3.5);
        assertEquals(3, result3.size());
        assertEquals(1, result3.get("a"));
        assertEquals(2L, result3.get("b"));
        assertEquals(3.5, result3.get("c"));
        QueryColumnsContext.getInstance().clearCache(three.getKey());

        QueryColumns five = QueryColumnsContext.build("a", "b", "c", "d", "e");
        MapViewResult result5 = new MapViewResult(five.getKey(), "1", "2", "3", "4", "5");
        assertEquals(5, result5.size());
        assertEquals("5", result5.get("e"));
        QueryColumnsContext.getInstance().clearCache(five.getKey());
    }

    /**
     * MapViewResult 本身是一个 Map，验证 Map 的基础行为
     */
    @Test
    void mapBehavior() {
        QueryColumns columns = QueryColumnsContext.build("name", "age");
        MapViewResult result = new MapViewResult(columns.getKey(), "tom", 18);
        assertEquals(true, result.containsKey("name"));
        assertEquals(true, result.containsValue(18));
        result.put("extra", "value");
        assertEquals(3, result.size());
        result.remove("extra");
        assertEquals(2, result.size());
        QueryColumnsContext.getInstance().clearCache(columns.getKey());
    }
}
