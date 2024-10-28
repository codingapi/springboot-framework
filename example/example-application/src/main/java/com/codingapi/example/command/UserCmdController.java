package com.codingapi.example.command;

import com.codingapi.example.pojo.cmd.UserCmd;
import com.codingapi.example.router.UserRouter;
import com.codingapi.springboot.framework.dto.request.IdRequest;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cmd/user")
@AllArgsConstructor
public class UserCmdController {

    private final UserRouter userRouter;

    @PostMapping("/save")
    public Response save(@RequestBody UserCmd.UpdateRequest request) {
        userRouter.save(request);
        return Response.buildSuccess();
    }

    @PostMapping("/removeEntrust")
    public Response removeEntrust(@RequestBody IdRequest request) {
        userRouter.removeEntrust(request.getLongId());
        return Response.buildSuccess();
    }

    @PostMapping("/entrust")
    public Response entrust(@RequestBody UserCmd.EntrustRequest request) {
        userRouter.entrust(request);
        return Response.buildSuccess();
    }

    @PostMapping("/changeManager")
    public Response changeManager(@RequestBody IdRequest request) {
        userRouter.changeManager(request.getLongId());
        return Response.buildSuccess();
    }

    @PostMapping("/remove")
    public Response remove(@RequestBody IdRequest request) {
        userRouter.remove(request.getLongId());
        return Response.buildSuccess();
    }


}
