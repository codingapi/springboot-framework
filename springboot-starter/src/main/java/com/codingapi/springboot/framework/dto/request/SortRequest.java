package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SortRequest {

    private List<Object> ids;

}
