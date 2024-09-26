package com.codingapi.springboot.flow.domain;

import lombok.Getter;

@Getter
public class Opinion {

    private final String opinion;
    private final boolean pass;

    public Opinion(String opinion, boolean pass) {
        this.opinion = opinion;
        this.pass = pass;
    }

    public Opinion(String opinion) {
        this.opinion = opinion;
        this.pass = true;
    }

    /**
     * 通过
     * @param opinion 意见
     */
    public static Opinion pass(String opinion) {
        return new Opinion(opinion, true);
    }

    /**
     * 拒绝
     * @param opinion 意见
     */
    public static Opinion reject(String opinion) {
        return new Opinion(opinion, false);
    }
}
