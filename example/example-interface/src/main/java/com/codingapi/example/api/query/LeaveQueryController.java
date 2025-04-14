package com.codingapi.example.api.query;

import com.codingapi.example.infra.db.entity.LeaveEntity;
import com.codingapi.example.infra.db.jpa.LeaveEntityRepository;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query/leave")
@AllArgsConstructor
public class LeaveQueryController {

    private final LeaveEntityRepository leaveEntityRepository;

    @GetMapping("/list")
    public MultiResponse<LeaveEntity> list(SearchRequest searchRequest){
        return MultiResponse.of(leaveEntityRepository.searchRequest(searchRequest));
    }


}
