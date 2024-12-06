package com.codingapi.springboot.authorization.register;

import com.codingapi.springboot.authorization.DataAuthorizationContext;
import com.codingapi.springboot.authorization.filter.DataAuthorizationFilter;

import java.util.List;

public class DataAuthorizationContextRegister {

    public DataAuthorizationContextRegister(List<DataAuthorizationFilter> dataAuthorizationFilters) {
        if(dataAuthorizationFilters!=null) {
            for (DataAuthorizationFilter filter : dataAuthorizationFilters) {
                DataAuthorizationContext.getInstance().addDataAuthorizationFilter(filter);
            }
        }
    }
}
