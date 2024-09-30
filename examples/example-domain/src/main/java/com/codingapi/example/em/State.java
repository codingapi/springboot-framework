package com.codingapi.example.em;

import com.codingapi.springboot.framework.em.IEnum;

public enum State implements IEnum {
    /**
     * 禁用
     */
    DISABLE(0),
    /**
     * 正常
     */
    ENABLE(1);


    private final int code;

    State(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static State of(int code) {
        for (State state : State.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        return null;
    }
}
