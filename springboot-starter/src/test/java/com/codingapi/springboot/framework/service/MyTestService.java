package com.codingapi.springboot.framework.service;

import com.codingapi.springboot.framework.entity.MyTest;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.framework.event.MyTestAsyncEvent;
import com.codingapi.springboot.framework.event.MyTestSyncEvent;
import com.codingapi.springboot.framework.repository.MyTestRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MyTestService {

    private final MyTestRepository testRepository;

    @Transactional
    public void save1(){
        MyTest myTest = new MyTest();
        myTest.setName("test");
        testRepository.save(myTest);
        EventPusher.push(new MyTestSyncEvent(myTest));
        EventPusher.push(new MyTestAsyncEvent(myTest));
        int value = 100 / 0;
        System.out.println(value);
    }

    @Transactional
    public void save2(){
        MyTest myTest = new MyTest();
        myTest.setName("test");
        testRepository.save(myTest);
        EventPusher.push(new MyTestSyncEvent(myTest));
        EventPusher.push(new MyTestAsyncEvent(myTest));
    }
}
