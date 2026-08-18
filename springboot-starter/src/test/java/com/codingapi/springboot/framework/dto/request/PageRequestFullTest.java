package com.codingapi.springboot.framework.dto.request;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageRequest 单元测试(补充现有 PageRequestTest)
 */
class PageRequestFullTest {

    @Test
    void staticFactories() {
        PageRequest pageRequest = PageRequest.of(2, 10);
        assertEquals(2, pageRequest.getCurrent());
        assertEquals(2, pageRequest.getPageNumber());
        assertEquals(10, pageRequest.getPageSize());
        assertEquals(20L, pageRequest.getOffset());
        assertTrue(pageRequest.hasPrevious());
        assertEquals(Sort.unsorted(), pageRequest.getSort());

        PageRequest firstPage = PageRequest.of(0, 10);
        assertFalse(firstPage.hasPrevious());
        assertEquals(0L, firstPage.getOffset());

        Sort sort = Sort.by("id").descending();
        PageRequest sorted = PageRequest.of(1, 10, sort);
        assertNotNull(sorted.getSort().getOrderFor("id"));
    }

    @Test
    void addSort() {
        PageRequest pageRequest = new PageRequest();
        assertEquals(Sort.unsorted(), pageRequest.getSort());

        pageRequest.addSort(Sort.by("name").ascending());
        assertNotNull(pageRequest.getSort().getOrderFor("name"));

        // 已有排序时再次添加
        pageRequest.addSort(Sort.by("id").descending());
        assertNotNull(pageRequest.getSort());
    }

    @Test
    void filterDelegation() {
        PageRequest pageRequest = PageRequest.of(0, 20);
        assertFalse(pageRequest.hasFilter());

        pageRequest.addFilter("name", "zhang");
        pageRequest.addFilter("age", Relation.GREATER_THAN, 18);
        pageRequest.andFilter(Filter.as("a", "1"));
        pageRequest.orFilters(Filter.as("b", "2"));

        assertTrue(pageRequest.hasFilter());
        assertEquals("zhang", pageRequest.getStringFilter("name"));
        assertEquals("default", pageRequest.getStringFilter("missing", "default"));
        assertEquals(0, pageRequest.getIntFilter("missing"));
        assertEquals(3, pageRequest.getIntFilter("missing", 3));
        assertNotNull(pageRequest.getRequestFilter().getFilter(Filter.FILTER_AND_KEY));
        assertNotNull(pageRequest.getRequestFilter().getFilter(Filter.FILTER_OR_KEY));

        pageRequest.removeFilter("name");
        assertNull(pageRequest.getStringFilter("name"));
    }

    @Test
    void setPageSizeAndCurrent() {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageSize(50);
        pageRequest.setCurrent(3);
        assertEquals(50, pageRequest.getPageSize());
        assertEquals(3, pageRequest.getCurrent());
        assertEquals(150L, pageRequest.getOffset());
    }

    @Test
    void idRequest() {
        IdRequest idRequest = new IdRequest();
        idRequest.setId("12");
        assertEquals("12", idRequest.getStringId());
        assertEquals(12, idRequest.getIntId());
        assertEquals(Long.valueOf(12L), idRequest.getLongId());
        assertEquals(12f, idRequest.getFloatId());
        assertEquals(12d, idRequest.getDoubleId());
    }

    @Test
    void sortRequest() {
        SortRequest sortRequest = new SortRequest();
        sortRequest.setIds(java.util.Arrays.asList("1", "2"));
        assertEquals(2, sortRequest.getIds().size());
    }
}
