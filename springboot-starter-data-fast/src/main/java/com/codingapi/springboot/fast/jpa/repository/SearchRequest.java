package com.codingapi.springboot.fast.jpa.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.Relation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class SearchRequest {

    private int current;
    private int pageSize;

    private final HttpServletRequest request;

    private final List<String> removeKeys = new ArrayList<>();

    private final PageRequest pageRequest;

    public SearchRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        this.request = attributes.getRequest();
        this.pageRequest = new PageRequest();
    }

    public void setCurrent(int current) {
        this.current = current - 1;
        this.removeKeys.add("current");
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.removeKeys.add("pageSize");
    }

    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value));
    }


    static class ClassContent {

        private final Class<?> clazz;
        private final PageRequest pageRequest;

        public ClassContent(Class<?> clazz, PageRequest pageRequest) {
            this.clazz = clazz;
            this.pageRequest = pageRequest;
        }

        public void addFilter(String key, String value) {
            Class<?> keyClass = getKeyClass(key);
            Object v = JSON.parseObject(value, keyClass);
            pageRequest.addFilter(key, Relation.EQUAL, v);
        }

        public void addFilter(String key, List<String> value) {
            Class<?> keyClass = getKeyClass(key);
            pageRequest.addFilter(key, Relation.IN, value.stream()
                    .map(v -> JSON.parseObject(v, keyClass))
                    .toArray()
            );
        }


        private Class<?> getKeyClass(String key) {
            String[] keys = key.split("\\.");
            Class<?> keyClass = clazz;
            for (String k : keys) {
                Field[] fields = keyClass.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equals(k)) {
                        keyClass = field.getType();
                        break;
                    }
                }
            }
            return keyClass;
        }

    }

    PageRequest toPageRequest(Class<?> clazz) {
        pageRequest.setCurrent(current);
        pageRequest.setPageSize(pageSize);

        ClassContent content = new ClassContent(clazz, pageRequest);

        String sort = request.getParameter("sort");
        if (StringUtils.hasLength(sort)) {
            sort = decode(sort);
            if (JSON.isValid(sort)) {
                removeKeys.add("sort");
                JSONObject jsonObject = JSON.parseObject(sort);
                for (String key : jsonObject.keySet()) {
                    String value = jsonObject.getString(key);
                    if ("ascend".equals(value)) {
                        pageRequest.addSort(Sort.by(key).ascending());
                    } else {
                        pageRequest.addSort(Sort.by(key).descending());
                    }
                }
            }
        }


        String filter = request.getParameter("filter");
        if (StringUtils.hasLength(filter)) {
            filter = decode(filter);
            if (JSON.isValid(filter)) {
                removeKeys.add("filter");
                JSONObject jsonObject = JSON.parseObject(filter);
                for (String key : jsonObject.keySet()) {
                    JSONArray value = jsonObject.getJSONArray(key);
                    if (value != null && !value.isEmpty()) {
                        List<String> values = value.stream().map(Object::toString).toList();
                        content.addFilter(key, values);
                    }
                }
            }
        }


        request.getParameterNames().asIterator().forEachRemaining(key -> {
            if (!removeKeys.contains(key)) {
                String value = request.getParameter(key);
                if (StringUtils.hasLength(value)) {
                    content.addFilter(key, value);
                }
            }
        });

        return pageRequest;
    }


}
