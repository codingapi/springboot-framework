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
    // 转办审批
    public static final int TYPE_TRANSFER = 2;

    /**
     * 审批意见
     */
    private final String advice;
    /**
     * 审批结果
     */
    private final boolean success;
    /**
     * 意见类型
     */
    private final int type;

    public Opinion(String advice, boolean success, int type) {
        this.advice = advice;
        this.success = success;
        this.type = type;
    }

    public boolean isTransfer() {
        return type == TYPE_TRANSFER;
    }

    public boolean isUnSignAuto() {
        return type == TYPE_UN_SIGN_AUTO;
    }

    public boolean isDefault() {
        return type == TYPE_DEFAULT;
    }

    public static Opinion pass(String advice) {
        return new Opinion(advice, true, TYPE_DEFAULT);
    }

    public static Opinion reject(String advice) {
        return new Opinion(advice, false, TYPE_DEFAULT);
    }

    public static Opinion transfer(String advice) {
        return new Opinion(advice, true, TYPE_TRANSFER);
    }

    public static Opinion unSignAutoSuccess() {
        return new Opinion("非会签自动审批", true, TYPE_UN_SIGN_AUTO);
    }

}
