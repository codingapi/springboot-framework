package com.codingapi.springboot.framework.dto.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.context.CurrentPageOffsetContext;
import com.codingapi.springboot.framework.dto.ICurrentOffset;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HttpServletRequest 请求参数解析成 PageRequest对象
 */
public class SearchRequest implements ICurrentOffset {

    @Getter
    private int current;
    @Getter
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
        this.current = CurrentPageOffsetContext.getInstance().getCurrentPage(this,current);
        this.removeKeys.add("current");
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.removeKeys.add("pageSize");
    }

    public void addSort(Sort sort) {
        pageRequest.addSort(sort);
    }

    public void removeFilter(String key) {
        pageRequest.removeFilter(key);
        this.removeKeys.add(key);
    }

    public String getParameter(String key) {
        return request.getParameter(key);
    }

    public String[] getParameterValues(String key) {
        return request.getParameterValues(key);
    }


    public List<String> getParameterNames() {
        Enumeration<String> enumeration = request.getParameterNames();
        List<String> list = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list;
    }

    public PageRequest addFilter(String key, Relation relation, Object... value) {
        return pageRequest.addFilter(key, relation, value);
    }

    public PageRequest addFilter(String key, Object... value) {
        return pageRequest.addFilter(key, value);
    }

    public PageRequest andFilter(Filter... filters) {
        return pageRequest.andFilter(filters);
    }

    public PageRequest orFilters(Filter... filters) {
        return pageRequest.orFilters(filters);
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

        public void addFilter(String key, Relation relation, String value) {
            Class<?> keyClass = getKeyType(key);
            Object v = parseObject(value, keyClass);
            pageRequest.addFilter(key, relation, v);
        }

        public void addFilter(String key, String value) {
            Class<?> keyClass = getKeyType(key);
            Object v = parseObject(value, keyClass);
            pageRequest.addFilter(key, Relation.EQUAL, v);
        }

        private Object parseObject(String value, Class<?> keyClass) {
            if (value.getClass().equals(keyClass)) {
                return value;
            }
            return JSON.parseObject(value, keyClass);
        }

        public void addFilter(String key, List<String> value) {
            Class<?> keyClass = getKeyType(key);
            pageRequest.addFilter(key, Relation.IN, value.stream()
                    .map(v -> parseObject(v, keyClass))
                    .toArray()
            );
        }



        private Class<?> getKeyType(String key) {
            String[] keys = key.split("\\.");
            Class<?> keyClass = clazz;

            for (String k : keys) {
                keyClass = findFieldInHierarchy(keyClass, k);

                if (keyClass == null) {
                    throw new IllegalArgumentException("Field " + k + " not found in class hierarchy.");
                }
            }
            return keyClass;
        }

        private Class<?> findFieldInHierarchy(Class<?> clazz, String fieldName) {
            Class<?> currentClass = clazz;

            while (currentClass != null) {
                Field[] fields = currentClass.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equals(fieldName)) {
                        return field.getType();
                    }
                }
                currentClass = currentClass.getSuperclass(); // 向上查找父类
            }
            return null;
        }

    }

    @Setter
    @Getter
    static class ParamOperation {
        private String key;
        private String type;

        public Relation getOperation() {
            return Relation.valueOf(type);
        }
    }

    private List<ParamOperation> loadParamOperations() {
        String params = request.getParameter("params");
        if (StringUtils.hasLength(params)) {
            params = decode(params);
            if (JSON.isValid(params)) {
                removeKeys.add("params");
                return JSON.parseArray(params, ParamOperation.class);
            }
        }
        return null;
    }

    public PageRequest toPageRequest(Class<?> clazz) {
        pageRequest.setCurrent(current);
        pageRequest.setPageSize(pageSize);

        ClassContent content = new ClassContent(clazz, pageRequest);

        List<ParamOperation> loadParams = loadParamOperations();

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
                if(jsonObject!=null) {
                    for (String key : jsonObject.keySet()) {
                        JSONArray value = jsonObject.getJSONArray(key);
                        if (value != null && !value.isEmpty()) {
                            List<String> values = value.stream().map(Object::toString).collect(Collectors.toList());
                            content.addFilter(key, values);
                        }
                    }
                }
            }
        }

        Enumeration<String> enumeration = request.getParameterNames();

        while (enumeration.hasMoreElements()){
            String key = enumeration.nextElement();
            if (!removeKeys.contains(key)) {
                String value = request.getParameter(key);
                if (StringUtils.hasLength(value)) {
                    if (loadParams != null) {
                        ParamOperation operation = loadParams.stream()
                                .filter(paramOperation -> paramOperation.getKey().equals(key))
                                .findFirst()
                                .orElse(null);
                        if (operation != null) {
                            content.addFilter(key, operation.getOperation(), value);
                        } else {
                            content.addFilter(key, value);
                        }
                    } else {
                        content.addFilter(key, value);
                    }
                }
            }
        }
        return pageRequest;
    }


}
