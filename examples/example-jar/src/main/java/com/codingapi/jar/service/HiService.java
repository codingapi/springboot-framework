package com.codingapi.jar.service;

import com.codingapi.jar.entity.Test;
import com.codingapi.jar.repository.TestRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HiService {

    private final TestRepository testRepository;

    public List<Test> hi(){
        return testRepository.findAll();
    }


    public void init(){
        Test test = new Test();
        test.setName("test");
        testRepository.save(test);
    }

}
