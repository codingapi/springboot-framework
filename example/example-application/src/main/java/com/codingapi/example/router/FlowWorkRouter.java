package com.codingapi.example.router;

import com.codingapi.example.domain.User;
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
            FlowWork flowWork = flowWorkRepository.getFlowWorkById(id);
            flowWork.setTitle(request.getTitle());
            flowWork.setDescription(request.getDescription());
            flowWorkRepository.save(flowWork);
        }
    }

    public void changeState(long id) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(id);
        if(flowWork.isEnable()){
            flowWork.disbale();
        }else{
            flowWork.enable();
        }
        flowWorkRepository.save(flowWork);
    }
}
