package com.codingapi.springboot.example.insfrastructure.query;


import com.codingapi.springboot.example.insfrastructure.entity.DemoEntity;
import com.codingapi.springboot.example.insfrastructure.jap.repository.DemoEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Repository
@AllArgsConstructor
public class DemoQueryApi {

    private final DemoEntityRepository demoEntityRepository;


    @ResponseBody
    public List<DemoEntity> findByName(@RequestParam("name") String name) {
        DemoEntity entity = new DemoEntity();
        entity.setName(name);
        return demoEntityRepository.findAll(Example.of(entity));
    }
}
