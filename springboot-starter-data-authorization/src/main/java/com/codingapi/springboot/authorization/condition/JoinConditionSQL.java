package com.codingapi.springboot.authorization.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import org.springframework.util.StringUtils;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinConditionSQL implements IConditionSQL {

    private Type type;
    private String tableName;
    private String tableAlias;
    private String onCondition;



    public static enum Type{
        INNER,
        LEFT,
        RIGHT
    }


    public Table toJoinTable(){
        if(StringUtils.hasText(tableAlias)){
            Table table = new Table(this.tableName);
            table.setAlias(new Alias(tableAlias));
            return table;
        }else {
            return new Table(this.tableName);
        }
    }

}
