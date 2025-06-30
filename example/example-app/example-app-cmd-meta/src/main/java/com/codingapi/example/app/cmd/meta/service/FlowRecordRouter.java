package com.codingapi.example.app.cmd.meta.service;

import com.codingapi.example.app.cmd.meta.pojo.FlowCmd;
import com.codingapi.example.infra.flow.user.FlowUserRepository;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.pojo.FlowStepResult;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.result.MessageResult;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FlowRecordRouter {


    private final FlowService flowService;
    private final FlowUserRepository flowUserRepository;

    public FlowResult startFlow(FlowCmd.StartFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        return flowService.startFlow(request.getWorkCode(), current, request.getBindData(), request.getAdvice());
    }

    public FlowStepResult getFlowStep(FlowCmd.FlowStep request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        if(request.hasRecordId()) {
            return flowService.getFlowStep(request.getRecordId(), request.getBindData(), current);
        }else {
            return flowService.getFlowStep(request.getWorkCode(), request.getBindData(), current);
        }
    }


    public FlowSubmitResult trySubmitFlow(FlowCmd.SubmitFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        if (request.getRecordId() > 0) {
            return flowService.trySubmitFlow(request.getRecordId(), current, request.getBindData(), request.getOpinion());
        } else {
            return flowService.trySubmitFlow(request.getWorkCode(), current, request.getBindData(), request.getOpinion());
        }
    }


    public FlowResult submitFlow(FlowCmd.SubmitFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        if (current.isFlowManager()) {
            return flowService.interfere(request.getRecordId(), current, request.getBindData(), request.getOpinion());
        } else {
            return flowService.submitFlow(request.getRecordId(), current, request.getBindData(), request.getOpinion());
        }
    }

    public MessageResult customFlowEvent(FlowCmd.CustomFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        return flowService.customFlowEvent(request.getRecordId(), current, request.getButtonId(), request.getBindData(), request.getOpinion());
    }


    public void save(FlowCmd.SaveFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        flowService.save(request.getRecordId(), current, request.getBindData(), request.getAdvice());
    }

    public void recall(FlowCmd.RecallFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        flowService.recall(request.getRecordId(), current);
    }


    public void transfer(FlowCmd.TransferFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        IFlowOperator target = flowUserRepository.getFlowOperatorById(request.getTargetUserId());
        flowService.transfer(request.getRecordId(), current, target, request.getBindData(), request.getAdvice());
    }


    public void postponed(FlowCmd.PostponedFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        flowService.postponed(request.getRecordId(), current, request.getTimeOut());
    }


    public void urge(FlowCmd.UrgeFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        flowService.urge(request.getRecordId(), current);
    }


    public void remove(FlowCmd.RemoveFlow request) {
        IFlowOperator current = flowUserRepository.getUserByUsername(request.getUserName());
        flowService.remove(request.getRecordId(), current);
    }
}
