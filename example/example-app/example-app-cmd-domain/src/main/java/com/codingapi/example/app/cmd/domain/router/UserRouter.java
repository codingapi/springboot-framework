package com.codingapi.example.app.cmd.domain.router;

import com.codingapi.example.app.cmd.domain.pojo.UserCmd;
import com.codingapi.example.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserRouter {

    private final UserService userService;

    public void createOrUpdate(UserCmd.UpdateRequest request) {
        if (request.hasId()) {
            userService.update(request.getId(), request.toMetric(),request.isFlowManager());
        } else {
            userService.create(request.toMetric(),request.isFlowManager());
        }
    }

    public void removeEntrust(long id) {
        userService.removeEntrust(id);
    }

    public void createEntrust(UserCmd.EntrustRequest request) {
        userService.createEntrust(request.getId(),request.getEntrustUserId());
    }

    public void changeManager(long id) {
        userService.changeManager(id);
    }

    public void removeUser(long id) {
        userService.removeUser(id);
    }
}
