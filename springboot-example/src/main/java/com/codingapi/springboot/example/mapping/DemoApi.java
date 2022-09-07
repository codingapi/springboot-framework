package com.codingapi.springboot.example.mapping;

import com.codingapi.springboot.example.domain.Demo;
import com.codingapi.springboot.example.repository.DemoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Component
@AllArgsConstructor
public class DemoApi {

    private final DemoRepository demoRepository;


    @ResponseBody
    public List<Demo> findByName(@RequestParam("name") String name) {
        Demo demo = new Demo();
        demo.setName(name);
        Example.of(demo);
        return demoRepository.findAll();
    }
}
