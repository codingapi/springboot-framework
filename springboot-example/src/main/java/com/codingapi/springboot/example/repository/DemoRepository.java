package com.codingapi.springboot.example.repository;

import com.codingapi.springboot.example.domain.Demo;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface DemoRepository extends JpaRepository<Demo, Integer> {


    @ResponseBody
    default List<Demo> test(@RequestParam("name") String name) {
        Demo demo = new Demo();
        demo.setName(name);
        return findAll(Example.of(demo));
    }

}
