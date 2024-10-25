package com.codingapi.springboot.flow.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 审批意见
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Opinion {

    // 默认审批(人工审批)
    public static final int TYPE_DEFAULT = 0;
    // 系统自动审批
    public static final int TYPE_AUTO = 1;


    // 审批结果 暂存
    public static final int RESULT_SAVE = 0;
    // 审批结果 转办
    public static final int RESULT_TRANSFER = 1;
    // 审批结果 通过
    public static final int RESULT_PASS = 2;
    // 审批结果 驳回
    public static final int RESULT_REJECT = 3;

    /**
     * 审批意见
     */
    private String advice;
    /**
     * 审批结果
     */
    private int result;
    /**
     * 意见类型
     */
    private int type;

    public Opinion(String advice, int result, int type) {
        this.advice = advice;
        this.result = result;
        this.type = type;
    }

    public static Opinion save(String advice) {
        return new Opinion(advice, RESULT_SAVE, TYPE_DEFAULT);
    }

    public static Opinion pass(String advice) {
        return new Opinion(advice, RESULT_PASS, TYPE_DEFAULT);
    }

    public static Opinion reject(String advice) {
        return new Opinion(advice, RESULT_REJECT, TYPE_DEFAULT);
    }

    public static Opinion transfer(String advice) {
        return new Opinion(advice, RESULT_TRANSFER, TYPE_DEFAULT);
    }

    public static Opinion unSignAutoSuccess() {
        return new Opinion("非会签自动审批", RESULT_PASS, TYPE_AUTO);
    }

    public boolean isSuccess() {
        return result == RESULT_PASS;
    }
}
