package com.codingapi.springboot.security.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LoginResponse {

    private String username;
    private String token;
    private List<String> authorities;

}
