package com.codingapi.springboot.permission.analyzer;

import java.util.regex.Pattern;

/**
 * @author lorne
 * @since 1.0.0
 */
public class CurdInsertSqlAnalyzer extends BaseAnalyzerFilter implements SqlAnalyzerFilter {

    //Pattern
    private static final Pattern INSERT_PATTERN = Pattern.compile("(\\([\\sa-zA-Z0-9_,|?]*\\s*[?]+\\s*[\\sa-zA-Z0-9_,|?]*\\s*\\))");

    private static final Pattern INSERT_PATTERN_KEY = Pattern.compile("(\\([\\sa-zA-Z0-9_]+[,][\\sa-zA-Z0-9_,]*\\))");

    private static final Pattern CURD_INSERT_PATTERN = Pattern.compile("(^\\s*(insert).*)", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean match(SQL sql) {
        return match(CURD_INSERT_PATTERN, sql.getSql());
    }

    @Override
    public void doFilter(SQL sql) {
        String[] values = split(INSERT_PATTERN, sql.getSql());
        if (values != null) {
            String[] keys = split(INSERT_PATTERN_KEY, sql.getSql());
            if (keys != null) {
                int index = 0;
                for (int i = 0; i < values.length; i++) {
                    String value = values[i].trim();
                    if ("?".equals(value)) {
                        sql.put(keys[i].trim(), ++index);
                    }
                }
            }
        }
    }

    @Override
    public String delete(String key, String sql) {
        //for example : insert into t_demo(id,name) values(?,?),delete id will get result: insert into t_demo(name) values(?)
        sql = sql.replaceFirst(String.format("\\s*%s\\s*[,]?", key), "");
        sql = sql.replaceFirst("\\?\\s*[,]?", "");
        return sql;
    }
}
