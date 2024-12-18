package com.codingapi.springboot.authorization.enhancer;

import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.handler.RowHandler;
import lombok.Getter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限 SQL 增强器
 */
public class DataPermissionSQLEnhancer {

    private final String sql;
    private final RowHandler rowHandler;

    @Getter
    private final Map<String, String> tableAlias;

    // 构造函数
    public DataPermissionSQLEnhancer(String sql, RowHandler rowHandler) {
        // 如何sql中存在? 则在?后面添加空格
        this.sql = sql.replaceAll("\\?", " ? ");
        this.rowHandler = rowHandler;
        this.tableAlias = new HashMap<>();
    }

    // 获取增强后的SQL
    public String getNewSQL() throws SQLException {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select) {
                Select select = (Select) statement;
                PlainSelect plainSelect = select.getPlainSelect();

                this.enhanceDataPermissionInSelect(plainSelect);
                return statement.toString();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return sql;
    }

    // 增强 SELECT 语句
    private void enhanceDataPermissionInSelect(PlainSelect plainSelect) throws Exception {
        this.applyDataPermissionToSubquery(plainSelect);

        FromItem fromItem = plainSelect.getFromItem();

        // 处理主 FROM 项（如果是子查询）
        if (fromItem instanceof Select) {
            this.applyDataPermissionToSubquery((Select) fromItem);
        }
        Expression where = plainSelect.getWhere();

        // 处理JOIN或关联子查询
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() instanceof Select) {
                    this.applyDataPermissionToSubquery((Select) join.getRightItem());
                }
                if(join.getRightItem() instanceof Table){
                    injectDataPermissionCondition(plainSelect, (Table) join.getRightItem(), where);
                }
            }
        }
    }

    // 注入数据权限条件
    private void injectDataPermissionCondition(PlainSelect plainSelect, Table table, Expression where) throws Exception {
        String tableName = table.getName();
        String aliaName = table.getAlias() != null ? table.getAlias().getName() : tableName;
        tableAlias.put(aliaName, tableName);
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

    // 处理子查询
    private void applyDataPermissionToSubquery(Select subSelect) throws Exception {
        PlainSelect selectBody = subSelect.getPlainSelect();
        if (selectBody != null) {
            // 获取 WHERE 子句
            Expression where = selectBody.getWhere();
            FromItem fromItem = selectBody.getFromItem();
            if (fromItem instanceof Table) {
                injectDataPermissionCondition(selectBody, (Table) fromItem, where);
            }
        }
    }
}
