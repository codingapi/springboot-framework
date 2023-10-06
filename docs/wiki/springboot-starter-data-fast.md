springboot-starter-data-fast

基于JPA的快速API能力服务

## FastController  快速API能力服务
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

## FastRepository 的使用教程


继承FastRepository接口，实现自定义的接口，即可使用FastRepository的能力
```java


import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.query.FastRepository;

public interface DemoRepository extends FastRepository<Demo,Integer> {

}


```
动态FastRepository的能力展示

```java

    // 重写findAll，通过Example查询 
    @Test
    void findAll() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);
            request.addFilter("name", "123");

            Page<Demo> page = demoRepository.findAll(request);
            assertEquals(1, page.getTotalElements());
        }


    // pageRequest 自定义条件查询        
    @Test
    void pageRequest() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);
            request.addFilter("name", PageRequest.FilterRelation.LIKE, "%2%");

            Page<Demo> page = demoRepository.pageRequest(request);
            assertEquals(1, page.getTotalElements());
        }


    // 动态sql的List查询    
    @Test
    void dynamicListQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            List<Demo> list = demoRepository.dynamicListQuery("from Demo where name = ?1", "123");
            assertEquals(1, list.size());
        }


    // 动态sql的分页查询        
    @Test
    void dynamicPageQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            Page<Demo> page = demoRepository.dynamicPageQuery("from Demo where name = ?1", PageRequest.of(1, 2), "123");
            assertEquals(1, page.getTotalElements());
        }

    // 增加排序查询
    @Test
    void sortQuery() {
            demoRepository.deleteAll();
            Demo demo1 = new Demo();
            demo1.setName("123");
            demoRepository.save(demo1);

            Demo demo2 = new Demo();
            demo2.setName("456");
            demoRepository.save(demo2);

            PageRequest request = new PageRequest();
            request.setCurrent(1);
            request.setPageSize(10);

            request.addSort(Sort.by("id").descending());
            Page<Demo> page = demoRepository.findAll(request);
            assertEquals(page.getContent().get(0).getName(), "456");
            assertEquals(2, page.getTotalElements());
        }

```
## SortRepository的使用教程

```java

public interface DemoRepository extends FastRepository<Demo,Integer>, SortRepository<Demo,Integer> {

}

```

SortRepository的能力展示

```java

    @Test
    @Transactional
    void pageSort() {
        demoRepository.deleteAll();
        Demo demo1 = new Demo();
        demo1.setName("123");
        demoRepository.save(demo1);

        Demo demo2 = new Demo();
        demo2.setName("456");
        demoRepository.save(demo2);

        List<Integer> ids = Arrays.asList(demo1.getId(), demo2.getId());
        System.out.println(ids);
        demoRepository.pageSort(PageRequest.of(1, 10), ids);

        Demo newDemo1 = demoRepository.getReferenceById(demo1.getId());
        Demo newDemo2 = demoRepository.getReferenceById(demo2.getId());

        assertEquals(newDemo2.getSort(), 1);
        assertEquals(newDemo1.getSort(), 0);
    }

```