package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.Relation;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ExampleBuilder 单元测试（包级私有类，测试置于同一包下）
 */
class ExampleBuilderTest {

    /**
     * 无过滤条件时返回 null
     */
    @Test
    void noFilterReturnsNull() {
        PageRequest request = new PageRequest();
        ExampleBuilder builder = new ExampleBuilder(request, Demo.class);
        assertNull(builder.getExample());
    }

    /**
     * 过滤条件命中实体属性时构建 Example，
     * 同时覆盖字符串值向 Integer 类型属性的转换分支
     */
    @Test
    void buildExampleWithFilters() {
        PageRequest request = new PageRequest();
        request.addFilter("name", "tom");
        request.addFilter("sort", "18");
        ExampleBuilder builder = new ExampleBuilder(request, Demo.class);
        Example<Demo> example = builder.getExample();
        assertNotNull(example);
        Demo probe = example.getProbe();
        assertEquals("tom", probe.getName());
        assertEquals(Integer.valueOf(18), probe.getSort());
    }

    /**
     * 过滤条件未命中任何属性时，Example 的 probe 为空白对象
     */
    @Test
    void filterNotMatchAnyProperty() {
        PageRequest request = new PageRequest();
        request.addFilter("notExistField", "value");
        ExampleBuilder builder = new ExampleBuilder(request, Demo.class);
        Example<Demo> example = builder.getExample();
        assertNotNull(example);
        assertNull(example.getProbe().getName());
    }

    /**
     * 命中只读属性（如 class）时写入失败被内部吞掉，不影响整体构建
     */
    @Test
    void readOnlyPropertyFailureIsIgnored() {
        PageRequest request = new PageRequest();
        request.addFilter("class", "ignored");
        request.addFilter("name", "tom");
        ExampleBuilder builder = new ExampleBuilder(request, Demo.class);
        Example<Demo> example = builder.getExample();
        assertNotNull(example);
        assertEquals("tom", example.getProbe().getName());
    }

    /**
     * 实体类无默认构造器时抛出 RuntimeException
     */
    @Test
    void entityWithoutDefaultConstructor() {
        PageRequest request = new PageRequest();
        request.addFilter("value", "1");
        ExampleBuilder builder = new ExampleBuilder(request, Integer.class);
        assertThrows(RuntimeException.class, builder::getExample);
    }

    /**
     * 非 EQUAL 关系同样参与 Example 组装（仅取值填充 probe）
     */
    @Test
    void relationFilterAlsoFillsProbe() {
        PageRequest request = new PageRequest();
        request.addFilter("name", Relation.LIKE, "to");
        ExampleBuilder builder = new ExampleBuilder(request, Demo.class);
        Example<Demo> example = builder.getExample();
        assertNotNull(example);
        assertEquals("to", example.getProbe().getName());
    }
}
