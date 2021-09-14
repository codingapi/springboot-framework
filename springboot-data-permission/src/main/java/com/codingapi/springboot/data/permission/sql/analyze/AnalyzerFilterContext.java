package com.codingapi.springboot.data.permission.sql.analyze;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
public class AnalyzerFilterContext {

    private static AnalyzerFilterContext instance;

    private final List<SqlAnalyzerFilter> filters;

    private AnalyzerFilterContext(){
        this.filters = new ArrayList<>();
    }

    public static AnalyzerFilterContext getInstance() {
        if(instance==null){
            synchronized (AnalyzerFilterContext.class){
                if(instance==null){
                    instance = new AnalyzerFilterContext();
                }
            }
        }
        return instance;
    }

    protected void addFilter(SqlAnalyzerFilter filter){
        this.filters.add(filter);
    }

    protected void addFilters(List<SqlAnalyzerFilter> filters){
        this.filters.addAll(filters);
    }

    public List<SqlAnalyzerFilter> getFilters() {
        return filters;
    }
}
