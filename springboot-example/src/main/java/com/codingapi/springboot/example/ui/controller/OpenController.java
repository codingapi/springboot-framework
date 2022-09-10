package com.codingapi.springboot.example.ui.controller;

import com.codingapi.springboot.example.application.executor.DemoExecutor;
import com.codingapi.springboot.example.infrastructure.entity.DemoEntity;
import com.codingapi.springboot.example.infrastructure.jap.repository.DemoEntityRepository;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/demo")
@AllArgsConstructor
public class OpenController {

    private final DemoExecutor executor;

    private final DemoEntityRepository demoEntityRepository;

    @GetMapping("/save")
    public Response save(@RequestParam("name") String name) {
        executor.create(name);
        return Response.buildSuccess();
    }

    @GetMapping("/findAll")
    public MultiResponse<DemoEntity> findAll(PageRequest pageRequest) {
        return MultiResponse.of(demoEntityRepository.findAll(pageRequest));
    }
}
