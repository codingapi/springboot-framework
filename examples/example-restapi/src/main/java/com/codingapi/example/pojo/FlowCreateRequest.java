package com.codingapi.example.pojo;

import com.codingapi.example.domain.Leave;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlowCreateRequest {

    private long flowWorkId;

    private Leave leave;

}
