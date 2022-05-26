package com.codingapi.springboot.framework.exception;

import lombok.Getter;

public class MessageException extends RuntimeException{

    @Getter
    private final String errCode;

    @Getter
    private final String errMessage;


    public MessageException(String errCode, String errMessage, Throwable cause) {
        super(errMessage, cause);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public MessageException(String errCode,  Throwable cause) {
        super(ExceptionMessageContext.getInstance().getErrorMsg(errCode), cause);
        this.errCode = errCode;
        this.errMessage = getMessage();
    }

    public MessageException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public MessageException(String errCode) {
        super(ExceptionMessageContext.getInstance().getErrorMsg(errCode));
        this.errCode = errCode;
        this.errMessage = getMessage();
    }

}
