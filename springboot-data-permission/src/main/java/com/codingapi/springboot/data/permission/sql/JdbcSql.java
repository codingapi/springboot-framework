package com.codingapi.springboot.data.permission.sql;

import java.util.HashMap;
import java.util.Map;

public class JdbcSql {

    private Map<Integer,JdbcValue> parameterValues = new HashMap<>();
    private String sql;

    public JdbcSql(String sql) {
        this.sql = sql;
    }

    public void put(int parameterIndex, Object value) {
        parameterValues.put(parameterIndex,new JdbcValue(value));
    }

    public String getExecuteSql(){
        int size = parameterValues.size();
        char[] chars = sql.toCharArray();
        int index = 1;
        StringBuilder builder = new StringBuilder();
        for (char character :chars){
            if(character=='?'){
                JdbcValue value = parameterValues.get(index++);
                builder.append(value.strVal());
            }else{
                builder.append(character);
            }
        }
        return builder.toString();
    }
}
