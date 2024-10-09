package com.codingapi.example.pojo;

import lombok.Getter;
import lombok.Setter;

public class FlowRequest {


    @Setter
    @Getter
    public static class CreateRequest {
        private long flowWorkId;
        private String desc;
        private String startDate;
        private String endDate;
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
