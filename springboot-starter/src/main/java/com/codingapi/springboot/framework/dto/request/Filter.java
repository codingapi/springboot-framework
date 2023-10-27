package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Filter {

    private String key;
    private Object[] value;
    private Relation relation;

    public Filter(String key, Relation relation, Object... value) {
        this.key = key;
        this.value = value;
        this.relation = relation;
    }

    public Filter(String key, Object... value) {
        this(key, Relation.EQUAL, value);
    }

    public Filter(Filter... value) {
        this(null, null, value);
    }

    public static Filter as(String key, Relation relation, Object... value) {
        return new Filter(key, relation, value);
    }

    public static Filter as(String key, Object... value) {
        return new Filter(key, value);
    }

    public static Filter as(Filter... value) {
        return new Filter(value);
    }

    public boolean isEqual() {
        return relation == Relation.EQUAL;
    }

    public boolean isLike() {
        return relation == Relation.LIKE;
    }

    public boolean isBetween() {
        return relation == Relation.BETWEEN;
    }

    public boolean isIn() {
        return relation == Relation.IN;
    }

    public boolean isOr() {
        return value != null && value.length > 0 && value[0] instanceof Filter;
    }

    public boolean isGreaterThan() {
        return relation == Relation.GREATER_THAN;
    }

    public boolean isLessThan() {
        return relation == Relation.LESS_THAN;
    }

    public boolean isGreaterThanEqual() {
        return relation == Relation.GREATER_THAN_EQUAL;
    }

    public boolean isLessThanEqual() {
        return relation == Relation.LESS_THAN_EQUAL;
    }

    public Object getFilterValue(Class<?> clazz) {
        Object val = value[0];
        if (val instanceof String) {
            if (clazz == Integer.class) {
                return Integer.parseInt((String) val);
            }
            if (clazz == Long.class) {
                return Long.parseLong((String) val);
            }
            if (clazz == Double.class) {
                return Double.parseDouble((String) val);
            }
            if (clazz == Float.class) {
                return Float.parseFloat((String) val);
            }
        }
        return value[0];
    }

}
