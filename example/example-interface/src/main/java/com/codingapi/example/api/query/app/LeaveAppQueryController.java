package com.codingapi.example.api.query.app;

import com.codingapi.example.app.query.service.LeaveAppQueryService;
import com.codingapi.example.infra.db.entity.LeaveEntity;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/query/leave")
@AllArgsConstructor
public class LeaveAppQueryController {

    private final LeaveAppQueryService leaveAppQueryService;

    @GetMapping("/list")
    public MultiResponse<LeaveEntity> list(SearchRequest searchRequest){
        return MultiResponse.of(leaveAppQueryService.list(searchRequest));
    }

}
