package com.codingapi.springboot.fast.jpa.map;

import com.codingapi.springboot.framework.utils.RandomGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class QueryColumns {
    private final String key;
    private final List<String> columns;

    QueryColumns() {
        this.key = RandomGenerator.randomString(8);
        this.columns = new ArrayList<>();
    }

    public QueryColumns addColumn(String column) {
        this.columns.add(column);
        return this;
    }

    List<String> getColumnAlias(){
        List<String> columnAlias = new ArrayList<>();
        for (String column : columns) {
            if(column.contains("as ") ) {
                String[] parts = column.split("as ");
                String alias = parts[parts.length - 1];
                columnAlias.add(alias.trim());
                continue;
            }
            if(column.contains("AS ") ) {
                String[] parts = column.split("AS ");
                String alias = parts[parts.length - 1];
                columnAlias.add(alias.trim());
                continue;
            }
            if(column.contains(".") ) {
                String[] parts = column.split("\\.");
                String alias = parts[parts.length - 1];
                columnAlias.add(alias.trim());
                continue;
            }
            columnAlias.add(column.trim());
        }
        return columnAlias;
    }

    public String getColumnSql() {
        return String.join(",", columns);
    }
}
