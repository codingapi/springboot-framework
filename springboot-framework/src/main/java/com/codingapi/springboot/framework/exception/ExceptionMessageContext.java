package com.codingapi.springboot.framework.exception;


public class ExceptionMessageContext {

    private static ExceptionMessageContext context;

    public static ExceptionMessageContext getInstance(){
        if(context==null){
            synchronized (ExceptionMessageContext.class){
                context = new ExceptionMessageContext();
            }
        }
        return context;
    }

    private ExceptionLocaleMessage exceptionLocaleMessage;

    private ExceptionMessageContext(){

    }

    void setExceptionLocaleMessage(ExceptionLocaleMessage exceptionLocaleMessage){
        this.exceptionLocaleMessage = exceptionLocaleMessage;
    }

    public String getErrorMsg(String errCode){
        return exceptionLocaleMessage.getMessage(errCode);
    }

}
