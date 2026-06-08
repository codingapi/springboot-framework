---
name: meta-table-annotation
description: 通过 @MetaTable/@MetaColumn/@MetaRelation 注解声明表的元数据描述，驱动动态实体生成和元数据查询
status: 已实现
scope: 后端
source: 项目自有
last_commit: a59736ac
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/annotation/MetaTable.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/annotation/MetaColumn.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/annotation/MetaRelation.java
---

## 解决什么问题

在 fast-repository 模块的动态实体生成场景中（如 Groovy 脚本定义的数据表、运行时创建的临时查询实体），需要在没有 JPA Entity 类的情况下描述一张数据库表的结构。传统的做法是手动构建 `TableEntityMetadata` 对象并逐个添加列信息，代码冗长且容易出错：

```java
// 手动构建元数据 — 繁琐、易错
TableEntityMetadata metadata = new TableEntityMetadata("com.example.DynamicUser");
metadata.setTable("t_user", "用户表");
metadata.addPrimaryKeyColumn(Long.class, "id", "id", GenerationType.IDENTITY, ...);
metadata.addColumn(String.class, "name", "name", "姓名", false, false, true, ...);
```

`@MetaTable` / `@MetaColumn` / `@MetaRelation` 注解体系提供了一种**声明式**的元数据描述方式，将表结构信息直接标注在 POJO 类上，由框架自动扫描并转换为 `TableEntityMetadata`，大幅简化了动态实体的定义流程。

## 如何使用

### 注解说明

#### @MetaTable（类级别）

标注在类上，声明该类对应的数据库表信息。

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | String | 是 | 数据库表名称 |
| `desc` | String | 是 | 表的中文说明/备注 |

#### @MetaColumn（字段级别）

标注在字段上，声明该字段对应的数据库列信息。

| 属性 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `name` | String | 是 | — | 数据库列名称 |
| `desc` | String | 是 | — | 列的中文说明/备注 |
| `primaryKey` | boolean | 否 | `false` | 是否为主键 |
| `type` | ColumnType | 否 | `ColumnType.String` | 字段数据类型 |
| `format` | String | 否 | `""` | 格式化模板（如日期格式） |
| `dependent` | MetaRelation | 否 | 空关联 | 外键依赖关系 |

#### @MetaRelation（嵌套在 @MetaColumn 中）

声明字段的外键关联关系，作为 `@MetaColumn.dependent` 的值使用。

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `tableName` | String | 是 | 关联的目标表名 |
| `columnName` | String | 是 | 关联的目标列名 |

#### ColumnType 枚举

| 值 | 说明 |
|----|------|
| `Number` | 整数 |
| `Float` | 浮点数 |
| `String` | 字符串 |
| `Date` | 日期 |
| `File` | 文件 |
| `Boolean` | 布尔 |
| `Bytes` | 字节数组 |
| `JSON` | JSON 对象 |
| `Any` | 任意类型 |

### 命名规范

- 注解类统一放在 `com.codingapi.springboot.framework.annotation` 包下
- `@MetaTable` 只能用在类上（`@Target(ElementType.TYPE)`）
- `@MetaColumn` 只能用在字段上（`@Target(ElementType.FIELD)`）
- 所有注解均为 `@Retention(RetentionPolicy.RUNTIME)`，支持运行时反射读取

## 使用实例

```java
// ✅ 正确 — 完整的元数据注解声明
@MetaTable(name = "t_leave_request", desc = "请假申请表")
public class LeaveRequestMeta {

    @MetaColumn(name = "id", desc = "主键ID", primaryKey = true, type = ColumnType.Number)
    private Long id;

    @MetaColumn(name = "user_id", desc = "申请人ID", type = ColumnType.Number,
                dependent = @MetaRelation(tableName = "t_user", columnName = "id"))
    private Long userId;

    @MetaColumn(name = "start_date", desc = "开始日期", type = ColumnType.Date, format = "yyyy-MM-dd")
    private String startDate;

    @MetaColumn(name = "end_date", desc = "结束日期", type = ColumnType.Date, format = "yyyy-MM-dd")
    private String endDate;

    @MetaColumn(name = "reason", desc = "请假原因", type = ColumnType.String)
    private String reason;

    @MetaColumn(name = "approved", desc = "是否批准", type = ColumnType.Boolean)
    private Boolean approved;
}

// ✅ 正确 — 最简声明（使用默认值）
@MetaTable(name = "t_dict", desc = "数据字典")
public class DictMeta {

    @MetaColumn(name = "id", desc = "主键", primaryKey = true)
    private Long id;

    @MetaColumn(name = "code", desc = "编码")
    private String code;

    @MetaColumn(name = "label", desc = "显示文本")
    private String label;
}

// ❌ 错误 — 缺少 @MetaTable 注解
public class BadMeta {
    @MetaColumn(name = "id", desc = "主键", primaryKey = true)
    private Long id;
}

// ❌ 错误 — @MetaColumn 缺少必填属性 name 和 desc
@MetaTable(name = "t_test", desc = "测试表")
public class IncompleteMeta {
    @MetaColumn(desc = "主键")   // 缺少 name！编译报错
    private Long id;
}

// ❌ 错误 — 不要混用 JPA 注解代替 Meta 注解
@Entity  // 这是 JPA 注解，不是元数据注解
@Table(name = "t_user")
public class UserMeta {
    @Id
    @Column(name = "id")
    private Long id;
}
```
