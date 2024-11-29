package com.codingapi.example.handler;

import com.codingapi.example.domain.Leave;
import com.codingapi.example.repository.LeaveRepository;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LeaveHandler implements IHandler<FlowApprovalEvent> {

    private final LeaveRepository leaveRepository;

    @Override
    public void handler(FlowApprovalEvent event) {
        if(event.isFinish() && event.match(Leave.class)){
            Leave leave = (Leave)event.getBindData();
            leaveRepository.save(leave);
        }
    }
}
