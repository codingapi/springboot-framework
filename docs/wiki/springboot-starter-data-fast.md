springboot-starter-data-fast

基于JPA的快速API能力服务

```java
package com.codingapi.springboot.example.query;

import com.codingapi.springboot.example.infrastructure.jpa.entity.DemoEntity;
import com.codingapi.springboot.example.infrastructure.jpa.pojo.PageSearch;
import com.codingapi.springboot.fast.annotation.FastController;
import com.codingapi.springboot.fast.annotation.FastMapping;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMethod;

@FastController
public interface FastDemoApi {


    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @FastMapping(
            method = RequestMethod.GET,
            mapping = "/api/demo/findByName1",
            value = "select d from DemoEntity d where name = :name",
            countQuery = "select count(d) from DemoEntity d where name = :name")
    MultiResponse<DemoEntity> findByName1(PageSearch query);



    @PreAuthorize(value = "hasRole('ROLE_USER')")
    @FastMapping(
            method = RequestMethod.GET,
            mapping = "/api/demo/findByName2",
            value = "select d from DemoEntity d where name = :name",
            countQuery = "select count(d) from DemoEntity d where name = :name")
    MultiResponse<DemoEntity> findByName2(PageSearch query);

}

```
@FastController 用于标记当前接口为Fast接口  
@FastMapping 用于标记当前接口的映射关系  
mapping为接口映射路径，method为接口请求方法  
value为查询语句，countQuery为查询总数语句，query为查询参数，支持分页查询，排序查询，查询参数等等  
MultiResponse为返回结果
@PreAuthorize(value = "hasRole('ROLE_USER')") 用于标记当前接口的权限，如果不需要权限可以不用添加

