package com.codingapi.example.command;

import com.codingapi.example.pojo.cmd.FlowWorkCmd;
import com.codingapi.example.router.FlowWorkRouter;
import com.codingapi.springboot.framework.dto.request.IdRequest;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/cmd/flowWork")
@AllArgsConstructor
public class FlowWorkCmdController {

    private final FlowWorkRouter flowWorkRouter;

    @PostMapping("/save")
    public Response save(@RequestBody FlowWorkCmd.CreateRequest request){
        flowWorkRouter.save(request);
        return Response.buildSuccess();
    }

    @PostMapping("/copy")
    public Response copy(@RequestBody IdRequest request){
        flowWorkRouter.copy(request.getLongId());
        return Response.buildSuccess();
    }

    @PostMapping("/schema")
    public Response schema(@RequestBody FlowWorkCmd.SchemaRequest request){
        log.info("schema:{}",request);
        flowWorkRouter.schema(request);
        return Response.buildSuccess();
    }

    @PostMapping("/changeState")
    public Response changeState(@RequestBody IdRequest request){
        flowWorkRouter.changeState(request.getLongId());
        return Response.buildSuccess();
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody IdRequest request){
        flowWorkRouter.delete(request.getLongId());
        return Response.buildSuccess();
    }

}
