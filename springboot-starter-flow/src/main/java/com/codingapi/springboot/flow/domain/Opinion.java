package com.codingapi.springboot.flow.domain;

import lombok.Getter;

/**
 * 审批意见
 */
@Getter
public class Opinion {

    private final String opinion;
    private final boolean success;

    public Opinion(String opinion, boolean success) {
        this.opinion = opinion;
        this.success = success;
    }
}
