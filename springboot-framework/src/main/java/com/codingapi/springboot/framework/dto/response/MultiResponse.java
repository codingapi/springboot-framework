package com.codingapi.springboot.framework.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * @author lorne
 * @date 2020/12/17
 * @description
 */
@Setter
@Getter
public class MultiResponse<T> extends Response {

    private long total;

    private Collection<T> data;

    public static <T> MultiResponse<T> of(Collection<T> data, long total) {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(true);
        multiResponse.setData(data);
        multiResponse.setTotal(total);
        return multiResponse;
    }


    public static <T> MultiResponse<T> of(Collection<T> data) {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(true);
        multiResponse.setData(data);
        long total = 0;
        if(data!=null){
            total = data.size();
        }
        multiResponse.setTotal(total);
        return multiResponse;
    }






}
