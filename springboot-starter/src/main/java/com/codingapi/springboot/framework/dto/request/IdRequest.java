package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IdRequest {

    private String id;

    public String getStringId(){
        return id;
    }

    public int getIntId(){
        return Integer.parseInt(id);
    }

    public Long getLongId(){
        return Long.parseLong(id);
    }

    public float getFloatId(){
        return Float.parseFloat(id);
    }

    public double getDoubleId(){
        return Double.parseDouble(id);
    }


}
