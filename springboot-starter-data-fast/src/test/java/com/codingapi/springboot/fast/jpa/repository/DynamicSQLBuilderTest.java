package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.framework.dto.request.Filter;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.Relation;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DynamicSQLBuilder 单元测试：覆盖全部过滤关系、or/and 嵌套与排序拼装。
 * （DynamicSQLBuilder 为包级私有，测试类置于同一包下）
 */
class DynamicSQLBuilderTest {

    @Test
    void emptyRequest() {
        PageRequest request = new PageRequest();
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE ", builder.getHQL());
        assertEquals("SELECT COUNT(1) FROM Demo WHERE ", builder.getCountHQL());
        assertEquals(0, builder.getParams().length);
    }

    @Test
    void equalFilter() {
        PageRequest request = new PageRequest();
        request.addFilter("name", "tom");
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE name = ?1", builder.getHQL());
        assertEquals("SELECT COUNT(1) FROM Demo WHERE name = ?1", builder.getCountHQL());
        assertArrayEquals(new Object[]{"tom"}, builder.getParams());
    }

    @Test
    void nullFilters() {
        PageRequest request = new PageRequest();
        request.addFilter("name", Relation.IS_NULL);
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE name IS NULL ", builder.getHQL());
        assertEquals(0, builder.getParams().length);
    }

    @Test
    void notNullFilter() {
        PageRequest request = new PageRequest();
        request.addFilter("name", Relation.IS_NOT_NULL);
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE name IS NOT NULL ", builder.getHQL());
        assertEquals(0, builder.getParams().length);
    }

    @Test
    void notEqualFilter() {
        PageRequest request = new PageRequest();
        request.addFilter("name", Relation.NOT_EQUAL, "tom");
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE name != ?1", builder.getHQL());
        assertArrayEquals(new Object[]{"tom"}, builder.getParams());
    }

    @Test
    void likeFilters() {
        PageRequest request = new PageRequest();
        request.addFilter("name", Relation.LIKE, "to");
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE name LIKE ?1", builder.getHQL());
        assertArrayEquals(new Object[]{"%to%"}, builder.getParams());

        PageRequest left = new PageRequest();
        left.addFilter("name", Relation.LEFT_LIKE, "to");
        DynamicSQLBuilder leftBuilder = new DynamicSQLBuilder(left, Demo.class);
        assertEquals("FROM Demo WHERE name LIKE ?1", leftBuilder.getHQL());
        assertArrayEquals(new Object[]{"%to"}, leftBuilder.getParams());

        PageRequest right = new PageRequest();
        right.addFilter("name", Relation.RIGHT_LIKE, "to");
        DynamicSQLBuilder rightBuilder = new DynamicSQLBuilder(right, Demo.class);
        assertEquals("FROM Demo WHERE name LIKE ?1", rightBuilder.getHQL());
        assertArrayEquals(new Object[]{"to%"}, rightBuilder.getParams());
    }

    @Test
    void inAndNotInFilters() {
        PageRequest request = new PageRequest();
        request.addFilter("id", Relation.IN, 1, 2, 3);
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE id IN (?1)", builder.getHQL());
        assertEquals(1, builder.getParams().length);
        assertEquals(Arrays.asList(1, 2, 3), builder.getParams()[0]);

        PageRequest notIn = new PageRequest();
        notIn.addFilter("id", Relation.NOT_IN, 4);
        DynamicSQLBuilder notInBuilder = new DynamicSQLBuilder(notIn, Demo.class);
        assertEquals("FROM Demo WHERE id NOT IN (?1)", notInBuilder.getHQL());
        assertEquals(Collections.singletonList(4), notInBuilder.getParams()[0]);
    }

    @Test
    void compareFilters() {
        PageRequest request = new PageRequest();
        request.addFilter("sort", Relation.GREATER_THAN, 1);
        assertEquals("FROM Demo WHERE sort > ?1", new DynamicSQLBuilder(request, Demo.class).getHQL());

        PageRequest lt = new PageRequest();
        lt.addFilter("sort", Relation.LESS_THAN, 2);
        assertEquals("FROM Demo WHERE sort < ?1", new DynamicSQLBuilder(lt, Demo.class).getHQL());

        PageRequest gte = new PageRequest();
        gte.addFilter("sort", Relation.GREATER_THAN_EQUAL, 3);
        assertEquals("FROM Demo WHERE sort >= ?1", new DynamicSQLBuilder(gte, Demo.class).getHQL());

        PageRequest lte = new PageRequest();
        lte.addFilter("sort", Relation.LESS_THAN_EQUAL, 4);
        assertEquals("FROM Demo WHERE sort <= ?1", new DynamicSQLBuilder(lte, Demo.class).getHQL());
    }

    /**
     * BETWEEN 使用两个占位符，且后续参数序号连续
     */
    @Test
    void betweenFilterWithFollowingFilter() {
        PageRequest request = new PageRequest();
        request.addFilter("sort", Relation.BETWEEN, 1, 10);
        request.addFilter("name", "tom");
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE sort BETWEEN ?1 AND ?2 AND name = ?3", builder.getHQL());
        assertArrayEquals(new Object[]{1, 10, "tom"}, builder.getParams());
    }

    /**
     * or 过滤组装
     */
    @Test
    void orFilters() {
        PageRequest request = new PageRequest();
        request.orFilters(Filter.as("name", "a"), Filter.as("name", "b"));
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE  ( name = ?1 OR name = ?2 )", builder.getHQL());
        assertArrayEquals(new Object[]{"a", "b"}, builder.getParams());
    }

    /**
     * and 过滤组装
     */
    @Test
    void andFilters() {
        PageRequest request = new PageRequest();
        request.andFilter(Filter.as("name", "a"), Filter.as("sort", Relation.GREATER_THAN, 1));
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE  ( name = ?1 AND sort > ?2 )", builder.getHQL());
        assertArrayEquals(new Object[]{"a", 1}, builder.getParams());
    }

    /**
     * or 嵌套 and 的递归组装
     */
    @Test
    void nestedOrAndFilters() {
        PageRequest request = new PageRequest();
        request.orFilters(Filter.and(Filter.as("name", "a"), Filter.as("name", "b")), Filter.as("name", "c"));
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE  (  ( name = ?1 AND name = ?2 ) OR name = ?3 )", builder.getHQL());
        assertArrayEquals(new Object[]{"a", "b", "c"}, builder.getParams());
    }

    /**
     * 空的 or/and 过滤组不产生任何 SQL
     */
    @Test
    void emptyOrAndFilters() {
        PageRequest request = new PageRequest();
        request.orFilters();
        request.andFilter();
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE ", builder.getHQL());
        assertEquals(0, builder.getParams().length);
    }

    /**
     * 多字段排序拼装（通过构造器传入多字段 Sort）
     */
    @Test
    void sortBuild() {
        Sort sort = Sort.by("id").descending().and(Sort.by("name").ascending());
        PageRequest request = new PageRequest(0, 10, sort);
        request.addFilter("name", "tom");
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE name = ?1 ORDER BY id DESC,name ASC", builder.getHQL());
        // count HQL 不包含排序
        assertEquals("SELECT COUNT(1) FROM Demo WHERE name = ?1", builder.getCountHQL());
    }

    /**
     * 仅排序无过滤
     */
    @Test
    void sortOnly() {
        PageRequest request = new PageRequest();
        request.addSort(Sort.by("id").ascending());
        DynamicSQLBuilder builder = new DynamicSQLBuilder(request, Demo.class);
        assertEquals("FROM Demo WHERE  ORDER BY id ASC", builder.getHQL());
    }
}
