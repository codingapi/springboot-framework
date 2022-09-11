package com.codingapi.springboot.framework.properties;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

public class BootProperties {

    private int pageCurrentFixValue = 1;

    @Setter
    @Getter
    private String aseKey = "ACDX$%^&*$#XCZas";

    @Setter
    @Getter
    private String aseIv = "ACXFGH@EDS#XCZas";


    public BootProperties() {
        System.setProperty(PageRequest.CURRENT_FIX_VALUE, String.valueOf(pageCurrentFixValue));
    }

    public int getPageCurrentFixValue() {
        return pageCurrentFixValue;
    }

    public void setPageCurrentFixValue(int pageCurrentFixValue) {
        this.pageCurrentFixValue = pageCurrentFixValue;
        System.setProperty(PageRequest.CURRENT_FIX_VALUE, String.valueOf(pageCurrentFixValue));
    }
}
