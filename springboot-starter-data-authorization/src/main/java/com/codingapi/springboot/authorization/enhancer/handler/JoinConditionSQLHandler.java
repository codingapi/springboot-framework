package com.codingapi.springboot.authorization.enhancer.handler;

import com.codingapi.springboot.authorization.condition.IConditionSQL;
import com.codingapi.springboot.authorization.condition.JoinConditionSQL;
import lombok.SneakyThrows;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.ArrayList;
import java.util.Collection;

public class JoinConditionSQLHandler implements IConditionSQLHandler {

    @Override
    public boolean support(IConditionSQL conditionSQL) {
        return conditionSQL instanceof JoinConditionSQL;
    }

    @SneakyThrows
    @Override
    public void handler(IConditionSQL conditionSQL, PlainSelect plainSelect, Table table, Expression where) {
        JoinConditionSQL joinConditionSQL = (JoinConditionSQL) conditionSQL;

        if(plainSelect.getJoins()==null){
            plainSelect.setJoins(new ArrayList<>());
        }

        Join join = new Join();
        join.setFromItem(joinConditionSQL.toJoinTable());

        if(joinConditionSQL.getType()== JoinConditionSQL.Type.INNER){
            join.setInner(true);
        }

        if(joinConditionSQL.getType()== JoinConditionSQL.Type.RIGHT){
            join.setRight(true);
        }

        if(joinConditionSQL.getType()== JoinConditionSQL.Type.LEFT){
            join.setLeft(true);
        }

        Collection<Expression> expressions = new ArrayList<>();
        Expression expression = CCJSqlParserUtil.parseCondExpression(joinConditionSQL.getOnCondition());
        expressions.add(expression);
        join.setOnExpressions(expressions);
        plainSelect.addJoins(join);
    }
}
