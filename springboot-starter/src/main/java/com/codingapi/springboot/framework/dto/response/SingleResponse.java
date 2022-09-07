package com.codingapi.springboot.framework.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lorne
 * @date 2020/12/17
 */
@Setter
@Getter
public class SingleResponse<T> extends Response {

    private T data;

    public static <T> SingleResponse<T> of(T data) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(true);
        singleResponse.setData(data);
        return singleResponse;
    }


    public static <T> SingleResponse<T> empty() {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(true);
        singleResponse.setData(null);
        return singleResponse;
    }


}
