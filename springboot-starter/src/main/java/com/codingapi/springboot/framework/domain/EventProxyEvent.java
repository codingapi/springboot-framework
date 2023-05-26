package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.event.IEvent;

public class EventProxyEvent implements IEvent {

    private Object entity;

    private long timestamp;

    private String field;
    private Object oldField;
    private Object newField;
}
