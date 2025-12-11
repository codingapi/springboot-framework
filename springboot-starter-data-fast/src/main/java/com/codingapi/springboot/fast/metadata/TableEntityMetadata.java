package com.codingapi.springboot.fast.metadata;

import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 表实体元数据
 */
@Getter
public class TableEntityMetadata {

    /**
     * 类名称
     */
    private final String className;
    /**
     * 数据库表信息
     */
    private final TableMeta table;
    /**
     * 字段信息
     */
    private final List<ColumnMeta> columns;

    @Setter
    @Getter
    public static class TableMeta {
        /**
         * 数据库名称
         */
        private String name;
        private String catalog;
        private String schema;
        /**
         * 备注信息
         */
        private String comment;
    }

    @Setter
    @Getter
    public static class ColumnMeta {
        /**
         * 字段类型
         */
        private Class<?> type;
        /**
         * 表字段名称
         */
        private String columnName;
        /**
         * 类字段名称
         */
        private String fieldName;
        /**
         * 是否主键
         */
        private boolean isPrimaryKey;
        /**
         * 主键自增策略
         */
        private GeneratedValueMeta generatedValue;
        /**
         * 备注
         */
        private String comment;
        /**
         * 是否唯一
         */
        private boolean unique;
        /**
         * 是否为空
         */
        private boolean nullable;
        /**
         * 是否可以插入数据
         */
        private boolean insertable;
        /**
         * 是否可以更新数据
         */
        private boolean updatable;
        /**
         * 字段定义
         */
        private String columnDefinition;
        /**
         * 字段长度
         */
        private int length;
        private int precision;
        private int scale;
    }

    @Setter
    @Getter
    public static class GeneratedValueMeta {
        /**
         * 自增长策略
         */
        private GenerationType strategy;
        /**
         * 自定义
         */
        private String generator;
    }


    /**
     * 类名称
     *
     * @param className 类名称
     */
    public TableEntityMetadata(String className) {
        this.className = className;
        this.table = new TableMeta();
        this.columns = new ArrayList<>();
    }


    public void setTable(String name, String catalog, String schema, String comment) {
        this.table.setName(name);
        this.table.setCatalog(catalog);
        this.table.setSchema(schema);
        this.table.setComment(comment);
    }

    public void setTable(String name, String comment) {
        this.setTable(name, null, null, comment);
    }

    public void setTable(String name) {
        this.setTable(name, null, null, null);
    }

    public void addPrimaryKeyColumn(Class<?> type, String fieldName, String columnName, GenerationType strategy,
                                    String generator, String comment, boolean unique, boolean nullable,
                                    boolean insertable, boolean updatable, String columnDefinition,
                                    int length, int precision, int scale) {
        ColumnMeta column = new ColumnMeta();
        column.setType(type);
        column.setFieldName(fieldName);
        column.setColumnName(columnName);
        column.setPrimaryKey(true);
        GeneratedValueMeta generatedValueMeta = new GeneratedValueMeta();
        generatedValueMeta.setGenerator(generator);
        generatedValueMeta.setStrategy(strategy);
        column.setGeneratedValue(generatedValueMeta);
        column.setComment(comment);
        column.setUnique(unique);
        column.setNullable(nullable);
        column.setInsertable(insertable);
        column.setUpdatable(updatable);
        column.setColumnDefinition(columnDefinition);
        column.setLength(length);
        column.setPrecision(precision);
        column.setScale(scale);
        this.columns.add(column);
    }

    public void addColumn(Class<?> type, String fieldName, String columnName, String comment,
                          boolean unique, boolean nullable, boolean insertable,
                          boolean updatable, String columnDefinition, int length,
                          int precision, int scale) {
        ColumnMeta column = new ColumnMeta();
        column.setPrimaryKey(false);
        column.setType(type);
        column.setFieldName(fieldName);
        column.setColumnName(columnName);
        column.setComment(comment);
        column.setUnique(unique);
        column.setNullable(nullable);
        column.setInsertable(insertable);
        column.setUpdatable(updatable);
        column.setColumnDefinition(columnDefinition);
        column.setLength(length);
        column.setPrecision(precision);
        column.setScale(scale);
        this.columns.add(column);
    }

    public void addColumn(Class<?> type, String name, String comment) {
        this.addColumn(type, name, name, comment, false, false, false, false, null, 255, 0, 0);
    }

    public void addColumn(Class<?> type, String name) {
        this.addColumn(type, name, name, null, false, false, false, false, null, 255, 0, 0);
    }

    public void addPrimaryKeyColumn(Class<?> type, String name) {
        this.addPrimaryKeyColumn(type, name, name, null, null, null, false, false, false, false, null, 255, 0, 0);
    }

    public void addPrimaryKeyColumn(Class<?> type, String name, GenerationType strategy) {
        this.addPrimaryKeyColumn(type, name, name, strategy, null, null, false, false, false, false, null, 255, 0, 0);
    }

    public void addPrimaryKeyColumn(Class<?> type, String name, GenerationType strategy, String comment) {
        this.addPrimaryKeyColumn(type, name, name, strategy, null, comment, false, false, false, false, null, 255, 0, 0);
    }

    public void verify() {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("columns must not null.");
        }
        boolean hasPrimaryKey = false;
        for (ColumnMeta column : columns) {
            if (column.isPrimaryKey) {
                hasPrimaryKey = true;
            }
        }
        if (!hasPrimaryKey) {
            throw new IllegalArgumentException("columns must has primaryKey.");
        }
    }

    public Class<?> buildClass() {
        this.verify();
        TableEntityClassBuilder builder = new TableEntityClassBuilder(this);
        return builder.build();
    }


}
