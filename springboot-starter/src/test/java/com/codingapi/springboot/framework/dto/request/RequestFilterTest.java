package com.codingapi.springboot.framework.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RequestFilter 单元测试
 */
class RequestFilterTest {

    @Test
    void addFilters() {
        RequestFilter requestFilter = new RequestFilter();
        assertFalse(requestFilter.hasFilter());

        requestFilter.addFilter("name", "zhang");
        requestFilter.addFilter("age", Relation.GREATER_THAN, "18");

        assertTrue(requestFilter.hasFilter());
        assertEquals(2, requestFilter.getFilters().size());
        assertEquals("zhang", requestFilter.getStringFilter("name"));
        assertTrue(requestFilter.getFilter("age").isGreaterThan());
    }

    @Test
    void groupFilters() {
        RequestFilter requestFilter = new RequestFilter();
        requestFilter.andFilters(Filter.as("a", "1"), Filter.as("b", "2"));
        requestFilter.orFilters(Filter.as("c", "3"));

        Filter andFilter = requestFilter.getFilter(Filter.FILTER_AND_KEY);
        assertNotNull(andFilter);
        assertTrue(andFilter.isAndFilters());
        assertEquals(2, andFilter.getValue().length);

        Filter orFilter = requestFilter.getFilter(Filter.FILTER_OR_KEY);
        assertNotNull(orFilter);
        assertTrue(orFilter.isOrFilters());
    }

    @Test
    void pushFilterReplacesSameKey() {
        RequestFilter requestFilter = new RequestFilter();
        requestFilter.addFilter("name", "zhang");
        requestFilter.addFilter("name", Relation.LIKE, "li");

        assertEquals(1, requestFilter.getFilters().size());
        assertTrue(requestFilter.getFilter("name").isLike());
        assertEquals("li", requestFilter.getFilter("name").getValue()[0]);
    }

    @Test
    void removeFilter() {
        RequestFilter requestFilter = new RequestFilter();
        requestFilter.addFilter("name", "zhang");
        requestFilter.removeFilter("name");

        assertFalse(requestFilter.hasFilter());
        assertNull(requestFilter.getFilter("name"));
        assertTrue(requestFilter.getFilters().isEmpty());
    }

    @Test
    void stringFilterWithDefault() {
        RequestFilter requestFilter = new RequestFilter();
        assertNull(requestFilter.getStringFilter("missing"));
        assertEquals("default", requestFilter.getStringFilter("missing", "default"));

        requestFilter.addFilter("blank", "");
        assertEquals("default", requestFilter.getStringFilter("blank", "default"));

        requestFilter.addFilter("name", "zhang");
        assertEquals("zhang", requestFilter.getStringFilter("name", "default"));
    }

    @Test
    void intFilter() {
        RequestFilter requestFilter = new RequestFilter();
        assertEquals(0, requestFilter.getIntFilter("missing"));
        assertEquals(5, requestFilter.getIntFilter("missing", 5));

        requestFilter.addFilter("age", "18");
        assertEquals(18, requestFilter.getIntFilter("age"));
        assertEquals(18, requestFilter.getIntFilter("age", 5));

        requestFilter.addFilter("zero", "");
        assertEquals(0, requestFilter.getIntFilter("zero"));
        assertEquals(5, requestFilter.getIntFilter("zero", 5));
    }

    @Test
    void isAllEqualFilter() {
        RequestFilter requestFilter = new RequestFilter();
        // 无条件时返回 false
        assertFalse(requestFilter.isAllEqualFilter());

        // 全部为等值条件
        requestFilter.addFilter("name", "zhang");
        requestFilter.addFilter("age", Relation.EQUAL, 18);
        assertTrue(requestFilter.isAllEqualFilter());

        // 包含 LIKE 条件
        requestFilter.addFilter("title", Relation.LIKE, "%a%");
        assertFalse(requestFilter.isAllEqualFilter());
        requestFilter.removeFilter("title");
        assertTrue(requestFilter.isAllEqualFilter());

        // 包含范围条件
        requestFilter.addFilter("age", Relation.GREATER_THAN, 20);
        assertFalse(requestFilter.isAllEqualFilter());
        requestFilter.removeFilter("age");
        assertTrue(requestFilter.isAllEqualFilter());

        // 包含 OR 组合条件
        requestFilter.orFilters(Filter.as("name", "a"), Filter.as("name", "b"));
        assertFalse(requestFilter.isAllEqualFilter());
        requestFilter.removeFilter(Filter.FILTER_OR_KEY);
        assertTrue(requestFilter.isAllEqualFilter());

        // 包含 AND 组合条件
        requestFilter.andFilters(Filter.as("name", "a"), Filter.as("age", 1));
        assertFalse(requestFilter.isAllEqualFilter());
    }
}
