package com.codingapi.springboot.flow.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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
    // 审批结果 抄送
    public static final int RESULT_CIRCULATE = 4;

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

    /**
     *  指定流程的操作者
     *  operatorIds 为空时，表示不指定操作者，由流程配置的操作者匹配器决定
     */
    private List<Long> operatorIds;

    public Opinion(String advice, int result, int type) {
        this.advice = advice;
        this.result = result;
        this.type = type;
    }

    public Opinion specify(List<Long> operatorIds) {
        this.operatorIds = operatorIds;
        return this;
    }

    public Opinion specify(long... operatorIds) {
        List<Long> operatorIdList = new ArrayList<>();
        for (long operatorId : operatorIds) {
            operatorIdList.add(operatorId);
        }
        return specify(operatorIdList);
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

    public static Opinion circulate() {
        return new Opinion("", RESULT_CIRCULATE, TYPE_AUTO);
    }

    public boolean isCirculate() {
        return result == RESULT_CIRCULATE;
    }

    public boolean isSuccess() {
        return result == RESULT_PASS;
    }

    public boolean isReject() {
        return result == RESULT_REJECT;
    }
}
