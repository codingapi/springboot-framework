package com.codingapi.example.handler;

import com.codingapi.example.domain.leave.entity.Leave;
import com.codingapi.example.domain.leave.repository.LeaveRepository;
import com.codingapi.example.infra.flow.form.LeaveForm;
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
        if(event.isFinish() && event.match(LeaveForm.class)){
            LeaveForm form = (LeaveForm)event.getBindData();

            Leave leave = new Leave();
            leave.setId(form.getId());
            leave.setUsername(form.getUsername());
            leave.setCreateTime(form.getCreateTime());
            leave.setDays(form.getDays());
            leave.setDesc(form.getDesc());

            leaveRepository.save(leave);
        }
    }
}
