package com.codingapi.example.api.query;

import com.codingapi.example.app.query.service.UserQueryService;
import com.codingapi.example.infra.db.entity.UserEntity;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/query/user")
@AllArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    @GetMapping("/list")
    public MultiResponse<UserEntity> list(SearchRequest searchRequest) {
        return MultiResponse.of(userQueryService.list(searchRequest));
    }
}
