package com.codingapi.springboot.fast.query;

import com.codingapi.springboot.fast.annotation.FastController;
import com.codingapi.springboot.fast.annotation.FastMapping;
import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import org.springframework.web.bind.annotation.RequestParam;

@FastController
public interface FastDemoQuery {

    @FastMapping(value = "select d from Demo d", mapping = "/demo/findAll")
    MultiResponse<Demo> findAll();

    @FastMapping(value = "select d from Demo d where d.id = ?1", mapping = "/demo/getById")
    SingleResponse<Demo> getById(@RequestParam("id") int id);

    @FastMapping(value = "select d from Demo d", countQuery = "select count(d) from Demo d", mapping = "/demo/findPage")
    MultiResponse<Demo> findPage(PageRequest pageRequest);

}
