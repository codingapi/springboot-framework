package com.codingapi.springboot.authorization.enhancer.handler;

import com.codingapi.springboot.authorization.condition.IConditionSQL;
import lombok.Getter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.ArrayList;
import java.util.List;

public class ConditionSQLHandlerContext {

    private final List<IConditionSQLHandler> handlers;

    @Getter
    private final static ConditionSQLHandlerContext instance = new ConditionSQLHandlerContext();

    private ConditionSQLHandlerContext() {
        this.handlers = new ArrayList<>();
        this.registerHandlers();
    }


    private void registerHandlers(){
        this.handlers.add(new WhereConditionSQLHandler());
        this.handlers.add(new JoinConditionSQLHandler());
    }


    public void handler(IConditionSQL dynamicSQL, PlainSelect plainSelect, Table table, Expression where) {
        for (IConditionSQLHandler handler:handlers){
            if (handler.support(dynamicSQL)){
                handler.handler(dynamicSQL,plainSelect, table, where);
            }
        }
    }
}