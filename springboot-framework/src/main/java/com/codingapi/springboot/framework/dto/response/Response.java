package com.codingapi.springboot.framework.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lorne
 * @date 2020/12/17
 * @description
 */
@Setter
@Getter
public class Response implements Serializable {

    private boolean isSuccess;

    private String errCode;

    private String errMessage;

    public static Response buildFailure(String errCode, String errMessage) {
        Response response = new Response();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    public static Response buildSuccess(){
        Response response = new Response();
        response.setSuccess(true);
        return response;
    }



}
