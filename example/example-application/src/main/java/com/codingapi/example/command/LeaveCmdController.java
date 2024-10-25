package com.codingapi.example.command;

import com.codingapi.example.domain.Leave;
import com.codingapi.example.domain.User;
import com.codingapi.example.pojo.cmd.LeaveCmd;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cmd/leave")
@AllArgsConstructor
public class LeaveCmdController {

    private final FlowService flowService;
    private final UserRepository userRepository;


    @PostMapping("/startLeave")
    public Response startLeave(@RequestBody LeaveCmd.StartLeave request) {
        User user = userRepository.getUserByUsername(request.getUsername());

        Leave leave = new Leave();
        leave.setUsername(user.getUsername());
        leave.setDesc(request.getDesc());
        leave.setDays(request.getDays());
        leave.setCreateTime(System.currentTimeMillis());

        flowService.startFlow(request.getFlowId(), user, leave, request.getDesc());

        return Response.buildSuccess();
    }
}
