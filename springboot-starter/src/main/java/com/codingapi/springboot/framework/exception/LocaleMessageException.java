package com.codingapi.springboot.framework.exception;

import lombok.Getter;

public class LocaleMessageException extends RuntimeException {

    @Getter
    private final String errCode;

    @Getter
    private final String errMessage;


    public LocaleMessageException(String errCode, String errMessage, Throwable cause) {
        super(errMessage, cause);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public LocaleMessageException(String errCode, Throwable cause) {
        super(MessageContext.getInstance().getErrorMsg(errCode), cause);
        this.errCode = errCode;
        this.errMessage = getMessage();
    }

    public LocaleMessageException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public LocaleMessageException(String errCode) {
        super(MessageContext.getInstance().getErrorMsg(errCode));
        this.errCode = errCode;
        this.errMessage = getMessage();
    }

}
