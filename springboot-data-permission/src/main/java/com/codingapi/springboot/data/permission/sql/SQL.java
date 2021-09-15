package com.codingapi.springboot.data.permission.sql;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class SQL {

    enum SQLType{
        INSERT,DELETE,UPDATE,SELECT
    }

    //jdbc sql type
    private SQLType sqlType;

    // jdbc sql
    @Getter
    private String sql;

    // jdbc sql to char arrays.
    @Getter
    private char[] sqlChars;

    //jdbc sql parameter key and values,for example insert into t_demo(id,name) values(?,?), keys:id,name value:1,2
    private final Map<String,Integer> parameterKeys = new HashMap<>();

    // init jdbc sql parameterKeys
    private final Map<String,Integer> initParameters = new HashMap<>();

    // remove keys index values
    @Getter
    private final List<Integer> removeIndexes = new ArrayList<>();

    //Pattern
    private static final Pattern INSERT_PATTERN = Pattern.compile("(\\([\\sa-zA-Z0-9_,|?]*\\s*[?]+\\s*[\\sa-zA-Z0-9_,|?]*\\s*\\))");

    private static final Pattern INSERT_PATTERN_KEY = Pattern.compile("(\\([\\sa-zA-Z0-9_]+[,][\\sa-zA-Z0-9_,]*\\))");

    private static final Pattern EQUAL_PATTERN = Pattern.compile("([a-zA-Z0-9_\\.]*\\s*[=]\\s*[?])");

    private static final Pattern CURD_INSERT_PATTERN = Pattern.compile("(^\\s*(insert).*)",Pattern.CASE_INSENSITIVE);

    private static final Pattern CURD_DELETE_PATTERN = Pattern.compile("(^\\s*(delete).*)",Pattern.CASE_INSENSITIVE);

    private static final Pattern CURD_UPDATE_PATTERN = Pattern.compile("(^\\s*(update).*)",Pattern.CASE_INSENSITIVE);

    private static final Pattern CURD_SELECT_PATTERN = Pattern.compile("(^\\s*(select).*)",Pattern.CASE_INSENSITIVE);


    public SQL(String sql) {
        this.sql = sql;
        this.sqlChars = sql.toCharArray();
        this.initParameterKeys();
        this.copyParameter();
    }

    public boolean hasRemoveIndex(int index) {
        return removeIndexes.contains(index);
    }

    public void deleteKey(String key){
        Integer value = parameterKeys.get(key);
        if(value!=null){
            if (sqlType == SQLType.INSERT) {
                //for example : insert into t_demo(id,name) values(?,?),delete id will get result: insert into t_demo(name) values(?)
                sql = sql.replaceFirst(String.format("\\s*%s\\s*[,]?",key), "");
                sql = sql.replaceFirst("\\?\\s*[,]?", "");
            }
            //for example : update t_demo set name = ?, age = ? where id = ? , delete name will get result:update t_demo set age = ? where id = ?
            sql = sql.replaceFirst(String.format("\\s*%s\\s*=\\s*\\?\\s*[,]?",key), "");
            sql = sql.trim();
            //when endsWith where will append 1=1
            if(sql.toUpperCase().endsWith("WHERE")){
                sql = sql+" 1=1";
            }
            removeIndexes.add(value);
            parameterKeys.remove(key);
            this.sqlChars = sql.toCharArray();
        }
    }

    private void initParameterKeys(){
        if(match(CURD_INSERT_PATTERN)){
            sqlType = SQLType.INSERT;
            String[] values = split(INSERT_PATTERN);
            if(values!=null){
                String[] keys = split(INSERT_PATTERN_KEY);
                if(keys!=null) {
                    int index = 0;
                    for (int i = 0; i < values.length; i++) {
                        String value = values[i].trim();
                        if("?".equals(value)){
                            parameterKeys.put(keys[i].trim(),++index);
                        }
                    }
                }
            }
        }else {
            if(match(CURD_UPDATE_PATTERN)){
                sqlType = SQLType.UPDATE;
            }
            if(match(CURD_DELETE_PATTERN)){
                sqlType = SQLType.DELETE;
            }
            if(match(CURD_SELECT_PATTERN)){
                sqlType = SQLType.SELECT;
            }
            if(sqlType!=null) {
                List<String> values = find(EQUAL_PATTERN);
                if (values.size() > 0) {
                    for (int i = 0; i < values.size(); i++) {
                        String value = values.get(i);
                        String[] kvs = value.split("=");
                        parameterKeys.put(kvs[0].trim(), i+1);
                    }
                }
            }
        }
        log.debug("parameterKeys:{}",parameterKeys);
    }

    public int getSqlParameterCount(){
        int count = 0;
        for(char c:sqlChars){
            if(c=='?'){
                count++;
            }
        }
        return count;
    }

    private void copyParameter(){
        for(String key:parameterKeys.keySet()){
            initParameters.put(key,parameterKeys.get(key));
        }
    }

    public int getIndex(String parameterKey){
        Integer value = initParameters.get(parameterKey);
        if(value==null){
            return 0;
        }
        return value;
    }


    public String[] split(Pattern pattern){
        Matcher matcher =  pattern.matcher(sql);
        if (matcher.find()){
            String values =  matcher.group(0);
            if(values!=null){
                values = values.trim();
                values = values.substring(1,values.length()-1);
                return values.split(",");
            }
        }
        return null;
    }


    private List<String> find(Pattern pattern){
        Matcher matcher =  pattern.matcher(sql);
        List<String> list = new ArrayList<>();
        while (matcher.find()){
            int count = matcher.groupCount();
            for(int i=0;i<count;i++){
                list.add(matcher.group(i).trim());
            }
        }
        return list;
    }

    private boolean match(Pattern pattern){
        Matcher matcher =  pattern.matcher(sql);
        return matcher.find();
    }

    @Override
    public String toString() {
        return getSql();
    }
}
