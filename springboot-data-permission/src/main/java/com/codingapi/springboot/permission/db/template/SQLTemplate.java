package com.codingapi.springboot.permission.db.template;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface SQLTemplate {

    /**
     * @param  driverName connection driverName
     * @return match result
     */
    boolean match(String driverName);

    String insert();

    String create(String tableName);

}
