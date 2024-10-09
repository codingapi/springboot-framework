package com.codingapi.example.controller;

import com.codingapi.example.domain.User;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import com.codingapi.springboot.framework.dto.response.MultiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/list")
    public MultiResponse<User> list(SearchRequest request) {
        return MultiResponse.of(userRepository.list(request));
    }

}
