package com.codingapi.springboot.authorization.properties;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataAuthorizationProperties {

    private boolean showSql = false;

    public DataAuthorizationProperties() {
        DataAuthorizationPropertyContext.getInstance().setDataAuthorizationProperties(this);
    }

}
