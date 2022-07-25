package com.codingapi.springboot.framework.properties;

import com.codingapi.springboot.framework.dto.request.PageRequest;

public class BootProperties {

    private int pageCurrentFixValue = 1;


    public int getPageCurrentFixValue() {
        return pageCurrentFixValue;
    }

    public void setPageCurrentFixValue(int pageCurrentFixValue) {
        this.pageCurrentFixValue = pageCurrentFixValue;
        System.setProperty(PageRequest.CURRENT_FIX_VALUE,String.valueOf(pageCurrentFixValue));
    }

    public BootProperties() {
        System.setProperty(PageRequest.CURRENT_FIX_VALUE,String.valueOf(pageCurrentFixValue));
    }
}
