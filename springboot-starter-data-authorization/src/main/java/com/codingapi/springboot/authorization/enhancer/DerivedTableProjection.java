package com.codingapi.springboot.authorization.enhancer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 派生表（FROM/JOIN 子查询）输出列到物理表字段的投影映射。
 * <p>
 * 解决 Issue #212：分页包装 SQL（SELECT 裸列 FROM (...) AS __base__）执行后，
 * 结果集元数据中的表名是派生表别名（或空串），无法直接映射回物理表字段。
 * 通过记录派生表每一列的来源（显式列引用 / 唯一 * 展开），实现结果列归因。
 */
class DerivedTableProjection {

    /**
     * 显式投影列：key = 输出列名（小写），value = [物理表名, 物理字段名]
     */
    private final Map<String, String[]> explicitColumns = new HashMap<>();

    /**
     * 表达式/函数 AS 别名等非物理列派生出的输出列（小写），不参与 * 归因
     */
    private final Set<String> expressionColumns = new HashSet<>();

    /**
     * 唯一 * 来源的物理表名；为 null 且 starAmbiguous 为 false 时表示无 * 投影
     */
    private String starTable;

    /**
     * * 来源是否歧义（多个 * 或多表 SELECT *），歧义时不做 * 归因
     */
    private boolean starAmbiguous;

    /**
     * 添加显式投影列
     *
     * @param columnKey  输出列名（小写）
     * @param tableName  物理表名
     * @param columnName 物理字段名
     */
    void putExplicit(String columnKey, String tableName, String columnName) {
        explicitColumns.put(columnKey, new String[]{tableName, columnName});
    }

    /**
     * 标记非物理列（表达式 AS 别名）
     *
     * @param columnKey 输出列名（小写）
     */
    void markExpression(String columnKey) {
        expressionColumns.add(columnKey);
    }

    /**
     * 添加 * 展开来源的物理表；出现多个来源时视为歧义
     *
     * @param tableName 物理表名，null 表示来源不可解析
     */
    void addStarTable(String tableName) {
        if (tableName == null || starTable != null) {
            starTable = null;
            starAmbiguous = true;
        } else {
            starTable = tableName;
        }
    }

    /**
     * 标记 * 来源歧义
     */
    void markStarAmbiguous() {
        starTable = null;
        starAmbiguous = true;
    }

    /**
     * * 展开来源的物理表名（链式解析 t.* 来源时使用）
     */
    String getStarTable() {
        return starTable;
    }

    /**
     * * 来源是否歧义（链式解析 t.* 来源时使用）
     */
    boolean isStarAmbiguous() {
        return starAmbiguous;
    }

    /**
     * 将输出列解析为物理表字段
     *
     * @param columnKey  输出列名（小写）
     * @param columnName 输出列名（原样，用于 * 展开归因）
     * @return [物理表名, 物理字段名]，无法归因时返回 null
     */
    String[] find(String columnKey, String columnName) {
        String[] reference = explicitColumns.get(columnKey);
        if (reference != null) {
            return reference;
        }
        if (expressionColumns.contains(columnKey)) {
            return null;
        }
        if (starTable != null) {
            return new String[]{starTable, columnName};
        }
        return null;
    }
}
