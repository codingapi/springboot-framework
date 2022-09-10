package com.codingapi.springboot.example.infrastructure.query;

import com.codingapi.springboot.example.infrastructure.dto.DemoDTO;
import com.codingapi.springboot.example.infrastructure.entity.DemoEntity;
import com.codingapi.springboot.fast.annotation.FastController;
import com.codingapi.springboot.fast.annotation.FastMapping;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import org.springframework.web.bind.annotation.RequestMethod;

@FastController
public interface FastDemoApi {

    @FastMapping(
            method = RequestMethod.GET,
            mapping = "/open/demo/findByName",
            value = "select d from DemoEntity d where name = :name",
            countQuery = "select count(d) from DemoEntity d where name = :name")
    MultiResponse<DemoEntity> findByName(DemoDTO.DemoQuery query);

}
