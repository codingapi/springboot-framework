package com.codingapi.springboot.example.controller;

import com.codingapi.springboot.example.domain.Demo;
import com.codingapi.springboot.example.repository.DemoRepository;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/open")
@RestController
public class OpenController {

    private final DemoRepository demoRepository;

    @GetMapping("/list")
    public MultiResponse<Demo> findAll(PageRequest request){
        return MultiResponse.of(demoRepository.findAll(request));
    }

}
