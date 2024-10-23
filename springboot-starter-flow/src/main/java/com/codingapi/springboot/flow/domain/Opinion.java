package com.codingapi.springboot.flow.domain;

import lombok.Getter;
import lombok.ToString;

/**
 * 审批意见
 */
@Getter
@ToString
public class Opinion {

    private final String opinion;
    private final boolean success;

    public Opinion(String opinion, boolean success) {
        this.opinion = opinion;
        this.success = success;
    }


    public static Opinion success(String opinion){
        return new Opinion(opinion,true);
    }
}
