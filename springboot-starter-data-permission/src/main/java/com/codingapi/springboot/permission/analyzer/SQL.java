package com.codingapi.springboot.permission.analyzer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class SQL {

    // jdbc sql
    @Getter
    private String sql;

    // jdbc sql to char arrays.
    @Getter
    private char[] sqlChars;

    //jdbc sql parameter key and values,for example insert into t_demo(id,name) values(?,?), keys:id,name value:1,2
    private final Map<String, Integer> parameterKeys = new HashMap<>();

    // init jdbc sql parameterKeys
    private final Map<String, Integer> initParameters = new HashMap<>();

    // remove keys index values
    @Getter
    private final List<Integer> removeIndexes = new ArrayList<>();


    public SQL(String sql) {
        this.sql = sql;
        this.sqlChars = sql.toCharArray();
        this.init();
    }

    private void init() {
        for (SqlAnalyzerFilter filter : AnalyzerFilterContext.getInstance().getFilters()) {
            if (filter.match(this)) {
                filter.doFilter(this);
            }
        }
        this.copyParameter();
    }

    public boolean hasRemoveIndex(int index) {
        return removeIndexes.contains(index);
    }

    public void put(String parameterKey, int index) {
        parameterKeys.put(parameterKey, index);
    }

    public void deleteKey(String key) {
        Integer value = parameterKeys.get(key);
        if (value != null) {
            for (SqlAnalyzerFilter filter : AnalyzerFilterContext.getInstance().getFilters()) {
                if (filter.match(this)) {
                    sql = filter.delete(key, sql);
                }
            }
            sql = sql.trim();
            //when endsWith where will append 1=1
            if (sql.toUpperCase().endsWith("WHERE")) {
                sql = sql + " 1=1";
            }
            removeIndexes.add(value);
            parameterKeys.remove(key);
            this.sqlChars = sql.toCharArray();
        }
    }

    public void copyParameter() {
        for (String key : parameterKeys.keySet()) {
            initParameters.put(key, parameterKeys.get(key));
        }
    }

    public int getIndex(String parameterKey) {
        Integer value = initParameters.get(parameterKey);
        if (value == null) {
            return 0;
        }
        return value;
    }


    @Override
    public String toString() {
        return getSql();
    }
}
