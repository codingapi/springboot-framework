package com.codingapi.springboot.example.insfrastructure.query;

import com.codingapi.springboot.example.insfrastructure.entity.DemoEntity;
import com.codingapi.springboot.fast.annotation.FastController;
import com.codingapi.springboot.fast.annotation.FastMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FastController
public interface FastDemoApi {

    @FastMapping(method = RequestMethod.GET,mapping = "/open/fast/demo/findAll",hql = "select d from DemoEntity d where name = ?1")
    List<DemoEntity> findAll(@RequestParam("name") String name);

}
