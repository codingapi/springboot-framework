package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SearchRequest 及其内部类 ClassContent、ParamOperation 的单元测试
 */
class SearchRequestParseTest {

    private MockHttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        httpRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    private static String encode(String json) {
        return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    @Setter
    @Getter
    static class Address {
        private String city;
    }

    @Setter
    @Getter
    static class BaseQuery {
        private int state;
    }

    @Setter
    @Getter
    static class UserQuery extends BaseQuery {
        private long id;
        private String name;
        private int age;
        private boolean deleted;
        private Address address;
    }

    @Setter
    @Getter
    static class RawQuery {
        private String params;
        private String sort;
        private String filter;
    }

    @Test
    void toPageRequestWithSortFilterAndParams() {
        httpRequest.setParameter("current", "2");
        httpRequest.setParameter("pageSize", "15");
        httpRequest.setParameter("name", "zhang");
        httpRequest.setParameter("age", "18");
        httpRequest.setParameter("empty", "");
        httpRequest.setParameter("sort", encode("{\"name\":\"ascend\"}"));
        httpRequest.setParameter("filter", encode("{\"id\":[\"1\",\"2\"]}"));
        httpRequest.setParameter("params", encode("[{\"key\":\"age\",\"type\":\"GREATER_THAN\"}]"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCurrent(2);
        searchRequest.setPageSize(15);

        assertEquals("zhang", searchRequest.getParameter("name"));
        assertArrayEquals(new String[]{"zhang"}, searchRequest.getParameterValues("name"));
        List<String> parameterNames = searchRequest.getParameterNames();
        assertTrue(parameterNames.contains("name"));
        assertTrue(parameterNames.contains("age"));

        PageRequest pageRequest = searchRequest.toPageRequest(UserQuery.class);

        // SearchRequest 不是 PageRequest, 默认分页偏移规则会减一
        assertEquals(1, searchRequest.getCurrent());
        assertEquals(1, pageRequest.getCurrent());
        assertEquals(15, pageRequest.getPageSize());

        Filter nameFilter = pageRequest.getRequestFilter().getFilter("name");
        assertNotNull(nameFilter);
        assertTrue(nameFilter.isEqual());
        assertEquals("zhang", nameFilter.getValue()[0]);

        Filter ageFilter = pageRequest.getRequestFilter().getFilter("age");
        assertNotNull(ageFilter);
        assertTrue(ageFilter.isGreaterThan());
        assertEquals(18, ageFilter.getValue()[0]);

        Filter idFilter = pageRequest.getRequestFilter().getFilter("id");
        assertNotNull(idFilter);
        assertTrue(idFilter.isIn());
        assertEquals(2, idFilter.getValue().length);
        assertEquals(1L, idFilter.getValue()[0]);
        assertEquals(2L, idFilter.getValue()[1]);

        Sort sort = pageRequest.getSort();
        assertNotNull(sort.getOrderFor("name"));
        assertTrue(sort.getOrderFor("name").isAscending());

        // filter/sort/params 已从查询参数中移除, 不会再作为过滤条件
        assertNull(pageRequest.getRequestFilter().getFilter("sort"));
        assertNull(pageRequest.getRequestFilter().getFilter("filter"));
        assertNull(pageRequest.getRequestFilter().getFilter("params"));
    }

    @Test
    void toPageRequestWithDescendSort() {
        httpRequest.setParameter("sort", encode("{\"id\":\"descend\"}"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCurrent(1);
        searchRequest.setPageSize(10);

        PageRequest pageRequest = searchRequest.toPageRequest(UserQuery.class);
        assertNotNull(pageRequest.getSort().getOrderFor("id"));
        assertTrue(pageRequest.getSort().getOrderFor("id").isDescending());
    }

    @Test
    void toPageRequestWithEmptyFilterArrayAndBooleanField() {
        httpRequest.setParameter("filter", encode("{\"name\":[],\"deleted\":[\"true\"]}"));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCurrent(1);
        searchRequest.setPageSize(10);

        PageRequest pageRequest = searchRequest.toPageRequest(UserQuery.class);
        // 空数组的 filter 被忽略
        assertNull(pageRequest.getRequestFilter().getFilter("name"));
        Filter deletedFilter = pageRequest.getRequestFilter().getFilter("deleted");
        assertNotNull(deletedFilter);
        assertTrue(deletedFilter.isIn());
        assertEquals(true, deletedFilter.getValue()[0]);
    }

    @Test
    void toPageRequestWithoutOperations() {
        httpRequest.setParameter("name", "li");
        httpRequest.setParameter("state", "1");
        httpRequest.setParameter("address.city", "hangzhou");

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCurrent(1);
        searchRequest.setPageSize(10);

        PageRequest pageRequest = searchRequest.toPageRequest(UserQuery.class);

        assertTrue(pageRequest.getRequestFilter().getFilter("name").isEqual());
        assertEquals("li", pageRequest.getRequestFilter().getFilter("name").getValue()[0]);
        // state 字段定义在父类 BaseQuery 中
        assertEquals(1, pageRequest.getRequestFilter().getFilter("state").getValue()[0]);
        // 嵌套对象字段
        assertEquals("hangzhou", pageRequest.getRequestFilter().getFilter("address.city").getValue()[0]);
    }

    @Test
    void toPageRequestWithInvalidJsonParams() {
        // 合法 Base64 但非法 JSON 时不会加入 removeKeys, 原始(未解码)值将作为普通过滤条件处理
        String raw = encode("not-a-json");
        httpRequest.setParameter("params", raw);
        httpRequest.setParameter("sort", raw);
        httpRequest.setParameter("filter", raw);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCurrent(1);
        searchRequest.setPageSize(10);

        PageRequest pageRequest = searchRequest.toPageRequest(RawQuery.class);
        assertEquals(raw, pageRequest.getRequestFilter().getFilter("params").getValue()[0]);
        assertEquals(raw, pageRequest.getRequestFilter().getFilter("sort").getValue()[0]);
        assertEquals(raw, pageRequest.getRequestFilter().getFilter("filter").getValue()[0]);
    }

    @Test
    void toPageRequestWithInvalidBase64Params() {
        // 非法 Base64 不应抛出 IllegalArgumentException, 参数按原始值作为普通过滤条件处理
        httpRequest.setParameter("params", "%%%not-base64%%%");
        httpRequest.setParameter("sort", "%%%not-base64%%%");
        httpRequest.setParameter("filter", "%%%not-base64%%%");

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCurrent(1);
        searchRequest.setPageSize(10);

        PageRequest pageRequest = assertDoesNotThrow(() -> searchRequest.toPageRequest(RawQuery.class));
        assertEquals("%%%not-base64%%%", pageRequest.getRequestFilter().getFilter("params").getValue()[0]);
        assertEquals("%%%not-base64%%%", pageRequest.getRequestFilter().getFilter("sort").getValue()[0]);
        assertEquals("%%%not-base64%%%", pageRequest.getRequestFilter().getFilter("filter").getValue()[0]);
    }

    @Test
    void toPageRequestWithUnknownFieldThrowsException() {
        httpRequest.setParameter("unknownField", "value");

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCurrent(1);
        searchRequest.setPageSize(10);

        assertThrows(IllegalArgumentException.class, () -> searchRequest.toPageRequest(UserQuery.class));
    }

    @Test
    void removeFilterAddsRemoveKey() {
        httpRequest.setParameter("name", "wang");

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCurrent(1);
        searchRequest.setPageSize(10);
        searchRequest.removeFilter("name");

        PageRequest pageRequest = searchRequest.toPageRequest(UserQuery.class);
        assertNull(pageRequest.getRequestFilter().getFilter("name"));
    }

    @Test
    void delegateMethods() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.addSort(Sort.by("id").descending());
        searchRequest.addFilter("name", "zhang");
        searchRequest.addFilter("age", Relation.GREATER_THAN, 18);
        searchRequest.andFilter(Filter.as("a", "1"), Filter.as("b", "2"));
        searchRequest.orFilters(Filter.as("c", "3"), Filter.as("d", "4"));

        PageRequest pageRequest = searchRequest.toPageRequest(UserQuery.class);
        assertNotNull(pageRequest.getSort().getOrderFor("id"));
        assertEquals("zhang", pageRequest.getStringFilter("name"));
        assertTrue(pageRequest.getRequestFilter().getFilter("age").isGreaterThan());
        assertNotNull(pageRequest.getRequestFilter().getFilter(Filter.FILTER_AND_KEY));
        assertNotNull(pageRequest.getRequestFilter().getFilter(Filter.FILTER_OR_KEY));
    }

    @Test
    void classContentDirectly() {
        PageRequest pageRequest = new PageRequest();
        SearchRequest.ClassContent content = new SearchRequest.ClassContent(UserQuery.class, pageRequest);

        content.addFilter("name", "zhang");
        content.addFilter("age", Relation.LESS_THAN, "30");
        content.addFilter("id", Arrays.asList("1", "2"));
        content.addFilter("address.city", Relation.LIKE, "hz");

        RequestFilter requestFilter = pageRequest.getRequestFilter();
        assertTrue(requestFilter.getFilter("name").isEqual());
        assertTrue(requestFilter.getFilter("age").isLessThan());
        assertEquals(30, requestFilter.getFilter("age").getValue()[0]);
        assertTrue(requestFilter.getFilter("id").isIn());
        assertTrue(requestFilter.getFilter("address.city").isLike());
    }

    @Test
    void classContentWithSameTypeValue() {
        PageRequest pageRequest = new PageRequest();
        SearchRequest.ClassContent content = new SearchRequest.ClassContent(UserQuery.class, pageRequest);
        // String 类型字段直接返回原始值, 不需要 JSON 转换
        content.addFilter("name", Relation.EQUAL, "direct");
        assertEquals("direct", pageRequest.getRequestFilter().getFilter("name").getValue()[0]);
    }

    @Test
    void classContentUnknownField() {
        PageRequest pageRequest = new PageRequest();
        SearchRequest.ClassContent content = new SearchRequest.ClassContent(UserQuery.class, pageRequest);
        assertThrows(IllegalArgumentException.class, () -> content.addFilter("notExist", "value"));
        assertThrows(IllegalArgumentException.class, () -> content.addFilter("address.notExist", "value"));
    }

    @Test
    void paramOperation() {
        SearchRequest.ParamOperation operation = new SearchRequest.ParamOperation();
        operation.setKey("age");
        operation.setType("GREATER_THAN");
        assertEquals("age", operation.getKey());
        assertEquals("GREATER_THAN", operation.getType());
        assertEquals(Relation.GREATER_THAN, operation.getOperation());
    }
}
