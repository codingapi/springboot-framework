package com.codingapi.example.query.app;


import com.codingapi.example.domain.User;
import com.codingapi.example.entity.FlowRecordEntity;
import com.codingapi.example.jpa.FlowRecordEntityRepository;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.fast.jpa.SQLBuilder;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app/query/flowRecord")
@AllArgsConstructor
public class FlowAppQueryController {

    private final FlowRecordEntityRepository flowRecordQuery;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public MultiResponse<FlowRecordEntity> list(SearchRequest searchRequest) {
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity d where d.id in (select max(r.id) from FlowRecordEntity  r group by r.processId ) ");
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and d.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by d.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest));
    }


    @GetMapping("/findAllByOperatorId")
    public MultiResponse<FlowRecordEntity> findAllByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1  group by r.processId) ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and d.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by d.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest));
    }


    @GetMapping("/findTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and r.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by r.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest));
    }


    @GetMapping("/findDoneByOperatorId")
    public MultiResponse<FlowRecordEntity> findDoneByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'DONE' group by r.processId ) ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and d.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by d.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest));
    }


    @GetMapping("/findInitiatedByOperatorId")
    public MultiResponse<FlowRecordEntity> findInitiatedByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.preId = 0 and r.nodeCode = 'start'  group by r.processId) ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and d.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by d.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest));
    }


    @GetMapping("/findTimeoutTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findTimeoutTodoByOperatorId(SearchRequest searchRequest) {
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
        return MultiResponse.of(flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest));
    }


    @GetMapping("/findPostponedTodoByOperatorId")
    public MultiResponse<FlowRecordEntity> findPostponedTodoByOperatorId(SearchRequest searchRequest) {
        User user = userRepository.getUserByUsername(TokenContext.current().getUsername());
        String lastId = searchRequest.getParameter("lastId");
        SQLBuilder sqlBuilder = new SQLBuilder("from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.postponedCount > 0 ");
        sqlBuilder.addParam(user.getId());
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and r.id < ?",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by r.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
        return MultiResponse.of(flowRecordQuery.dynamicPageQuery(sqlBuilder,pageRequest));
    }
}
