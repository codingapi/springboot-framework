package com.codingapi.example.api.query.app;


import com.codingapi.example.app.query.service.FlowAppQueryService;
import com.codingapi.example.infra.flow.entity.FlowRecordEntity;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/query/flowRecord")
@AllArgsConstructor
public class FlowAppQueryController {

    private final FlowAppQueryService flowAppQueryService;

    @GetMapping("/list")
    public MultiResponse<FlowRecordEntity> list(SearchRequest searchRequest) {
        return MultiResponse.of(flowAppQueryService.list(searchRequest));
    }


    @GetMapping("/findAllByOperatorId")
    public MultiResponse<FlowRecordEntity> findAllByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowAppQueryService.findAllByOperatorId(searchRequest));
    }


    @GetMapping("/findTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTodoByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowAppQueryService.findTodoByOperatorId(searchRequest));
    }


    @GetMapping("/findDoneByOperatorId")
    public MultiResponse<FlowRecordEntity> findDoneByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowAppQueryService.findDoneByOperatorId(searchRequest));
    }


    @GetMapping("/findInitiatedByOperatorId")
    public MultiResponse<FlowRecordEntity> findInitiatedByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowAppQueryService.findInitiatedByOperatorId(searchRequest));
    }


    @GetMapping("/findTimeoutTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTimeoutTodoByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowAppQueryService.findTimeoutTodoByOperatorId(searchRequest));
    }


    @GetMapping("/findPostponedTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findPostponedTodoByOperatorId(SearchRequest searchRequest) {
        return MultiResponse.of(flowAppQueryService.findPostponedTodoByOperatorId(searchRequest));
    }
}
