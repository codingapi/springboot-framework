package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.entity.Demo;
import com.codingapi.springboot.fast.jpa.SQLBuilder;
import com.codingapi.springboot.fast.repository.DemoRepository;
import com.codingapi.springboot.framework.dto.request.Filter;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.Relation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
public class DemoRepositoryTest {

    @Autowired
    private DemoRepository demoRepository;


    @Test
    void test() {
        demoRepository.deleteAll();
        Demo demo = new Demo();
        demo.setName("123");
        demoRepository.save(demo);
        assertTrue(demo.getId() > 0);
    }


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

    @Test
    void pageRequestIsNull() {
        demoRepository.deleteAll();
        Demo demo1 = new Demo();
        demo1.setName("123");
        demo1 = demoRepository.save(demo1);

        Demo demo2 = new Demo();
        demoRepository.save(demo2);

        PageRequest request = new PageRequest();
        request.setCurrent(1);
        request.setPageSize(10);
        request.addFilter("name", Relation.IS_NULL);

        Page<Demo> page = demoRepository.pageRequest(request);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void pageRequestIsNotNull() {
        demoRepository.deleteAll();
        Demo demo1 = new Demo();
        demo1.setName("123");
        demo1 = demoRepository.save(demo1);

        Demo demo2 = new Demo();
        demoRepository.save(demo2);

        PageRequest request = new PageRequest();
        request.setCurrent(1);
        request.setPageSize(10);
        request.addFilter("name", Relation.IS_NOT_NULL);

        Page<Demo> page = demoRepository.pageRequest(request);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void pageRequestNotEqual() {
        demoRepository.deleteAll();
        Demo demo1 = new Demo();
        demo1.setName("123");
        demo1 = demoRepository.save(demo1);

        Demo demo2 = new Demo();
        demo2.setName("456");
        demoRepository.save(demo2);

        PageRequest request = new PageRequest();
        request.setCurrent(1);
        request.setPageSize(10);
        request.addFilter("id", Relation.NOT_EQUAL, demo1.getId());

        Page<Demo> page = demoRepository.pageRequest(request);
        assertEquals(1, page.getTotalElements());

    }


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
        request.addFilter("name", Relation.LIKE, "%2%");

        Page<Demo> page = demoRepository.pageRequest(request);
        assertEquals(1, page.getTotalElements());
    }


    @Test
    void customInSearch() {
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

        request.addFilter("id", Relation.IN, 1, 2, 3);

        Page<Demo> page = demoRepository.pageRequest(request);
        System.out.println(page.getContent());
    }

    @Test
    void customNotInSearch() {
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

        request.addFilter("id", Relation.NOT_IN, 3);

        Page<Demo> page = demoRepository.pageRequest(request);
        assertEquals(2, page.getTotalElements());
    }


    @Test
    void customOrSearch() {
        demoRepository.deleteAll();
        Demo demo1 = new Demo();
        demo1.setName("123");
        demoRepository.save(demo1);

        Demo demo2 = new Demo();
        demo2.setName("456");
        demoRepository.save(demo2);

        PageRequest request = new PageRequest();
        request.setCurrent(0);
        request.setPageSize(10);


//        request.andFilter(Filter.as("id", Relation.IN, 1, 2, 3), Filter.as("name", "123"));
        request.addFilter("name", "456").orFilters(Filter.as("id", Relation.IN, 1, 2, 3), Filter.as("name", "123"));

        request.addSort(Sort.by("id").descending());



        Page<Demo> page = demoRepository.pageRequest(request);
        log.info("demo:{}", page.getContent());
//        assertEquals(2, page.getTotalElements());
    }

    @Test
    void dynamicListQuery() {
        demoRepository.deleteAll();
        Demo demo1 = new Demo();
        demo1.setName("123");
        demoRepository.save(demo1);

        Demo demo2 = new Demo();
        demo2.setName("456");
        demoRepository.save(demo2);

        SQLBuilder builder = new SQLBuilder("from Demo where 1=1");
        String search = "12";
        builder.append("and name like ?","%"+search+"%");

        List<Demo> list = demoRepository.dynamicListQuery(builder);
        assertEquals(1, list.size());
    }



    @Test
    void dynamicNativeListQuery() {
        demoRepository.deleteAll();
        Demo demo1 = new Demo();
        demo1.setName("123");
        demoRepository.save(demo1);

        Demo demo2 = new Demo();
        demo2.setName("456");
        demoRepository.save(demo2);

        SQLBuilder builder = new SQLBuilder(Demo.class,"select * from t_demo where 1=1");
        String search = "12";
        builder.append("and name like ?","%"+search+"%");

        List<Demo> list = demoRepository.dynamicNativeListQuery(builder);
        assertEquals(1, list.size());
    }


    @Test
    void dynamicPageQuery() {
        demoRepository.deleteAll();
        Demo demo1 = new Demo();
        demo1.setName("123");
        demoRepository.save(demo1);

        Demo demo2 = new Demo();
        demo2.setName("456");
        demoRepository.save(demo2);

        SQLBuilder builder = new SQLBuilder(Demo.class,"select d from Demo d where 1=1","select count(1) from Demo d where 1=1");
        String search = "12";
        builder.append("and d.name like ?","%"+search+"%");

        Page<Demo> page = demoRepository.dynamicPageQuery(builder,PageRequest.of(1, 2));
        assertEquals(1, page.getTotalElements());
    }


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
        request.setCurrent(0);
        request.setPageSize(10);

        request.addSort(Sort.by("id").descending());
        Page<Demo> page = demoRepository.findAll(request);
        assertEquals(page.getContent().get(0).getName(), "456");
        assertEquals(2, page.getTotalElements());
    }

}
