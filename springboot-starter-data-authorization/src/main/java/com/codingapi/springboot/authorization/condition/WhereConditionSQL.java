package com.codingapi.springboot.authorization.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhereConditionSQL implements IConditionSQL {

    private String condition;

    public WhereConditionSQL(String condition,Object... args){
        this.condition = String.format(condition, args);
    }

}
