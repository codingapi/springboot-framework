package com.codingapi.springboot.framework.exception;


class MessageContext {

    private static MessageContext context;

    public static MessageContext getInstance() {
        if (context == null) {
            synchronized (MessageContext.class) {
                context = new MessageContext();
            }
        }
        return context;
    }

    private LocaleMessage localeMessage;

    private MessageContext() {

    }

    void setExceptionLocaleMessage(LocaleMessage localeMessage) {
        this.localeMessage = localeMessage;
    }

    public String getErrorMsg(String errCode) {
        return localeMessage.getMessage(errCode);
    }

}
