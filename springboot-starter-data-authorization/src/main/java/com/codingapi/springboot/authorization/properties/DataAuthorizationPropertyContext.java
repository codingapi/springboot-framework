package com.codingapi.springboot.authorization.properties;

import lombok.Getter;

public class DataAuthorizationPropertyContext {

    @Getter
    private final static DataAuthorizationPropertyContext instance = new DataAuthorizationPropertyContext();

    private DataAuthorizationPropertyContext(){}

    private DataAuthorizationProperties dataAuthorizationProperties;

    protected void setDataAuthorizationProperties(DataAuthorizationProperties dataAuthorizationProperties){
        this.dataAuthorizationProperties = dataAuthorizationProperties;
    }

    public boolean showSql(){
        if(dataAuthorizationProperties!=null) {
            return dataAuthorizationProperties.isShowSql();
        }
        return false;
    }
}
