package com.codingapi.jar.runner;

import com.codingapi.jar.service.HiService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class HiRunner implements ApplicationRunner {

    private final HiService hiService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        hiService.init();;
    }
}
