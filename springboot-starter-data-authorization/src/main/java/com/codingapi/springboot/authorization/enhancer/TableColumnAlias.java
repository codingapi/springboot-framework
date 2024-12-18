package com.codingapi.springboot.authorization.enhancer;

import java.util.HashMap;
import java.util.Map;

public class TableColumnAlias {

    private final String aliasName;
    private final String columnName;

    private Map<String,TableColumnAlias> children;

    public String getKey(){
        return aliasName+"."+columnName;
    }

    public TableColumnAlias(String aliasName, String columnName) {
        this.aliasName = aliasName;
        this.columnName = columnName;
        this.children = new HashMap<>();
    }

    public TableColumnAlias add(String aliasName,String columnName){
        TableColumnAlias tableColumnAlias = new TableColumnAlias(aliasName,columnName);
        children.put(tableColumnAlias.getKey(),tableColumnAlias);
        return tableColumnAlias;
    }
}
