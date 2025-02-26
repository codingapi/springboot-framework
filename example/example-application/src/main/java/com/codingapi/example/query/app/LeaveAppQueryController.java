package com.codingapi.example.query.app;

import com.codingapi.example.infra.entity.LeaveEntity;
import com.codingapi.example.infra.jpa.LeaveEntityRepository;
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
@RequestMapping("/api/app/query/leave")
@AllArgsConstructor
public class LeaveAppQueryController {

    private final LeaveEntityRepository leaveEntityRepository;

    @GetMapping("/list")
    public MultiResponse<LeaveEntity> list(SearchRequest searchRequest){
        String username =  TokenContext.current().getUsername();
        String lastId = searchRequest.getParameter("lastId");

        SQLBuilder sqlBuilder = new SQLBuilder("from LeaveEntity l where 1 =1 ");
        sqlBuilder.append(" and l.username = ? ",username);
        if(StringUtils.hasText(lastId)){
            sqlBuilder.append(" and l.id < ? ",Long.parseLong(lastId));
        }
        sqlBuilder.appendSql(" order by l.id desc ");
        PageRequest pageRequest = PageRequest.of(0, searchRequest.getPageSize());
//        return MultiResponse.empty();
        return MultiResponse.of(leaveEntityRepository.dynamicPageQuery(sqlBuilder,pageRequest));

    }


}
