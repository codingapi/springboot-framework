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
    private final boolean auto;

    public Opinion(String opinion, boolean success, boolean auto) {
        this.opinion = opinion;
        this.success = success;
        this.auto = auto;

    }

    public Opinion(String opinion, boolean success) {
        this(opinion, success, false);
    }


    public static Opinion success(String opinion) {
        return new Opinion(opinion, true);
    }

    public static Opinion autoSuccess() {
        return new Opinion("自动审批", true);
    }


}
