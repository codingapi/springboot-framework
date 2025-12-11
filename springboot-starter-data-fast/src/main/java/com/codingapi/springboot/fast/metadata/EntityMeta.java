package com.codingapi.springboot.fast.metadata;

import com.codingapi.springboot.fast.classloader.DynamicEntityClassBuilder;
import com.codingapi.springboot.fast.dynamic.DynamicEntityBuilder;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EntityMeta {

    private final String className;
    private final TableMeta table;
    private final List<ColumnMeta> columns;

    public EntityMeta(String className) {
        this.className = className;
        this.table = new TableMeta();
        this.columns = new ArrayList<>();
    }

    public void setTable(String name,String catalog,String schema,String comment){
        this.table.setName(name);
        this.table.setCatalog(catalog);
        this.table.setSchema(schema);
        this.table.setComment(comment);
    }

    public void setTable(String name,String comment){
        this.setTable(name,null,null,comment);
    }

    public void setTable(String name){
        this.setTable(name,null,null,null);
    }

    public void addPrimaryKeyColumn(Class<?> type,String name,GenerationType strategy,String generator,String comment,boolean unique,boolean nullable, boolean insertable,
                                    boolean updatable,String columnDefinition, int length,int precision,int scale){
        ColumnMeta column = new ColumnMeta();
        column.setType(type);
        column.setName(name);
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

    public void addColumn(Class<?> type,String name,String comment,boolean unique,boolean nullable, boolean insertable,
                                    boolean updatable,String columnDefinition, int length,int precision,int scale){
        ColumnMeta column = new ColumnMeta();
        column.setPrimaryKey(false);
        column.setType(type);
        column.setName(name);
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

    public void addColumn(Class<?> type,String name,String comment){
        this.addColumn(type,name,comment,false,false,false,false,null,255,0,0);
    }

    public void addColumn(Class<?> type,String name){
        this.addColumn(type,name,null,false,false,false,false,null,255,0,0);
    }

    public void addPrimaryKeyColumn(Class<?> type,String name,GenerationType strategy){
        this.addPrimaryKeyColumn(type,name,strategy,null,null,false,false,false,false,null,255,0,0);
    }

    public void addPrimaryKeyColumn(Class<?> type,String name,GenerationType strategy,String comment){
        this.addPrimaryKeyColumn(type,name,strategy,null,comment,false,false,false,false,null,255,0,0);
    }

    public Class<?> buildClass(){
        return DynamicEntityClassBuilder.buildDynamicEntity(this);
    }

    @Setter
    @Getter
    public static class TableMeta{
        private String name;
        private String catalog;
        private String schema;
        private String comment;
    }

    @Setter
    @Getter
    public static class ColumnMeta{
        private Class<?> type;
        private String name;
        private boolean isPrimaryKey;
        private GeneratedValueMeta generatedValue;
        private String comment;
        private boolean unique;
        private boolean nullable;
        private boolean insertable;
        private boolean updatable;
        private String columnDefinition;
        private int length;
        private int precision;
        private int scale;
    }

    @Setter
    @Getter
    public static class GeneratedValueMeta{
        private GenerationType strategy;
        private String generator;
    }

}
