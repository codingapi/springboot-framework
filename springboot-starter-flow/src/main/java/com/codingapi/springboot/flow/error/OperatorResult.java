package com.codingapi.springboot.flow.error;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *  操作人员的异常匹配对象
 */
@Getter
public class OperatorResult extends ErrorResult {

    private final List<Long> operatorIds;

    public OperatorResult(List<Long> operatorIds) {
        this.operatorIds = operatorIds;
    }

    public OperatorResult(long... operatorIds) {
        this.operatorIds = new ArrayList<>();
        Arrays.stream(operatorIds).forEach(this.operatorIds::add);
    }
}
