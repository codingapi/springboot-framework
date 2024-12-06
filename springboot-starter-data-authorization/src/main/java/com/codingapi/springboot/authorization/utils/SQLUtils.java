package com.codingapi.springboot.authorization.utils;

import java.util.regex.Pattern;

public class SQLUtils {

    // 定义正则表达式
    private static final Pattern QUERY_SQL_PATTERN = Pattern.compile(
            "^\\s*select\\s+(?!.*\\binto\\b).*",
            Pattern.CASE_INSENSITIVE // 忽略大小写
    );

    /**
     * 判断是否为查询 SQL（排除 SELECT INTO 类型的语句）
     */
    public static boolean isQuerySql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false; // 空字符串或 null 不是有效 SQL
        }

        // 使用正则表达式匹配
        return QUERY_SQL_PATTERN.matcher(sql.trim()).matches();
    }


}
