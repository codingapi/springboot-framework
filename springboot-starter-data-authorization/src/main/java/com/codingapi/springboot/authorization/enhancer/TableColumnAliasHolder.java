package com.codingapi.springboot.authorization.enhancer;

import lombok.Getter;
import net.sf.jsqlparser.expression.Alias;
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
            parent = fromItem.getAlias().getName();
            this.searchSubSelect(parent, subPlainSelect);
        }

        // 处理JOIN或关联子查询
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() instanceof Select) {
                    FromItem currentItem = join.getRightItem();
                    PlainSelect subPlainSelect = ((Select) currentItem).getPlainSelect();
                    this.appendColumnAlias(parent, null, plainSelect.getSelectItems());
                    parent = currentItem.getAlias().getName();
                    this.searchSubSelect(parent, subPlainSelect);
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
