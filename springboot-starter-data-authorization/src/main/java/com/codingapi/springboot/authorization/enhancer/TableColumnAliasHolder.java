package com.codingapi.springboot.authorization.enhancer;

import lombok.Getter;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;

/**
 * 表列别名持有者
 */
public class TableColumnAliasHolder {

    private final Statement statement;
    @Getter
    private final TableColumnAliasContext aliasContext;

    public TableColumnAliasHolder(Statement statement) {
        this.statement = statement;
        this.aliasContext = new TableColumnAliasContext();
    }


    /**
     * 获取表列别名
     */
    public void holderAlias() {
        Select select = (Select) statement;
        this.deepSearch(select);
    }


    private void deepSearch(Select select) {
        if (select instanceof PlainSelect) {
            PlainSelect plainSelect = select.getPlainSelect();
            this.searchSubSelect(null, plainSelect);
            aliasContext.columnKeyToMap();
        } else if (select instanceof SetOperationList) {
            SetOperationList setOperationList = select.getSetOperationList();
            List<Select> selectList = setOperationList.getSelects();
            for (Select selectItem : selectList) {
                this.deepSearch(selectItem);
            }
        }
    }


    // 增强 SELECT 语句
    private void searchSubSelect(String parent, PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();

        // FROM 项是表
        if (fromItem instanceof Table) {
            this.appendTableAlias(fromItem);
            Table table = (Table) fromItem;
            this.appendColumnAlias(parent, table.getName(), plainSelect.getSelectItems());
        }


        // FROM是子查询
        if (fromItem instanceof Select) {
            PlainSelect subPlainSelect = ((Select) fromItem).getPlainSelect();
            this.appendColumnAlias(parent, null, plainSelect.getSelectItems());
            parent = fromItem.getAlias()!=null?fromItem.getAlias().getName():null;
            this.searchSubSelect(parent, subPlainSelect);
            // 递归完成后子查询内的表/派生表均已注册，可构建其投影映射
            this.buildProjection(parent, subPlainSelect);
        }

        // 处理JOIN或关联子查询
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() instanceof Select) {
                    FromItem currentItem = join.getRightItem();
                    PlainSelect subPlainSelect = ((Select) currentItem).getPlainSelect();
                    this.appendColumnAlias(parent, null, plainSelect.getSelectItems());
                    parent = currentItem.getAlias()!=null?currentItem.getAlias().getName():null;
                    this.searchSubSelect(parent, subPlainSelect);
                    this.buildProjection(parent, subPlainSelect);
                }
                if (join.getRightItem() instanceof Table) {
                    FromItem currentItem = join.getRightItem();
                    this.appendTableAlias(currentItem);
                    Table table = (Table) currentItem;
                    this.appendColumnAlias(parent, table.getName(), plainSelect.getSelectItems());
                }
            }
        }
    }


    /**
     * 构建派生表（子查询）输出列到物理表字段的投影映射（Issue #212）。
     * 需在递归处理子查询之后调用，保证其引用的表/派生表均已注册。
     *
     * @param alias 派生表别名
     * @param subPlainSelect 子查询
     */
    private void buildProjection(String alias, PlainSelect subPlainSelect) {
        if (alias == null || subPlainSelect == null) {
            return;
        }
        DerivedTableProjection projection = new DerivedTableProjection();
        List<SelectItem<?>> selectItems = subPlainSelect.getSelectItems();
        if (selectItems != null) {
            for (SelectItem<?> selectItem : selectItems) {
                Expression expression = selectItem.getExpression();
                String aliasName = selectItem.getAlias() != null ? selectItem.getAlias().getName() : null;
                if (expression instanceof AllTableColumns allTableColumns) {
                    // 注意：AllTableColumns(emp.*) 是 AllColumns(*) 的子类，必须先判断
                    projection.addStarTable(this.starTableOf(allTableColumns.getTable()));
                } else if (expression instanceof AllColumns) {
                    // SELECT * ：仅当子查询只有一个来源时可归因
                    FromItem singleSource = this.singleSource(subPlainSelect);
                    projection.addStarTable(singleSource != null ? this.starTableOf(singleSource) : null);
                } else if (expression instanceof Column column) {
                    this.putProjectionColumn(projection, subPlainSelect, column, aliasName);
                } else if (aliasName != null) {
                    // 表达式/函数 AS 别名：非物理列，不参与归因
                    projection.markExpression(aliasName.toLowerCase());
                }
            }
        }
        aliasContext.addDerivedTable(alias, projection);
    }

    private void putProjectionColumn(DerivedTableProjection projection, PlainSelect subPlainSelect,
                                      Column column, String aliasName) {
        String tableRef = column.getTable() != null ? column.getTable().getName() : "";
        if (tableRef.isEmpty()) {
            // 裸列：仅当子查询只有一个来源时可归因
            FromItem singleSource = this.singleSource(subPlainSelect);
            tableRef = singleSource != null ? this.sourceName(singleSource) : null;
        }
        String[] reference = this.resolveColumnReference(tableRef, column.getColumnName());
        if (reference != null) {
            String outName = aliasName != null ? aliasName : column.getColumnName();
            projection.putExplicit(outName.toLowerCase(), reference[0], reference[1]);
        } else if (aliasName != null) {
            projection.markExpression(aliasName.toLowerCase());
        }
    }

    /**
     * 将 tableRef.column 解析到物理表字段（支持表别名、物理表名、派生表链式解析）
     */
    private String[] resolveColumnReference(String tableRef, String columnName) {
        if (tableRef == null || tableRef.isEmpty()) {
            return null;
        }
        String physicalTable = aliasContext.resolvePhysicalTable(tableRef);
        if (physicalTable != null) {
            return new String[]{physicalTable, columnName};
        }
        DerivedTableProjection derived = aliasContext.getDerivedTable(tableRef);
        if (derived != null) {
            return derived.find(columnName.toLowerCase(), columnName);
        }
        return null;
    }

    /**
     * 获取 * 展开来源的物理表名（Table 直接来源或派生表链式来源），歧义/未知返回 null
     */
    private String starTableOf(FromItem fromItem) {
        if (fromItem == null) {
            return null;
        }
        String refName = this.sourceName(fromItem);
        if (refName == null) {
            return null;
        }
        String physicalTable = aliasContext.resolvePhysicalTable(refName);
        if (physicalTable != null) {
            return physicalTable;
        }
        DerivedTableProjection derived = aliasContext.getDerivedTable(refName);
        if (derived != null && !derived.isStarAmbiguous()) {
            return derived.getStarTable();
        }
        return null;
    }

    /**
     * 子查询的唯一来源（无 JOIN 时的 FROM 项）
     */
    private FromItem singleSource(PlainSelect plainSelect) {
        if (plainSelect.getJoins() != null && !plainSelect.getJoins().isEmpty()) {
            return null;
        }
        return plainSelect.getFromItem();
    }

    /**
     * 来源的引用名（优先别名，其次表名；子查询取别名）
     */
    private String sourceName(FromItem fromItem) {
        if (fromItem instanceof Table table) {
            if (table.getAlias() != null) {
                return table.getAlias().getName();
            }
            return table.getName();
        }
        if (fromItem.getAlias() != null) {
            return fromItem.getAlias().getName();
        }
        return null;
    }


    /**
     * 添加表别名
     *
     * @param fromItem 表
     */
    private void appendTableAlias(FromItem fromItem) {
        Table table = (Table) fromItem;
        Alias alias = table.getAlias();
        String aliasName = alias != null ? alias.getName() : table.getName();
        aliasContext.addTable(aliasName, table.getName());
    }


    /**
     * 添加列别名
     *
     * @param parent      父表别名
     * @param selectItems 列
     */
    private void appendColumnAlias(String parent, String tableName, List<SelectItem<?>> selectItems) {
        if (selectItems != null) {
            for (SelectItem<?> selectItem : selectItems) {
                if (selectItem.getExpression() instanceof Column) {
                    Column column = (Column) selectItem.getExpression();
                    if (column.getTable() != null) {
                        tableName = column.getTable().getName();
                    }
                    String columnName = column.getColumnName();
                    Alias columnAlias = selectItem.getAlias();
                    String aliasName = columnAlias != null ? selectItem.getAlias().getName() : columnName;
                    aliasContext.addColumn(parent, tableName, columnName, aliasName);
                }
            }
        }
    }

}
