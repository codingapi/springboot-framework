package com.codingapi.springboot.permission.sql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcSql {

    private final Map<Integer,JdbcValue> parameterValues = new HashMap<>();
    private final SQL sql;

    public interface Clear{
        void clear() throws SQLException;
    }

    public interface CallBack{
       void run(int parameterIndex) throws SQLException;
    }

    public JdbcSql(SQL sql) {
        this.sql = sql;
    }

    public void clearParameters(Clear clear) throws SQLException{
        parameterValues.clear();
        if(clear!=null){
            clear.clear();
        }
    }

    public void put(String sqlKey, Object value,CallBack callBack) throws SQLException {
       int index = sql.getIndex(sqlKey);
       if(index!=0){
           this.put(index,value,callBack);
       }
    }


    public void put(int parameterIndex, Object value,CallBack callBack) throws SQLException {
        //skip remove index
        if(sql.hasRemoveIndex(parameterIndex)){
            return;
        }

        // rebuild index
        List<Integer> removeIndexes =  sql.getRemoveIndexes();
        for (int index:removeIndexes){
            if(parameterIndex>index){
                parameterIndex--;
            }
        }

        parameterValues.put(parameterIndex,new JdbcValue(value));
        if(callBack!=null){
            callBack.run(parameterIndex);
        }
    }

    public String getExecuteSql(){
        int index = 1;
        StringBuilder builder = new StringBuilder();
        for (char character :sql.getSqlChars()){
            if(character=='?'){
                JdbcValue value = parameterValues.get(index++);
                builder.append(value.strVal());
            }else{
                builder.append(character);
            }
        }
        return builder.toString();
    }

    public String getSql() {
        return sql.getSql();
    }


}
