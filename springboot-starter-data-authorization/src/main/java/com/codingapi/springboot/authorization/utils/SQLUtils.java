package com.codingapi.springboot.authorization.utils;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

public class SQLUtils {

    /**
     * 判断是否为查询
     */
    public static boolean isQuerySql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false; // 空字符串或 null 不是有效 SQL
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            return statement instanceof Select;
        } catch (Exception e) {
            return false;
        }
    }


}
