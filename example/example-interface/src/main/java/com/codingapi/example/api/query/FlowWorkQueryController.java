package com.codingapi.example.api.query;

import com.codingapi.example.app.query.service.FlowWorkQueryService;
import com.codingapi.example.infra.flow.entity.FlowWorkEntity;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query/flowWork")
@AllArgsConstructor
public class FlowWorkQueryController {

    private final FlowWorkQueryService flowWorkQueryService;

    @GetMapping("/list")
    public MultiResponse<FlowWorkEntity> list(SearchRequest searchRequest) {
        return MultiResponse.of(flowWorkQueryService.list(searchRequest));
    }

}
