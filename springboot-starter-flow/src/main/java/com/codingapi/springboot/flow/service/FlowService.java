package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.result.MessageResult;
import com.codingapi.springboot.flow.service.impl.*;
import com.codingapi.springboot.flow.user.IFlowOperator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


/**
 * 流程服务
 */
@Transactional
public class FlowService {

    private final FlowDetailService flowDetailService;
    private final FlowCustomEventService flowCustomEventService;
    private final FlowRecallService flowRecallService;
    private final FlowSaveService flowSaveService;
    private final FlowTransferService flowTransferService;
    private final FlowPostponedService flowPostponedService;
    private final FlowUrgeService flowUrgeService;

    private final FlowServiceRepositoryHolder flowServiceRepositoryHolder;


    public FlowService(FlowWorkRepository flowWorkRepository,
                       FlowRecordRepository flowRecordRepository,
                       FlowBindDataRepository flowBindDataRepository,
                       FlowOperatorRepository flowOperatorRepository,
                       FlowProcessRepository flowProcessRepository,
                       FlowBackupRepository flowBackupRepository) {
        this.flowServiceRepositoryHolder = new FlowServiceRepositoryHolder(flowWorkRepository, flowRecordRepository, flowBindDataRepository, flowOperatorRepository, flowProcessRepository, flowBackupRepository);
        this.flowDetailService = new FlowDetailService(flowWorkRepository, flowRecordRepository, flowBindDataRepository, flowOperatorRepository, flowProcessRepository);
        this.flowCustomEventService = new FlowCustomEventService(flowWorkRepository,flowRecordRepository, flowProcessRepository);
        this.flowRecallService = new FlowRecallService(flowWorkRepository,flowRecordRepository, flowProcessRepository);
        this.flowSaveService = new FlowSaveService(flowWorkRepository,flowRecordRepository, flowBindDataRepository, flowProcessRepository);
        this.flowTransferService = new FlowTransferService(flowWorkRepository,flowRecordRepository, flowBindDataRepository, flowProcessRepository);
        this.flowPostponedService = new FlowPostponedService(flowWorkRepository,flowRecordRepository, flowProcessRepository);
        this.flowUrgeService = new FlowUrgeService(flowWorkRepository,flowRecordRepository, flowProcessRepository);
    }

    /**
     * 流程详情
     *
     * @param recordId 流程记录id
     * @param workCode 流程编码
     * @return 流程详情
     */
    public FlowDetail detail(long recordId, String workCode, IFlowOperator currentOperator) {
        if (StringUtils.hasText(workCode)) {
            return flowDetailService.detail(workCode, currentOperator);
        } else {
            return flowDetailService.detail(recordId, currentOperator);
        }
    }

    /**
     * 流程详情
     *
     * @param recordId 流程记录id
     * @return 流程详情
     */
    public FlowDetail detail(long recordId, IFlowOperator currentOperator) {
        return this.detail(recordId, null, currentOperator);
    }


    /**
     * 流程详情
     *
     * @param workCode 流程编号
     * @return 流程详情
     */
    public FlowDetail detail(String workCode, IFlowOperator currentOperator) {
        return this.detail(0, workCode, currentOperator);
    }


    /**
     * 流程详情
     *
     * @param recordId 流程记录id
     * @return 流程详情
     */
    public FlowDetail detail(long recordId) {
        return this.detail(recordId, null, null);
    }

    /**
     * 延期待办
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param time            延期时间
     */
    public void postponed(long recordId, IFlowOperator currentOperator, long time) {
        flowPostponedService.postponed(recordId, currentOperator, time);
    }

    /**
     * 催办流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void urge(long recordId, IFlowOperator currentOperator) {
        flowUrgeService.urge(recordId, currentOperator);
    }


    /**
     * 干预流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public FlowResult interfere(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        if (!currentOperator.isFlowManager()) {
            throw new IllegalArgumentException("current operator is not flow manager");
        }
        return this.submitFlow(recordId, currentOperator, bindData, opinion);
    }


    /**
     * 转办流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param targetOperator  转办操作者
     * @param bindData        绑定数据
     * @param advice          转办意见
     */
    public void transfer(long recordId, IFlowOperator currentOperator, IFlowOperator targetOperator, IBindData bindData, String advice) {
        flowTransferService.transfer(recordId, currentOperator, targetOperator, bindData, advice);
    }


    /**
     * 保存流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param advice          审批意见
     */
    public void save(long recordId, IFlowOperator currentOperator, IBindData bindData, String advice) {
        flowSaveService.save(recordId, currentOperator, bindData, advice);
    }


    /**
     * 发起流程 （不自动提交到下一节点）
     *
     * @param workCode 流程编码
     * @param operator 操作者
     * @param bindData 绑定数据
     * @param advice   审批意见
     */
    public FlowResult startFlow(String workCode, IFlowOperator operator, IBindData bindData, String advice) {
        FlowStartService flowStartService = new FlowStartService(workCode, operator, bindData, advice, flowServiceRepositoryHolder);
        return flowStartService.startFlow();
    }


    /**
     * 尝试提交流程 (流程过程中)
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public FlowSubmitResult trySubmitFlow(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        FlowTrySubmitService flowTrySubmitService = new FlowTrySubmitService(currentOperator, bindData, opinion, flowServiceRepositoryHolder);
        return flowTrySubmitService.trySubmitFlow(recordId);
    }


    /**
     * 尝试提交流程 (发起流程)
     *
     * @param workCode        流程编码
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public FlowSubmitResult trySubmitFlow(String workCode, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        FlowTrySubmitService flowTrySubmitService = new FlowTrySubmitService(currentOperator, bindData, opinion, flowServiceRepositoryHolder);
        return flowTrySubmitService.trySubmitFlow(workCode);
    }


    /**
     * 提交流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public FlowResult submitFlow(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        FlowSubmitService flowSubmitService = new FlowSubmitService(recordId, currentOperator, bindData, opinion, flowServiceRepositoryHolder);
        return flowSubmitService.submitFlow();
    }


    /**
     * 自定义事件
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param buttonId        按钮id
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public MessageResult customFlowEvent(long recordId, IFlowOperator currentOperator, String buttonId, IBindData bindData, Opinion opinion) {
        return flowCustomEventService.customFlowEvent(recordId, currentOperator, buttonId, bindData, opinion);
    }


    /**
     * 撤回流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void recall(long recordId, IFlowOperator currentOperator) {
        flowRecallService.recall(recordId, currentOperator);
    }


}
