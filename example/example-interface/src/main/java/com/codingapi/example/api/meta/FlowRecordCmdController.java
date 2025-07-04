package com.codingapi.example.api.meta;

import com.codingapi.example.app.cmd.meta.pojo.FlowCmd;
import com.codingapi.example.app.cmd.meta.service.FlowRecordRouter;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.pojo.FlowStepResult;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.result.MessageResult;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/cmd/flowRecord")
@AllArgsConstructor
public class FlowRecordCmdController {

    private final FlowRecordRouter flowRecordRouter;

    @PostMapping("/startFlow")
    public SingleResponse<FlowResult> startFlow(@RequestBody FlowCmd.StartFlow request) {
        return SingleResponse.of(flowRecordRouter.startFlow(request));
    }

    @PostMapping("/getFlowStep")
    public SingleResponse<FlowStepResult> getFlowStep(@RequestBody FlowCmd.FlowStep request) {
        return SingleResponse.of(flowRecordRouter.getFlowStep(request));
    }

    @PostMapping("/trySubmitFlow")
    public SingleResponse<FlowSubmitResult> trySubmitFlow(@RequestBody FlowCmd.SubmitFlow request) {
        return SingleResponse.of(flowRecordRouter.trySubmitFlow(request));
    }

    @PostMapping("/submitFlow")
    public SingleResponse<FlowResult> submitFlow(@RequestBody FlowCmd.SubmitFlow request) {
        return SingleResponse.of(flowRecordRouter.submitFlow(request));
    }

    @PostMapping("/custom")
    public SingleResponse<MessageResult> customFlowEvent(@RequestBody FlowCmd.CustomFlow request) {
        return SingleResponse.of(flowRecordRouter.customFlowEvent(request));
    }

    @PostMapping("/save")
    public Response save(@RequestBody FlowCmd.SaveFlow request) {
        flowRecordRouter.save(request);
        return Response.buildSuccess();
    }

    @PostMapping("/recall")
    public Response recall(@RequestBody FlowCmd.RecallFlow request) {
        flowRecordRouter.recall(request);
        return Response.buildSuccess();
    }

    @PostMapping("/transfer")
    public Response transfer(@RequestBody FlowCmd.TransferFlow request) {
        flowRecordRouter.transfer(request);
        return Response.buildSuccess();
    }

    @PostMapping("/postponed")
    public Response postponed(@RequestBody FlowCmd.PostponedFlow request) {
        flowRecordRouter.postponed(request);
        return Response.buildSuccess();
    }

    @PostMapping("/urge")
    public Response urge(@RequestBody FlowCmd.UrgeFlow request) {
        flowRecordRouter.urge(request);
        return Response.buildSuccess();
    }

    @PostMapping("/remove")
    public Response remove(@RequestBody FlowCmd.RemoveFlow request) {
        flowRecordRouter.remove(request);
        return Response.buildSuccess();
    }


    @PostMapping("/back")
    public Response back(@RequestBody FlowCmd.BackFlow request) {
        flowRecordRouter.back(request);
        return Response.buildSuccess();
    }


    @PostMapping("/voided")
    public Response voided(@RequestBody FlowCmd.VoidedFlow request) {
        flowRecordRouter.voided(request);
        return Response.buildSuccess();
    }
}
