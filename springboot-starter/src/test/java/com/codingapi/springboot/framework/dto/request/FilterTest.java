package com.codingapi.springboot.framework.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Filter 单元测试
 */
class FilterTest {

    @Test
    void constructors() {
        Filter equalFilter = new Filter("name", "zhang");
        assertEquals("name", equalFilter.getKey());
        assertEquals(Relation.EQUAL, equalFilter.getRelation());
        assertEquals(1, equalFilter.getValue().length);
        assertEquals("zhang", equalFilter.getValue()[0]);

        Filter relationFilter = new Filter("age", Relation.GREATER_THAN, 18);
        assertEquals(Relation.GREATER_THAN, relationFilter.getRelation());

        Filter groupFilter = new Filter(Filter.FILTER_AND_KEY,
                new Filter("a", "1"), new Filter("b", "2"));
        assertNull(groupFilter.getRelation());
        assertEquals(2, groupFilter.getValue().length);
        assertTrue(groupFilter.isAndFilters());
        assertFalse(groupFilter.isOrFilters());
    }

    @Test
    void staticFactories() {
        Filter as1 = Filter.as("name", "zhang");
        assertTrue(as1.isEqual());

        Filter as2 = Filter.as("age", Relation.LESS_THAN, 30);
        assertTrue(as2.isLessThan());

        Filter and = Filter.and(Filter.as("a", "1"), Filter.as("b", "2"));
        assertEquals(Filter.FILTER_AND_KEY, and.getKey());
        assertTrue(and.isAndFilters());

        Filter or = Filter.or(Filter.as("c", "3"), Filter.as("d", "4"));
        assertEquals(Filter.FILTER_OR_KEY, or.getKey());
        assertTrue(or.isOrFilters());
        assertFalse(or.isAndFilters());
    }

    @Test
    void settersAndGetters() {
        Filter filter = new Filter("name", "zhang");
        filter.setKey("newName");
        filter.setRelation(Relation.NOT_EQUAL);
        filter.setValue(new Object[]{"li"});
        assertEquals("newName", filter.getKey());
        assertEquals(Relation.NOT_EQUAL, filter.getRelation());
        assertEquals("li", filter.getValue()[0]);
    }

    @Test
    void relationChecks() {
        assertTrue(new Filter("k", Relation.EQUAL, "v").isEqual());
        assertTrue(new Filter("k", Relation.NOT_EQUAL, "v").isNotEqual());
        assertTrue(new Filter("k", Relation.LIKE, "v").isLike());
        assertTrue(new Filter("k", Relation.LEFT_LIKE, "v").isLeftLike());
        assertTrue(new Filter("k", Relation.RIGHT_LIKE, "v").isRightLike());
        assertTrue(new Filter("k", Relation.BETWEEN, 1, 2).isBetween());
        assertTrue(new Filter("k", Relation.IN, 1, 2).isIn());
        assertTrue(new Filter("k", Relation.NOT_IN, 1, 2).isNotIn());
        assertTrue(new Filter("k", Relation.IS_NULL).isNull());
        assertTrue(new Filter("k", Relation.IS_NOT_NULL).isNotNull());
        assertTrue(new Filter("k", Relation.GREATER_THAN, 1).isGreaterThan());
        assertTrue(new Filter("k", Relation.LESS_THAN, 1).isLessThan());
        assertTrue(new Filter("k", Relation.GREATER_THAN_EQUAL, 1).isGreaterThanEqual());
        assertTrue(new Filter("k", Relation.LESS_THAN_EQUAL, 1).isLessThanEqual());

        Filter filter = new Filter("k", Relation.EQUAL, "v");
        assertFalse(filter.isNull());
        assertFalse(filter.isNotNull());
        assertFalse(filter.isIn());
        assertFalse(filter.isNotIn());
        assertFalse(filter.isLike());
        assertFalse(filter.isLeftLike());
        assertFalse(filter.isRightLike());
        assertFalse(filter.isBetween());
        assertFalse(filter.isGreaterThan());
        assertFalse(filter.isLessThan());
        assertFalse(filter.isGreaterThanEqual());
        assertFalse(filter.isLessThanEqual());
        assertFalse(filter.isNotEqual());
    }

    @Test
    void getFilterValueWithStringConversion() {
        assertEquals(1, new Filter("k", "1").getFilterValue(Integer.class));
        assertEquals(2L, new Filter("k", "2").getFilterValue(Long.class));
        assertEquals(3.5d, new Filter("k", "3.5").getFilterValue(Double.class));
        assertEquals(4.5f, new Filter("k", "4.5").getFilterValue(Float.class));
        assertEquals("text", new Filter("k", "text").getFilterValue(String.class));
        // 非字符串值原样返回
        assertEquals(9, new Filter("k", 9).getFilterValue(Integer.class));
    }

    @Test
    void relationEnum() {
        assertEquals(14, Relation.values().length);
        assertEquals(Relation.EQUAL, Relation.valueOf("EQUAL"));
        assertEquals(Relation.LESS_THAN_EQUAL, Relation.valueOf("LESS_THAN_EQUAL"));
    }
}
