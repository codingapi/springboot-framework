package com.codingapi.example.api.domain;

import com.codingapi.example.app.cmd.domain.pojo.UserCmd;
import com.codingapi.example.app.cmd.domain.router.UserRouter;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cmd/domain/user")
@AllArgsConstructor
public class UserDomainCmdController {

    private final UserRouter userRouter;

    @PostMapping("/save")
    public Response save(@RequestBody UserCmd.UpdateRequest request) {
        userRouter.save(request);
        return Response.buildSuccess();
    }
}
