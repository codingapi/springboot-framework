package com.codingapi.springboot.framework.event;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class EventPusherTest {

    private final ExecutorService executorService = Executors.newFixedThreadPool(100);

    @Test
    void test() {
        for (int i = 0; i < 100_0000; i++) {
            executorService.execute(() -> {
                try {
                    EventPusher.push(new DemoChangeEvent("before", "current"), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        try {
            Thread.sleep(10000); // 等待所有任务完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
