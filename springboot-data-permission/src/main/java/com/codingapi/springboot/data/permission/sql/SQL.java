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

    private String sql;
    private char[] sqlChars;

    private final Map<String,Integer> parameterKeys = new HashMap<>();

    private final Map<String,Integer> parameters = new HashMap<>();

    @Getter
    private final List<Integer> removeIndexes = new ArrayList<>();

    private static final Pattern INSERT_PATTERN = Pattern.compile("(\\([\\sa-zA-Z0-9_,|?]*\\s*[?]+\\s*[\\sa-zA-Z0-9_,|?]*\\s*\\))");

    private static final Pattern INSERT_PATTERN_KEY = Pattern.compile("(\\([\\sa-zA-Z0-9_]+[,][\\sa-zA-Z0-9_,]*\\))");

    private static final Pattern EQUAL_PATTERN = Pattern.compile("([a-zA-Z0-9_\\.]*\\s*[=]\\s*[?])");

    private static final Pattern CURD_INSERT_PATTERN = Pattern.compile("(^\\s*(insert).*)",Pattern.CASE_INSENSITIVE);

    private static final Pattern CURD_DELETE_PATTERN = Pattern.compile("(^\\s*(delete).*)",Pattern.CASE_INSENSITIVE);

    private static final Pattern CURD_UPDATE_PATTERN = Pattern.compile("(^\\s*(update).*)",Pattern.CASE_INSENSITIVE);

    private static final Pattern CURD_SELECT_PATTERN = Pattern.compile("(^\\s*(select).*)",Pattern.CASE_INSENSITIVE);

    private SQLType sqlType;

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
                sql = sql.replaceFirst("\\s*" + key + "\\s*[,]?", "");
                sql = sql.replaceFirst("\\?\\s*[,]?", "");
            }
            sql = sql.replaceFirst("\\s*" + key + "\\s*=\\s*\\?\\s*[,]?", "");
            sql = sql.trim();
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
        log.info("parameterKeys:{}",parameterKeys);
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
            parameters.put(key,parameterKeys.get(key));
        }
    }

    public int getIndex(String parameterKey){
        Integer value = parameters.get(parameterKey);
        if(value==null){
            return 0;
        }
        return value;
    }

    public String getSql() {
        return sql.toString();
    }

    public char[] getSqlChars() {
        return sqlChars;
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
