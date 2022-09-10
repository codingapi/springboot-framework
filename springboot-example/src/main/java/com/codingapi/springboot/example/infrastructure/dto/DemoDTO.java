package com.codingapi.springboot.example.infrastructure.dto;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import lombok.Getter;
import lombok.Setter;

public class DemoDTO {

    @Setter
    @Getter
    public static class DemoQuery extends PageRequest {

        private String name;
    }
}
