package com.codingapi.springboot.flow.domain;

import lombok.Getter;
import lombok.ToString;

/**
 * 审批意见
 */
@Getter
@ToString
public class Opinion {

    // 默认审批(人工审批)
    public static final int TYPE_DEFAULT = 0;
    // 非会签时自动审批
    public static final int TYPE_UN_SIGN_AUTO = 1;
    // 完成是自动审批
    public static final int TYPE_FINISH_AUTO = 2;

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
    private final int type;

    public Opinion(String advice, boolean success, int type) {
        this.advice = advice;
        this.success = success;
        this.type = type;

    }

    public static Opinion pass(String advice) {
        return new Opinion(advice, true, TYPE_DEFAULT);
    }

    public static Opinion reject(String advice) {
        return new Opinion(advice, false, TYPE_DEFAULT);
    }

    public static Opinion unSignAutoSuccess() {
        return new Opinion("非会签自动审批", true, TYPE_UN_SIGN_AUTO);
    }

    public static Opinion finishAutoSuccess() {
        return new Opinion("流程结束自动审批", true, TYPE_FINISH_AUTO);
    }

}
