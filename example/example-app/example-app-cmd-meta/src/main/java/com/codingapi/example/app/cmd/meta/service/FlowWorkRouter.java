package com.codingapi.example.app.cmd.meta.service;

import com.codingapi.example.app.cmd.meta.pojo.FlowWorkCmd;
import com.codingapi.example.infra.flow.entity.FlowWorkEntity;
import com.codingapi.example.infra.flow.jpa.FlowWorkEntityRepository;
import com.codingapi.example.infra.flow.user.FlowUserRepository;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class FlowWorkRouter {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowWorkEntityRepository flowWorkEntityRepository;
    private final FlowUserRepository flowUserRepository;

    public void delete(long id) {
        flowWorkRepository.delete(id);
    }

    public void save(FlowWorkCmd.CreateRequest request) {
        IFlowOperator user = flowUserRepository.getUserByUsername(request.getUsername());
        long id = request.getId();
        if (id == 0) {
            FlowWork flowWork = new FlowWork(
                    request.getCode(),
                    request.getTitle(),
                    request.getDescription(),
                    request.isSkipIfSameApprover(),
                    request.getPostponedMax(),
                    user);
            flowWorkRepository.save(flowWork);
        } else {
            FlowWorkEntity flowWork = flowWorkEntityRepository.getFlowWorkEntityById(id);
            flowWork.setTitle(request.getTitle());
            flowWork.setCode(request.getCode());
            flowWork.setDescription(request.getDescription());
            flowWork.setPostponedMax(request.getPostponedMax());
            flowWork.setSkipIfSameApprover(request.isSkipIfSameApprover());
            flowWork.setUpdateTime(System.currentTimeMillis());
            flowWorkEntityRepository.save(flowWork);
        }
    }

    public void changeState(long id) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(id);
        if (flowWork.isEnable()) {
            flowWork.disbale();
        } else {
            flowWork.enable();
        }
        flowWorkRepository.save(flowWork);
    }

    public void schema(FlowWorkCmd.SchemaRequest request) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(request.getId());
        flowWork.schema(request.getSchema());
        flowWorkRepository.save(flowWork);
    }

    public void copy(Long id) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(id);
        FlowWork flowCopy = flowWork.copy();
        flowWorkRepository.save(flowCopy);
    }
}
