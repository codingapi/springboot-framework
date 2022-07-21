package com.codingapi.springboot.example.domain.service;

import com.codingapi.springboot.example.domain.Demo;
import com.codingapi.springboot.example.repository.DemoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class DemoService {

    private final DemoRepository demoRepository;

    public void swap(Demo demo1,Demo demo2){
        String demo1Name = demo1.getName();
        String demo2Name = demo2.getName();

        demo1.changeName(demo2Name);
        demo2.changeName(demo1Name);
    }


    public void save(Demo demo){
        demoRepository.save(demo);
    }

}
