package com.codingapi.example.pojo;

import com.codingapi.example.domain.Leave;
import lombok.Getter;
import lombok.Setter;

public class FlowRequest {


    @Setter
    @Getter
    public static class CreateRequest{
        private long flowWorkId;
        private Leave leave;
    }

    @Setter
    @Getter
    public static class BuildRequest {
        private long id;
        private String title;
        private String description;
    }

    @Setter
    @Getter
    public static class SchemaRequest {
        private long id;
        private String schema;
    }
}
