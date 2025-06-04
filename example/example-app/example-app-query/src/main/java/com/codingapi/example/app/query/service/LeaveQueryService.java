package com.codingapi.example.app.query.service;


import com.codingapi.example.infra.db.entity.LeaveEntity;
import com.codingapi.example.infra.db.jpa.LeaveEntityRepository;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LeaveQueryService {

    private final LeaveEntityRepository leaveEntityRepository;

    public Page<LeaveEntity> list(SearchRequest searchRequest){
        return leaveEntityRepository.searchRequest(searchRequest);
    }

}
