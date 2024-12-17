package com.codingapi.springboot.authorization.analyzer;

import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.handler.RowHandler;
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

public class SelectSQLAnalyzer {

    private final String sql;
    private final RowHandler rowHandler;

    public SelectSQLAnalyzer(String sql, RowHandler rowHandler) {
        // 如何sql中存在? 则在?后面添加空格
        this.sql = sql.replaceAll("\\?", " ? ");
        this.rowHandler = rowHandler;
    }

    public String getNewSQL() throws SQLException {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select) {
                Select select = (Select) statement;
                PlainSelect plainSelect = select.getPlainSelect();
                this.processFromItems(plainSelect);
                return statement.toString();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return sql;
    }

    private void processFromItems(PlainSelect plainSelect) throws Exception {
        this.addConditionToSubSelect(plainSelect);

        FromItem fromItem = plainSelect.getFromItem();

        // 处理主 FROM 项（如果是子查询）
        if (fromItem instanceof Select) {
            this.addConditionToSubSelect((Select) fromItem);
        }

        // 处理 JOIN 子查询
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() instanceof Select) {
                    this.addConditionToSubSelect((Select) join.getRightItem());
                }
            }
        }
    }


    private void addConditionToSubSelect(Select subSelect) throws Exception {
        PlainSelect selectBody = subSelect.getPlainSelect();
        if (selectBody != null) {
            // 获取 WHERE 子句
            Expression where = selectBody.getWhere();
            FromItem fromItem = selectBody.getFromItem();
            if (fromItem instanceof Table) {
                Table table = (Table) fromItem;
                String tableName = table.getName();
                String aliaName = table.getAlias() != null ? table.getAlias().getName() : tableName;
                Condition condition = rowHandler.handler(selectBody.toString(), tableName, aliaName);
                if (condition != null) {
                    // 添加自定义条件
                    Expression customExpression = CCJSqlParserUtil.parseCondExpression(condition.getCondition());
                    if (where != null) {
                        selectBody.setWhere(new AndExpression(customExpression, where));
                    } else {
                        selectBody.setWhere(customExpression);
                    }
                }
            }
        }
    }

}
