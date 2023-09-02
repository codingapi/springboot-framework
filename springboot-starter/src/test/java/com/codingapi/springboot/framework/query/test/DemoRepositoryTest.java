package com.codingapi.springboot.framework.query.test;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.query.entity.Demo;
import com.codingapi.springboot.framework.query.repository.DemoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DemoRepositoryTest {

    @Autowired
    private DemoRepository demoRepository;


    @Test
    void test(){
        demoRepository.deleteAll();
        Demo demo = new Demo();
        demo.setName("123");
        demoRepository.save(demo);
        assertTrue(demo.getId()>0);
    }


    @Test
    void query(){
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

        request.addFilter("name","123");

        Example<Demo> demo = request.getExample(Demo.class);
        System.out.println(demo);
        Page<Demo> page =  demoRepository.findAll(demo,request);
        assertEquals(1, page.getTotalElements());
    }


    @Test
    void sort(){
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
        Page<Demo> page =  demoRepository.findAll(request);
        assertEquals(page.getContent().get(0).getName(),"456");
        assertEquals(2, page.getTotalElements());
    }
}
