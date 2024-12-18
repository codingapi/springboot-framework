package com.codingapi.springboot.authorization.enhancer;

import java.util.HashMap;
import java.util.Map;

public class TableColumnAliasContext {

    private final Map<String,TableColumnAlias> tableColumnAliasMap;

    public TableColumnAliasContext() {
        this.tableColumnAliasMap = new HashMap<>();
    }

    public TableColumnAlias add(String aliasName,String columnName){
        TableColumnAlias tableColumnAlias = new TableColumnAlias(aliasName,columnName);
        tableColumnAliasMap.put(tableColumnAlias.getKey(),tableColumnAlias);
        return tableColumnAlias;
    }


    public void print(){
        for(String key:tableColumnAliasMap.keySet()){
            TableColumnAlias tableColumnAlias = tableColumnAliasMap.get(key);
            System.out.println(tableColumnAlias.getKey());
        }
    }
}
