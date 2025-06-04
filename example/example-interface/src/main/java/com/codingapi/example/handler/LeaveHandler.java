package com.codingapi.example.handler;

import com.codingapi.example.domain.leave.service.LeaveService;
import com.codingapi.example.infra.flow.form.LeaveForm;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LeaveHandler implements IHandler<FlowApprovalEvent> {

    private final LeaveService leaveService;

    @Override
    public void handler(FlowApprovalEvent event) {
        if (event.isFinish() && event.match(LeaveForm.class)) {
            LeaveForm form = (LeaveForm) event.getBindData();
            leaveService.create(form.toLeave());
        }
    }
}
