package com.codingapi.example.router;

import com.codingapi.example.domain.User;
import com.codingapi.example.entity.FlowWorkEntity;
import com.codingapi.example.jpa.FlowWorkEntityRepository;
import com.codingapi.example.pojo.cmd.FlowWorkCmd;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
@Transactional
public class FlowWorkRouter {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowWorkEntityRepository flowWorkEntityRepository;
    private final UserRepository userRepository;

    public void delete(long id) {
        flowWorkRepository.delete(id);
    }

    public void save(FlowWorkCmd.CreateRequest request) {
        User user = userRepository.getUserByUsername(request.getUsername());
        long id = request.getId();
        if (id == 0) {
            FlowWork flowWork = new FlowWork(request.getCode(),request.getTitle(), request.getDescription(), request.getPostponedMax(), user);
            flowWorkRepository.save(flowWork);
        } else {
            FlowWorkEntity flowWork = flowWorkEntityRepository.getFlowWorkEntityById(id);
            flowWork.setTitle(request.getTitle());
            flowWork.setCode(request.getCode());
            flowWork.setDescription(request.getDescription());
            flowWork.setPostponedMax(request.getPostponedMax());
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
