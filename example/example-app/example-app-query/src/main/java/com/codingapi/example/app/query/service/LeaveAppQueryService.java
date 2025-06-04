package com.codingapi.example.app.query.service;

import com.codingapi.example.infra.db.entity.LeaveEntity;
import com.codingapi.example.infra.db.jpa.LeaveEntityRepository;
import com.codingapi.springboot.fast.jpa.SQLBuilder;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class LeaveAppQueryService {

    private final LeaveEntityRepository leaveEntityRepository;

    public Page<LeaveEntity> list(SearchRequest searchRequest) {
        String username = TokenContext.current().getUsername();
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from LeaveEntity l where 1 =1 ");
        sqlBuilder.append(" and l.username = ? ", username);
        if (StringUtils.hasText(lastId)) {
            sqlBuilder.append(" and l.id < ? ", Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by l.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return leaveEntityRepository.dynamicPageQuery(sqlBuilder, pageRequest);
    }
}
