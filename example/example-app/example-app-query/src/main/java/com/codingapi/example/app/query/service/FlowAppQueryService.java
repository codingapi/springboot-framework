package com.codingapi.example.app.query.service;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.domain.user.repository.UserRepository;
import com.codingapi.example.infra.flow.entity.FlowRecordEntity;
import com.codingapi.example.infra.flow.jpa.FlowRecordEntityRepository;
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
public class FlowAppQueryService {

    private final FlowRecordEntityRepository flowRecordQuery;
    private final UserRepository userRepository;

    public Page<FlowRecordEntity> list(SearchRequest searchRequest) {
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity d where d.id in (select max(r.id) from FlowRecordEntity  r group by r.processId ) ");
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and d.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by d.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest);
    }


    public Page<FlowRecordEntity> findAllByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1  group by r.processId) ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and d.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by d.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest);
    }


    public Page<FlowRecordEntity> findTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and r.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by r.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest);
    }


    public Page<FlowRecordEntity> findDoneByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'DONE' group by r.processId ) ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and d.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by d.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest);
    }


    public Page<FlowRecordEntity> findInitiatedByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.preId = 0 and r.nodeCode = 'start'  group by r.processId) ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and d.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by d.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest);
    }


    public Page<FlowRecordEntity> findTimeoutTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.timeoutTime >0 and r.timeoutTime < ?2 ");
        sqlBuilder.addParam(user.getId());
        sqlBuilder.addParam(System.currentTimeMillis());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and r.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by r.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest);
    }


    public Page<FlowRecordEntity> findPostponedTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.postponedCount > 0 ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and r.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by r.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest);
    }
}
