package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.condition.IConditionSQL;
import com.codingapi.springboot.authorization.condition.WhereConditionSQL;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *  查询条件
 */
@Getter
public class Condition {

    private final List<IConditionSQL> conditionList;

    public Condition() {
        this.conditionList = new ArrayList<>();
    }

    public Condition(String condition) {
        this.conditionList = new ArrayList<>();
        this.addConditionSQL(new WhereConditionSQL(condition));
    }

    public void addConditionSQL(IConditionSQL dynamicSQL){
        this.conditionList.add(dynamicSQL);
    }

    public static Condition customCondition(String condition) {
        Condition dynamicSQLContent = new Condition();
        dynamicSQLContent.addConditionSQL(new WhereConditionSQL(condition));
        return dynamicSQLContent;
    }

    public static Condition formatCondition(String condition, Object... args) {
        return customCondition(String.format(condition, args));
    }

    public static Condition emptyCondition() {
        return null;
    }

    public static Condition defaultCondition() {
        return customCondition("1=1");
    }

}
