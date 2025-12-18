package com.codingapi.springboot.fast.metadata;

import com.codingapi.springboot.fast.classloader.DynamicTableClassLoader;
import javax.persistence.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import org.hibernate.annotations.Comment;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态实体构建器 - 基于 TableEntityMetadata 元数据
 */
class TableEntityClassBuilder {

    private final TableEntityMetadata metadata;
    private DynamicType.Builder<?> builder;

    public TableEntityClassBuilder(TableEntityMetadata metadata) {
        if (metadata == null || metadata.getClassName() == null) {
            throw new IllegalArgumentException("Entity metadata cannot be null");
        }
        this.metadata = metadata;
        this.builder = new ByteBuddy()
                .subclass(Object.class)
                .name(metadata.getClassName())
                .implement(Serializable.class)
                .annotateType(buildEntityAnnotations());
    }

    public Class<?> build() {
        try {
            this.buildColumns();
            Class<?> clazz = builder.make()
                    .load(TableEntityClassBuilder.class.getClassLoader(),
                            ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded();
            DynamicTableClassLoader.getInstance().registerClass(clazz);
            return clazz;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build dynamic entity: " +
                    metadata.getClassName(), e);
        }
    }


    private void buildColumns() {
        for (TableEntityMetadata.ColumnMeta column : metadata.getColumns()) {
            this.addColumnField(column);
        }
    }


    /**
     * 构建实体类注解
     */
    private AnnotationDescription[] buildEntityAnnotations() {
        List<AnnotationDescription> annotations = new ArrayList<>();

        // @Entity 注解
        annotations.add(AnnotationDescription.Builder.ofType(Entity.class).build());

        // @Table 注解
        if (metadata.getTable() != null) {
            AnnotationDescription.Builder tableBuilder =
                    AnnotationDescription.Builder.ofType(Table.class);

            TableEntityMetadata.TableMeta tableMeta = metadata.getTable();
            if (tableMeta.getName() != null && !tableMeta.getName().isEmpty()) {
                tableBuilder = tableBuilder.define("name", tableMeta.getName());
            }
            if (tableMeta.getCatalog() != null && !tableMeta.getCatalog().isEmpty()) {
                tableBuilder = tableBuilder.define("catalog", tableMeta.getCatalog());
            }
            if (tableMeta.getSchema() != null && !tableMeta.getSchema().isEmpty()) {
                tableBuilder = tableBuilder.define("schema", tableMeta.getSchema());
            }

            annotations.add(tableBuilder.build());
        }

        // @Comment 注解 - 注释应该放在类上，而不是表注解上
        if (metadata.getTable() != null && StringUtils.hasText(metadata.getTable().getComment())) {
            AnnotationDescription.Builder commentBuilder =
                    AnnotationDescription.Builder.ofType(Comment.class);

            TableEntityMetadata.TableMeta tableMeta = metadata.getTable();
            commentBuilder = commentBuilder.define("value", tableMeta.getComment());
            annotations.add(commentBuilder.build());
        }

        return annotations.toArray(new AnnotationDescription[0]);
    }

    /**
     * 添加字段
     */
    private void addColumnField(TableEntityMetadata.ColumnMeta columnMeta) {
        // 确定字段类型
        Class<?> fieldType = columnMeta.getType();

        // 构建字段名
        String fieldName = columnMeta.getFieldName();

        // 构建字段注解
        List<AnnotationDescription> fieldAnnotations = this.buildFieldAnnotations(columnMeta);

        if (fieldAnnotations.isEmpty()) {
            builder = builder.defineField(fieldName, fieldType, Visibility.PRIVATE);
        } else {
            builder = builder.defineField(fieldName, fieldType, Visibility.PRIVATE)
                    .annotateField(fieldAnnotations.toArray(new AnnotationDescription[0]));
        }

        // 添加 getter 和 setter
        String capitalizedFieldName = capitalize(fieldName);
        String getterName = "get" + capitalizedFieldName;
        String setterName = "set" + capitalizedFieldName;

        // 布尔类型特殊处理
        if (fieldType == Boolean.class || fieldType == Boolean.TYPE) {
            getterName = "is" + capitalizedFieldName;
        }

        builder = builder.defineMethod(getterName, fieldType, Visibility.PUBLIC)
                .intercept(FieldAccessor.ofField(fieldName));

        builder =  builder.defineMethod(setterName, void.class, Visibility.PUBLIC)
                .withParameter(fieldType)
                .intercept(FieldAccessor.ofField(fieldName));

    }

    /**
     * 构建字段注解
     */
    private List<AnnotationDescription> buildFieldAnnotations(
            TableEntityMetadata.ColumnMeta columnMeta) {
        List<AnnotationDescription> annotations = new ArrayList<>();

        // @Id 注解
        if (columnMeta.isPrimaryKey()) {
            annotations.add(AnnotationDescription.Builder.ofType(Id.class).build());

            // @GeneratedValue 注解
            if (columnMeta.getGeneratedValue() != null) {
                TableEntityMetadata.GeneratedValueMeta genMeta = columnMeta.getGeneratedValue();
                AnnotationDescription.Builder genBuilder =
                        AnnotationDescription.Builder.ofType(GeneratedValue.class);

                if (genMeta.getStrategy() != null) {
                    genBuilder = genBuilder.define("strategy", genMeta.getStrategy());
                }

                if (genMeta.getGenerator() != null && !genMeta.getGenerator().isEmpty()) {
                    genBuilder = genBuilder.define("generator", genMeta.getGenerator());
                }

                annotations.add(genBuilder.build());
            }
        }

        // @Lob 注解
        if (columnMeta.isLob()) {
            AnnotationDescription.Builder logBuilder =
                    AnnotationDescription.Builder.ofType(Lob.class);
            annotations.add(logBuilder.build());
        }

        // @Column 注解
        AnnotationDescription.Builder columnBuilder =
                AnnotationDescription.Builder.ofType(Column.class);

        columnBuilder = columnBuilder
                .define("name", columnMeta.getColumnName())
                .define("nullable", columnMeta.isNullable())
                .define("unique", columnMeta.isUnique())
                .define("insertable", columnMeta.isInsertable())
                .define("updatable", columnMeta.isUpdatable());

        if (columnMeta.getLength() > 0) {
            columnBuilder = columnBuilder.define("length", columnMeta.getLength());
        }
        if (columnMeta.getPrecision() > 0) {
            columnBuilder = columnBuilder.define("precision", columnMeta.getPrecision());
        }
        if (columnMeta.getScale() > 0) {
            columnBuilder = columnBuilder.define("scale", columnMeta.getScale());
        }
        if (columnMeta.getColumnDefinition() != null &&
                !columnMeta.getColumnDefinition().isEmpty()) {
            columnBuilder = columnBuilder.define("columnDefinition",
                    columnMeta.getColumnDefinition());
        }

        annotations.add(columnBuilder.build());

        // @Comment 注解
        if (StringUtils.hasText(columnMeta.getComment())) {
            annotations.add(AnnotationDescription.Builder.ofType(Comment.class)
                    .define("value", columnMeta.getComment())
                    .build());
        }

        return annotations;
    }


    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}