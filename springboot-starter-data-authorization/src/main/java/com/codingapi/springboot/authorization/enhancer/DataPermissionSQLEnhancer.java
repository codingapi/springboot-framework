package com.codingapi.springboot.authorization.enhancer;


import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.handler.RowHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据权限 SQL 增强器
 */
public class DataPermissionSQLEnhancer {

    private final String sql;
    private final RowHandler rowHandler;
    private final TableColumnAliasHolder tableColumnAliasHolder;
    private final Statement statement;


    // 构造函数
    public DataPermissionSQLEnhancer(String sql, RowHandler rowHandler) throws SQLException {
        try {
            // 如何sql中存在? 则在?后面添加空格
            this.sql = sql.replaceAll("\\?", " ? ");
            this.rowHandler = rowHandler;
            this.statement = CCJSqlParserUtil.parse(this.sql);
            this.tableColumnAliasHolder = new TableColumnAliasHolder(statement);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    // 获取增强后的SQL
    public String getNewSQL() throws SQLException {
        try {
            if (statement instanceof Select) {
                tableColumnAliasHolder.holderAlias();
                Select select = (Select) statement;
                this.deepMatch(select);
                return statement.toString();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return sql;
    }


    private void deepMatch(Select select) throws Exception {
        if (select instanceof PlainSelect) {
            PlainSelect plainSelect = select.getPlainSelect();
            this.enhanceDataPermissionInSelect(plainSelect);
        }
        if (select instanceof SetOperationList) {
            SetOperationList setOperationList = select.getSetOperationList();
            List<Select> selectList = setOperationList.getSelects();
            for (Select selectItem : selectList) {
                this.deepMatch(selectItem.getPlainSelect());
            }
        }
    }

    public TableColumnAliasContext getTableAlias() {
        return tableColumnAliasHolder.getAliasContext();
    }


    // 增强 SELECT 语句
    private void enhanceDataPermissionInSelect(PlainSelect plainSelect) throws Exception {
        FromItem fromItem = plainSelect.getFromItem();

        // FROM 项是表
        if (fromItem instanceof Table) {
            Table table = (Table) fromItem;
            this.injectDataPermissionCondition(plainSelect, table, plainSelect.getWhere());
        }

        // FROM是子查询
        if (fromItem instanceof Select) {
            PlainSelect subPlainSelect = ((Select) fromItem).getPlainSelect();
            this.enhanceDataPermissionInSelect(subPlainSelect);
        }

        // 处理JOIN或关联子查询
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() instanceof Select) {
                    PlainSelect subPlainSelect = ((Select) join.getRightItem()).getPlainSelect();
                    this.enhanceDataPermissionInSelect(subPlainSelect);
                }
                if (join.getRightItem() instanceof Table) {
                    injectDataPermissionCondition(plainSelect, (Table) join.getRightItem(), plainSelect.getWhere());
                }
            }
        }

        Expression expression =  plainSelect.getWhere();
        this.handlerSubSelect(expression);
    }

    private void handlerSubSelect(Expression expression) throws Exception {
        if(expression!=null){
            if(expression instanceof AndExpression){
                AndExpression andExpression = (AndExpression) expression;
                Expression leftExpression =  andExpression.getLeftExpression();
                Expression rightExpression =  andExpression.getRightExpression();

                this.handlerSubSelect(leftExpression);
                this.handlerSubSelect(rightExpression);

            }
            if(expression instanceof OrExpression){
                OrExpression orExpression = (OrExpression) expression;
                Expression leftExpression =  orExpression.getLeftExpression();
                Expression rightExpression =  orExpression.getRightExpression();

                this.handlerSubSelect(leftExpression);
                this.handlerSubSelect(rightExpression);
            }

            if(expression instanceof InExpression){
                InExpression inExpression = (InExpression) expression;
                Expression leftExpression =  inExpression.getLeftExpression();
                Expression rightExpression =  inExpression.getRightExpression();

                this.handlerSubSelect(leftExpression);
                this.handlerSubSelect(rightExpression);
            }

            if(expression instanceof ParenthesedSelect){
                ParenthesedSelect  parenthesedSelect = (ParenthesedSelect) expression;
                this.enhanceDataPermissionInSelect(parenthesedSelect.getPlainSelect());
            }

            if(expression instanceof PlainSelect){
                this.enhanceDataPermissionInSelect((PlainSelect) expression);
            }
        }
    }


    // 注入数据权限条件
    private void injectDataPermissionCondition(PlainSelect plainSelect, Table table, Expression where) throws Exception {
        String tableName = table.getName();
        String aliaName = table.getAlias() != null ? table.getAlias().getName() : tableName;
        Condition condition = rowHandler.handler(plainSelect.toString(), tableName, aliaName);
        if (condition != null) {
            // 添加自定义条件
            Expression customExpression = CCJSqlParserUtil.parseCondExpression(condition.getCondition());
            if (where != null) {
                plainSelect.setWhere(new AndExpression(customExpression, where));
            } else {
                plainSelect.setWhere(customExpression);
            }
        }
    }

}
