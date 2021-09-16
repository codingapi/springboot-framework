package com.codingapi.springboot.permission.sql;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author lorne
 * @since 1.0.0
 */
public class CurdDeleteSqlAnalyzer extends BaseAnalyzerFilter implements SqlAnalyzerFilter{

    //Pattern
    private static final Pattern EQUAL_PATTERN = Pattern.compile("([a-zA-Z0-9_\\.]*\\s*[=]\\s*[?])");

    private static final Pattern CURD_DELETE_PATTERN = Pattern.compile("(^\\s*(delete).*)",Pattern.CASE_INSENSITIVE);

    @Override
    public boolean match(SQL sql) {
        return match(CURD_DELETE_PATTERN,sql.getSql());
    }

    @Override
    public void doFilter(SQL sql) {
        List<String> values = find(EQUAL_PATTERN,sql.getSql());
        if (values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                String[] kvs = value.split("=");
                sql.put(kvs[0].trim(), i+1);
            }
        }
    }

    @Override
    public String delete(String key, String sql) {
        //for example : update t_demo set name = ?, age = ? where id = ? , delete name will get result:update t_demo set age = ? where id = ?
        sql = sql.replaceFirst(String.format("\\s*%s\\s*=\\s*\\?\\s*[,]?",key), "");
        return sql;
    }
}
