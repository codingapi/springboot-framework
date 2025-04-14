package com.codingapi.example.app.query.service;

import com.codingapi.example.infra.db.entity.UserEntity;
import com.codingapi.example.infra.db.jpa.UserEntityRepository;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserQueryService {

    private final UserEntityRepository userEntityRepository;

    public Page<UserEntity> list(SearchRequest searchRequest) {
        return userEntityRepository.searchRequest(searchRequest);
    }

}
