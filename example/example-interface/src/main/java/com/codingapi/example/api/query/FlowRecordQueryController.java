package com.codingapi.example.api.query;

import com.codingapi.example.app.query.service.FlowRecordQueryService;
import com.codingapi.example.infra.flow.entity.FlowRecordEntity;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query/flowRecord")
@AllArgsConstructor
public class FlowRecordQueryController {

    private final FlowRecordQueryService flowRecordQueryService;

    @GetMapping("/list")
    public MultiResponse<FlowRecordEntity> list(SearchRequest searchRequest) {
        return MultiResponse.of(flowRecordQueryService.list(searchRequest));
    }


    @GetMapping("/detail")
    public SingleResponse<FlowDetail> detail(SearchRequest searchRequest) {
        return SingleResponse.of(flowRecordQueryService.detail(searchRequest));
    }



    @GetMapping("/findAllByOperatorId")
    public MultiResponse<FlowRecordEntity> findAllByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowRecordQueryService.findAllByOperatorId(searchRequest));
    }


    @GetMapping("/findTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTodoByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowRecordQueryService.findTodoByOperatorId(searchRequest));
    }


    @GetMapping("/findDoneByOperatorId")
    public MultiResponse<FlowRecordEntity> findDoneByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowRecordQueryService.findDoneByOperatorId(searchRequest));
    }


    @GetMapping("/findInitiatedByOperatorId")
    public MultiResponse<FlowRecordEntity> findInitiatedByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowRecordQueryService.findInitiatedByOperatorId(searchRequest));
    }


    @GetMapping("/findTimeoutTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTimeoutTodoByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowRecordQueryService.findTimeoutTodoByOperatorId(searchRequest));
    }


    @GetMapping("/findPostponedTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findPostponedTodoByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowRecordQueryService.findPostponedTodoByOperatorId(searchRequest));
    }

}
