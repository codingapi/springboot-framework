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

    public LocaleMessageException(String errCode,Throwable cause) {
        super(MessageContext.getInstance().getErrorMsg(errCode), cause);
        this.errCode = errCode;
        this.errMessage = getMessage();
    }


    public LocaleMessageException(String errCode,Object[] args, Throwable cause) {
        super(MessageContext.getInstance().getErrorMsg(errCode,args), cause);
        this.errCode = errCode;
        this.errMessage = getMessage();
    }

    public LocaleMessageException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public LocaleMessageException(String errCode,Object[] args) {
        super(MessageContext.getInstance().getErrorMsg(errCode,args));
        this.errCode = errCode;
        this.errMessage = getMessage();
    }

    public LocaleMessageException(String errCode) {
        super(MessageContext.getInstance().getErrorMsg(errCode));
        this.errCode = errCode;
        this.errMessage = getMessage();
    }


    /**
     * 占位符异常，在message.properties中配置错误信息,占位符参数从0开始实例如下
     * error3= 错误 3 {0} {1}
     * @param errCode 错误码
     * @param args 占位符参数
     * @return 异常
     */
    public static LocaleMessageException of(String errCode, Object ...args) {
        return new LocaleMessageException(errCode, args);
    }


}
