package com.codingapi.springboot.authorization.enhancer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  表、字段别名上下文
 */
public class TableColumnAliasContext {

    private final List<TableColumnAlias> columnAliases;
    @Getter
    private final Map<String, String> tableAlias;

    private final Map<String, String> columnAliasMap;

    protected TableColumnAliasContext() {
        this.columnAliases = new ArrayList<>();
        this.tableAlias = new HashMap<>();
        this.columnAliasMap = new HashMap<>();
    }

    /**
     * 添加表别名
     * @param tableAlias 表别名
     * @param tableName 表名
     */
    protected void addTable(String tableAlias, String tableName) {
        this.tableAlias.put(tableAlias, tableName);
    }

    /**
     * 添加字段别名
     * @param parent 父级（上级别名）
     * @param tableName 表名
     * @param columnName 字段名
     * @param aliasName 别名
     */
    protected void addColumn(String parent, String tableName, String columnName, String aliasName) {
        TableColumnAlias tableColumnAlias = new TableColumnAlias(parent, tableName, columnName, aliasName);
        columnAliases.add(tableColumnAlias);
    }

    /**
     * 列别名转换为map
     */
    protected void columnKeyToMap() {
        for (TableColumnAlias tableColumnAlias : columnAliases) {
            if (tableColumnAlias.isTable(tableAlias)) {
                String parentKey = tableColumnAlias.getParentKey();
                String tableAliasName = tableColumnAlias.getTableName();
                String tableName = this.getTableName(tableAliasName);
                String columnValue = tableName + "." + tableColumnAlias.getColumnName();
                columnAliasMap.put(parentKey, columnValue);
                columnAliasMap.put(tableColumnAlias.getTableAliasKey(), columnValue);
            }
        }
    }

    /**
     * 获取表名（真实表名）
     * @param tableName 表名或表别名
     * @return 真实表名
     */
    public String getTableName(String tableName) {
        String value = tableAlias.get(tableName);
        if (value != null) {
            return value;
        }
        return tableName;
    }


    /**
     * 获取字段名（真实字段名）
     * @param tableName 表名或表别名
     * @param columnName 字段名
     * @return 真实字段名
     */
    public String getColumnName(String tableName, String columnName) {
        String key = tableName + "." + columnName;
        String value = columnAliasMap.get(key);
        if (value != null) {
            return value.split("\\.")[1];
        }
        return columnName;
    }
}
