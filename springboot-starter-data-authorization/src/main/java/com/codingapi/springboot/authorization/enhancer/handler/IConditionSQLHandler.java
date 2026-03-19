package com.codingapi.springboot.authorization.enhancer.handler;

import com.codingapi.springboot.authorization.condition.IConditionSQL;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;

public interface IConditionSQLHandler {

    boolean support(IConditionSQL conditionSQL);

    void handler(IConditionSQL conditionSQL, PlainSelect plainSelect, Table table, Expression where);


}