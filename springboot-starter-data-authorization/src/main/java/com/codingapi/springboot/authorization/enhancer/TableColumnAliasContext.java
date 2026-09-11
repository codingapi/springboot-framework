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

    /**
     * 派生表投影：key = 派生表别名（小写）
     */
    private final Map<String, DerivedTableProjection> derivedTables;

    protected TableColumnAliasContext() {
        this.columnAliases = new ArrayList<>();
        this.tableAlias = new HashMap<>();
        this.columnAliasMap = new HashMap<>();
        this.derivedTables = new HashMap<>();
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
     * 注册派生表投影
     * @param alias 派生表别名
     * @param projection 投影映射
     */
    protected void addDerivedTable(String alias, DerivedTableProjection projection) {
        if (alias != null && projection != null) {
            derivedTables.put(alias.toLowerCase(), projection);
        }
    }

    /**
     * 按表引用名（表名或别名，忽略大小写）获取物理表名
     * @param tableRef 表引用名
     * @return 物理表名，未找到返回 null
     */
    protected String resolvePhysicalTable(String tableRef) {
        if (tableRef == null || tableRef.isEmpty()) {
            return null;
        }
        for (Map.Entry<String, String> entry : tableAlias.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(tableRef)) {
                return entry.getValue();
            }
        }
        for (String tableName : tableAlias.values()) {
            if (tableName.equalsIgnoreCase(tableRef)) {
                return tableName;
            }
        }
        return null;
    }

    /**
     * 获取派生表投影（忽略大小写）
     * @param tableRef 派生表别名
     * @return 投影映射，未找到返回 null
     */
    protected DerivedTableProjection getDerivedTable(String tableRef) {
        if (tableRef == null || tableRef.isEmpty()) {
            return null;
        }
        return derivedTables.get(tableRef.toLowerCase());
    }

    /**
     * 联合解析结果集列的（表名, 字段名）到物理表字段。
     * <p>
     * 元数据表名命中派生表别名、或为空/未知（部分数据库对派生表列上报空表名，
     * 见 Issue #212）时，沿派生表投影归因；否则回退到既有解析行为。
     *
     * @param tableName 元数据表名（或别名）
     * @param columnName 元数据字段名（或别名）
     * @return [物理表名, 物理字段名]
     */
    public String[] resolveTableNameAndColumn(String tableName, String columnName) {
        String[] derived = resolveDerivedColumn(tableName, columnName);
        if (derived != null) {
            return derived;
        }
        return new String[]{getTableName(tableName), getColumnName(tableName, columnName)};
    }

    private String[] resolveDerivedColumn(String tableName, String columnName) {
        if (derivedTables.isEmpty() || columnName == null || columnName.isEmpty()) {
            return null;
        }
        String columnKey = columnName.toLowerCase();
        String tableKey = tableName == null ? "" : tableName.toLowerCase();
        if (!tableKey.isEmpty()) {
            DerivedTableProjection projection = derivedTables.get(tableKey);
            if (projection != null) {
                return projection.find(columnKey, columnName);
            }
            // 已知物理表/别名：维持原有解析，不做派生归因，避免误匹配
            for (String alias : tableAlias.keySet()) {
                if (alias.equalsIgnoreCase(tableKey)) {
                    return null;
                }
            }
            for (String physicalTable : tableAlias.values()) {
                if (physicalTable.equalsIgnoreCase(tableKey)) {
                    return null;
                }
            }
        }
        // 表名为空或未知：在所有派生表投影中做唯一匹配（匹配到多个不同物理表时不归因）
        String[] matched = null;
        for (DerivedTableProjection projection : derivedTables.values()) {
            String[] reference = projection.find(columnKey, columnName);
            if (reference != null) {
                if (matched != null && !matched[0].equalsIgnoreCase(reference[0])) {
                    return null;
                }
                matched = reference;
            }
        }
        return matched;
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
