package com.codingapi.springboot.framework.exception;


class MessageContext {

    private static MessageContext context;
    private LocaleMessage localeMessage;

    private MessageContext() {

    }

    public static MessageContext getInstance() {
        if (context == null) {
            synchronized (MessageContext.class) {
                context = new MessageContext();
            }
        }
        return context;
    }

    void setExceptionLocaleMessage(LocaleMessage localeMessage) {
        this.localeMessage = localeMessage;
    }

    public String getErrorMsg(String errCode) {
        return localeMessage.getMessage(errCode);
    }

}
