package com.codingapi.springboot.fast.classloader;

import com.codingapi.springboot.fast.metadata.EntityMeta;
import jakarta.persistence.*;
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
 * 动态实体构建器 - 基于 EntityClass 元数据
 */
public class DynamicEntityClassBuilder {

    /**
     * 根据 EntityClass 构建动态实体
     */
    public static Class<?> buildDynamicEntity(EntityMeta entityMeta) {
        if (entityMeta == null || entityMeta.getClassName() == null) {
            throw new IllegalArgumentException("Entity metadata cannot be null");
        }

        try {
            DynamicType.Builder<?> builder = new ByteBuddy()
                    .subclass(Object.class)
                    .name(entityMeta.getClassName())
                    .implement(Serializable.class)
                    .annotateType(buildEntityAnnotations(entityMeta));

            // 添加字段
            boolean hasPrimaryKey = false;
            for (EntityMeta.ColumnMeta column : entityMeta.getColumns()) {
                builder = addColumnField(builder, column);
                if (column.isPrimaryKey()) {
                    hasPrimaryKey = true;
                }
            }

            // 如果没有主键，添加默认ID字段
            if (!hasPrimaryKey) {
                builder = addDefaultIdField(builder);
            }


            Class<?> clazz = builder.make()
                    .load(DynamicEntityClassBuilder.class.getClassLoader(),
                            ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded();

            DynamicEntityClassLoaderContext.getInstance().registerClass(clazz);

            return clazz;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build dynamic entity: " +
                    entityMeta.getClassName(), e);
        }
    }

    /**
     * 构建实体类注解
     */
    private static AnnotationDescription[] buildEntityAnnotations(EntityMeta entityMeta) {
        List<AnnotationDescription> annotations = new ArrayList<>();

        // @Entity 注解
        annotations.add(AnnotationDescription.Builder.ofType(Entity.class).build());

        // @Table 注解
        if (entityMeta.getTable() != null) {
            AnnotationDescription.Builder tableBuilder =
                    AnnotationDescription.Builder.ofType(Table.class);

            EntityMeta.TableMeta tableMeta = entityMeta.getTable();
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
        if (entityMeta.getTable() != null && StringUtils.hasText(entityMeta.getTable().getComment())) {
            AnnotationDescription.Builder commentBuilder =
                    AnnotationDescription.Builder.ofType(Comment.class);

            EntityMeta.TableMeta tableMeta = entityMeta.getTable();
            commentBuilder = commentBuilder.define("value", tableMeta.getComment());
            annotations.add(commentBuilder.build());
        }

        return annotations.toArray(new AnnotationDescription[0]);
    }

    /**
     * 添加字段
     */
    private static DynamicType.Builder<?> addColumnField(DynamicType.Builder<?> builder,
                                                         EntityMeta.ColumnMeta columnMeta) {
        // 确定字段类型
        Class<?> fieldType = columnMeta.getType();

        // 构建字段名（转换驼峰命名）
        String fieldName = convertToCamelCase(columnMeta.getName());



        // 构建字段注解
        List<AnnotationDescription> fieldAnnotations =
                buildFieldAnnotations(columnMeta, fieldName);

        // 开始定义字段
        DynamicType.Builder<?> fieldBuilder = builder;


        if (fieldAnnotations.isEmpty()) {
            fieldBuilder = fieldBuilder.defineField(fieldName, fieldType, Visibility.PRIVATE);
        }else {
            fieldBuilder = fieldBuilder.defineField(fieldName, fieldType, Visibility.PRIVATE)
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

        fieldBuilder = fieldBuilder
                .defineMethod(getterName, fieldType, Visibility.PUBLIC)
                .intercept(FieldAccessor.ofField(fieldName));

        fieldBuilder = fieldBuilder
                .defineMethod(setterName, void.class, Visibility.PUBLIC)
                .withParameter(fieldType)
                .intercept(FieldAccessor.ofField(fieldName));

        return fieldBuilder;
    }

    /**
     * 构建字段注解
     */
    private static List<AnnotationDescription> buildFieldAnnotations(
            EntityMeta.ColumnMeta columnMeta, String fieldName) {

        List<AnnotationDescription> annotations = new ArrayList<>();

        // @Id 注解
        if (columnMeta.isPrimaryKey()) {
            annotations.add(AnnotationDescription.Builder.ofType(Id.class).build());

            // @GeneratedValue 注解
            if (columnMeta.getGeneratedValue() != null) {
                EntityMeta.GeneratedValueMeta genMeta = columnMeta.getGeneratedValue();
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

        // @Column 注解
        AnnotationDescription.Builder columnBuilder =
                AnnotationDescription.Builder.ofType(Column.class);

        columnBuilder = columnBuilder
                .define("name", columnMeta.getName())
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

    /**
     * 添加默认ID字段 - 修正版本
     */
    private static DynamicType.Builder<?> addDefaultIdField(DynamicType.Builder<?> builder) {


        // 创建字段注解
        List<AnnotationDescription> fieldAnnotations = new ArrayList<>();
        fieldAnnotations.add(AnnotationDescription.Builder.ofType(Id.class).build());

        // @GeneratedValue 注解
        AnnotationDescription.Builder genBuilder =
                AnnotationDescription.Builder.ofType(GeneratedValue.class);
        genBuilder = genBuilder.define("strategy", GenerationType.IDENTITY);
        fieldAnnotations.add(genBuilder.build());

        // @Column 注解
        AnnotationDescription.Builder columnBuilder =
                AnnotationDescription.Builder.ofType(Column.class);
        columnBuilder = columnBuilder
                .define("name", "id")
                .define("nullable", false);
        fieldAnnotations.add(columnBuilder.build());

        // 应用字段注解
        builder = builder.defineField("id", Long.class, Visibility.PRIVATE).annotateField(fieldAnnotations.toArray(new AnnotationDescription[0]));

        // 添加 getter 和 setter
        builder = builder
                .defineMethod("getId", Long.class, Visibility.PUBLIC)
                .intercept(FieldAccessor.ofField("id"))
                .defineMethod("setId", void.class, Visibility.PUBLIC)
                .withParameter(Long.class)
                .intercept(FieldAccessor.ofField("id"));

        return builder;
    }

    /**
     * 转换为驼峰命名
     */
    private static String convertToCamelCase(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        // 处理下划线命名
        if (name.contains("_")) {
            StringBuilder result = new StringBuilder();
            String[] parts = name.split("_");
            if (parts.length > 0) {
                result.append(parts[0].toLowerCase());

                for (int i = 1; i < parts.length; i++) {
                    if (!parts[i].isEmpty()) {
                        result.append(Character.toUpperCase(parts[i].charAt(0)));
                        if (parts[i].length() > 1) {
                            result.append(parts[i].substring(1).toLowerCase());
                        }
                    }
                }
            }
            return result.toString();
        }

        // 已经是驼峰命名，首字母小写
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}