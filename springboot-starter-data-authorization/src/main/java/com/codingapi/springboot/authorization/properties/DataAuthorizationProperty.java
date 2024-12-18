package com.codingapi.springboot.authorization.properties;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataAuthorizationProperty {

    private boolean showSql = false;

    public DataAuthorizationProperty() {
        DataAuthorizationPropertyContext.getInstance().setDataAuthorizationProperty(this);
    }

}
