package com.codingapi.springboot.flow.domain;

import lombok.Getter;
import lombok.ToString;

/**
 * 审批意见
 */
@Getter
@ToString
public class Opinion {

    /**
     * 审批意见
     */
    private final String advice;
    /**
     * 审批结果
     */
    private final boolean success;
    /**
     * 是否自动审批
     * 当非会签时，若多人审批的情况下，其中一个人审批了其他会会自动审批
     */
    private final boolean auto;

    public Opinion(String advice, boolean success, boolean auto) {
        this.advice = advice;
        this.success = success;
        this.auto = auto;

    }

    public Opinion(String advice, boolean success) {
        this(advice, success, false);
    }


    public static Opinion success(String advice) {
        return new Opinion(advice, true);
    }

    public static Opinion autoSuccess() {
        return new Opinion("自动审批", true);
    }


}
