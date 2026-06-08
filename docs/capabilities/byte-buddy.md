---
name: byte-buddy
description: 字节码操作库，用于运行时动态生成 JPA 实体类
status: 已实现
scope: 后端
source: 框架:Byte Buddy
---

## 解决什么问题

在 springboot-starter-data-fast 模块中，需要支持**动态表结构**场景：
- 根据元数据（`TableEntityMetadata`）在运行时动态生成 JPA 实体类
- 生成的实体类需要携带 `@Entity`、`@Table`、`@Column`、`@Id` 等 JPA 注解
- 生成的类需要有标准的 getter/setter 方法
- 生成的类需要注册到自定义 ClassLoader 中以被 Hibernate/JPA 识别

Byte Buddy 提供了类型安全的字节码操作 API，可以在运行时创建带有注解和方法的 Java 类，无需编译期代码生成。

## 如何使用

### 依赖引入

```xml
<!-- pom.xml 中已声明 -->
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
</dependency>
```

### 核心类

- `TableEntityClassBuilder` — 基于 Byte Buddy 的动态实体类构建器
- `TableEntityMetadata` — 实体元数据定义（表名、列信息、注解等）
- `DynamicTableClassLoader` — 动态类加载器，注册运行时生成的类

## 使用实例

### 1. 动态构建 JPA 实体类

```java
// TableEntityClassBuilder.java
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;

class TableEntityClassBuilder {

    private final TableEntityMetadata metadata;
    private DynamicType.Builder<?> builder;

    public TableEntityClassBuilder(TableEntityMetadata metadata) {
        this.metadata = metadata;
        // 创建子类并添加 @Entity、@Table 注解
        this.builder = new ByteBuddy()
            .subclass(Object.class)
            .name(metadata.getClassName())
            .implement(Serializable.class)
            .annotateType(buildEntityAnnotations());
    }

    public Class<?> build() {
        this.buildColumns();
        Class<?> clazz = builder.make()
            .load(TableEntityClassBuilder.class.getClassLoader(),
                  ClassLoadingStrategy.Default.WRAPPER)
            .getLoaded();
        // 注册到动态类加载器
        DynamicTableClassLoader.getInstance().registerClass(clazz);
        return clazz;
    }
}
```

### 2. 动态添加字段和注解

```java
// TableEntityClassBuilder.java - 添加带 JPA 注解的字段
private void addColumnField(TableEntityMetadata.ColumnMeta columnMeta) {
    String fieldName = columnMeta.getFieldName();
    Class<?> fieldType = columnMeta.getType();

    // 构建字段注解 (@Column, @Id, @GeneratedValue 等)
    List<AnnotationDescription> fieldAnnotations = buildFieldAnnotations(columnMeta);

    // 定义私有字段并添加注解
    builder = builder.defineField(fieldName, fieldType, Visibility.PRIVATE)
        .annotateField(fieldAnnotations.toArray(new AnnotationDescription[0]));

    // 自动生成 getter/setter 方法
    String capitalizedFieldName = capitalize(fieldName);
    builder = builder
        .defineMethod("get" + capitalizedFieldName, fieldType, Visibility.PUBLIC)
        .intercept(FieldAccessor.ofField(fieldName))
        .defineMethod("set" + capitalizedFieldName, void.class, Visibility.PUBLIC)
        .withParameter(fieldType)
        .intercept(FieldAccessor.ofField(fieldName));
}
```

### 3. 构建实体类注解

```java
// TableEntityClassBuilder.java - 动态构建 @Entity + @Table 注解
private AnnotationDescription[] buildEntityAnnotations() {
    List<AnnotationDescription> annotations = new ArrayList<>();

    // @Entity
    annotations.add(AnnotationDescription.Builder.ofType(Entity.class).build());

    // @Table(name = "xxx")
    if (metadata.getTable() != null) {
        AnnotationDescription.Builder tableBuilder =
            AnnotationDescription.Builder.ofType(Table.class);
        tableBuilder = tableBuilder.define("name", metadata.getTable().getName());
        annotations.add(tableBuilder.build());
    }

    // @Comment
    if (StringUtils.hasText(metadata.getTable().getComment())) {
        annotations.add(AnnotationDescription.Builder.ofType(Comment.class)
            .define("value", metadata.getTable().getComment()).build());
    }

    return annotations.toArray(new AnnotationDescription[0]);
}
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter-data-fast | `TableEntityClassBuilder` 运行时动态生成 JPA 实体类 |
