package com.codingapi.springboot.authorization.handler;

import lombok.Getter;

@Getter
public class Condition {

    private final String condition;

    private Condition(String condition) {
        this.condition = condition;
    }

    public static Condition customCondition(String condition) {
        return new Condition(condition);
    }

    public static Condition formatCondition(String condition, Object... args) {
        return new Condition(String.format(condition, args));
    }

    public static Condition emptyCondition() {
        return null;
    }

    public static Condition defaultCondition() {
        return new Condition("1=1");
    }

}
