package com.codingapi.springboot.authorization.enhancer;

import lombok.Getter;

import java.util.Map;

/**
 * 表字段别名
 */
public class TableColumnAlias {

    private final String parent;
    @Getter
    private final String tableName;
    @Getter
    private final String columnName;
    private final String aliasName;

    public TableColumnAlias(String parent, String tableName, String columnName, String aliasName) {
        this.parent = parent;
        this.tableName = tableName;
        this.columnName = columnName;
        if (aliasName != null) {
            this.aliasName = aliasName
                    .replaceAll("`", "")
                    .replaceAll("\"", "")
                    .replaceAll("'", "")
                    .trim();
        } else {
            this.aliasName = null;
        }
    }

    /**
     * 是否是表
     *
     * @param tableAlias 表别名
     * @return 是否是表
     */
    public boolean isTable(Map<String, String> tableAlias) {
        return tableAlias.containsKey(tableName);
    }


    /**
     * 获取父级key
     *
     * @return key
     */
    public String getParentKey() {
        return parent + "." + aliasName;
    }

    /**
     * 获取表别名key
     *
     * @return key
     */
    public String getTableAliasKey() {
        return tableName + "." + aliasName;
    }
}
