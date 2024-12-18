package com.codingapi.springboot.authorization.properties;

import lombok.Getter;

public class DataAuthorizationPropertyContext {

    @Getter
    private final static DataAuthorizationPropertyContext instance = new DataAuthorizationPropertyContext();

    private DataAuthorizationPropertyContext(){}

    private DataAuthorizationProperty dataAuthorizationProperty;

    protected void setDataAuthorizationProperty(DataAuthorizationProperty dataAuthorizationProperty){
        this.dataAuthorizationProperty = dataAuthorizationProperty;
    }

    public boolean showSql(){
        return dataAuthorizationProperty.isShowSql();
    }
}
