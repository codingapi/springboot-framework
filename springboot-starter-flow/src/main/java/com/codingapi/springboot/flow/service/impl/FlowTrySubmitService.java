package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.service.FlowServiceRepositoryHolder;
import com.codingapi.springboot.flow.user.IFlowOperator;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FlowTrySubmitService {

    private final IFlowOperator currentOperator;
    private final IBindData bindData;
    private final Opinion opinion;
    private final FlowServiceRepositoryHolder flowServiceRepositoryHolder;

    public FlowTrySubmitService(IFlowOperator currentOperator,
                                IBindData bindData,
                                Opinion opinion,
                                FlowServiceRepositoryHolder flowServiceRepositoryHolder) {
        this.currentOperator = currentOperator;
        this.bindData = bindData;
        this.opinion = opinion;
        this.flowServiceRepositoryHolder = flowServiceRepositoryHolder;
    }


    public FlowSubmitResult trySubmitFlow(long recordId) {
        FlowSubmitService flowSubmitService = new FlowSubmitService(recordId, currentOperator, bindData, opinion, flowServiceRepositoryHolder);
        return flowSubmitService.trySubmitFlow();
    }


    public FlowSubmitResult trySubmitFlow(String workCode) {
        FlowStartService flowStartService = new FlowStartService(workCode, currentOperator, bindData, opinion.getAdvice(), flowServiceRepositoryHolder);
        FlowRecord flowRecord = flowStartService.tryStartFlow();

        FlowSubmitService flowSubmitService = new FlowSubmitService(flowRecord, flowStartService.getFlowWork(), currentOperator, bindData, opinion, flowServiceRepositoryHolder);
        return flowSubmitService.trySubmitFlow();
    }

}
