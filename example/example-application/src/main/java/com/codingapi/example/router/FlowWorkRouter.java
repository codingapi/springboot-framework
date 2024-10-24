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

@Service
@AllArgsConstructor
public class FlowWorkRouter {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowWorkEntityRepository flowWorkEntityRepository;
    private final UserRepository userRepository;

    public void delete(long id){
        flowWorkRepository.delete(id);
    }

    public void save(FlowWorkCmd.CreateRequest request) {
        User user = userRepository.getUserByUsername(request.getUsername());
        long id = request.getId();
        if (id == 0) {
            FlowWork flowWork = new FlowWork(request.getTitle(), request.getDescription(), user);
            flowWorkRepository.save(flowWork);
        } else {
            FlowWorkEntity flowWork = flowWorkEntityRepository.getFlowWorkEntityById(id);
            flowWork.setTitle(request.getTitle());
            flowWork.setDescription(request.getDescription());
            flowWork.setPostponedMax(request.getPostponedMax());
            flowWorkEntityRepository.save(flowWork);
        }
    }

    public void changeState(long id) {
        FlowWorkEntity flowWork = flowWorkEntityRepository.getFlowWorkEntityById(id);
        if(flowWork.isEnable()){
            flowWork.setEnable(false);
        }else{
            flowWork.setEnable(true);
        }
        flowWorkEntityRepository.save(flowWork);
    }

    public void schema(FlowWorkCmd.SchemaRequest request) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(request.getId());
        flowWork.schema(request.getSchema());
        flowWorkRepository.save(flowWork);
    }
}
