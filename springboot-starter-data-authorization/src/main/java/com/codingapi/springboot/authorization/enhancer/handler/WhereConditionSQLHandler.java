package com.codingapi.springboot.authorization.enhancer.handler;

import com.codingapi.springboot.authorization.condition.IConditionSQL;
import com.codingapi.springboot.authorization.condition.WhereConditionSQL;
import lombok.SneakyThrows;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class WhereConditionSQLHandler implements IConditionSQLHandler {

    @Override
    public boolean support(IConditionSQL conditionSQL) {
        return conditionSQL instanceof WhereConditionSQL;
    }

    @SneakyThrows
    @Override
    public void handler(IConditionSQL conditionSQL, PlainSelect plainSelect, Table table, Expression where) {
        String whereSQL = ((WhereConditionSQL) conditionSQL).getCondition();
        // 添加自定义条件
        Expression customExpression = CCJSqlParserUtil.parseCondExpression(whereSQL);
        if (where != null) {
            plainSelect.setWhere(new AndExpression(customExpression, where));
        } else {
            plainSelect.setWhere(customExpression);
        }
    }
}
