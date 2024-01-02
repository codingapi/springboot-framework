package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.entity.Demo;
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
        request.andFilter("name", Relation.LIKE, "%2%");

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

        request.andFilter("id", Relation.IN, 1, 2, 3);

        Page<Demo> page = demoRepository.pageRequest(request);
        log.info("demo:{}", page.getContent());
//        assertEquals(2, page.getTotalElements());
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
        request.setCurrent(1);
        request.setPageSize(10);


        request.orFilters(Filter.as("id", Relation.IN, 1, 2, 3), Filter.as("name", "123"));

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

        List<Demo> list = demoRepository.dynamicListQuery("from Demo where name = ?1", "123");
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

        Page<Demo> page = demoRepository.dynamicPageQuery("from Demo where name = ?1", PageRequest.of(1, 2), "123");
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
        request.setCurrent(1);
        request.setPageSize(10);

        request.addSort(Sort.by("id").descending());
        Page<Demo> page = demoRepository.findAll(request);
        assertEquals(page.getContent().get(0).getName(), "456");
        assertEquals(2, page.getTotalElements());
    }

}
