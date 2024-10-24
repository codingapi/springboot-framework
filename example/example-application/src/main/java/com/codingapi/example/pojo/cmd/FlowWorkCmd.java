package com.codingapi.example.pojo.cmd;

import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class FlowWorkCmd {

    @Setter
    @Getter
    public static class CreateRequest{

        private long id;
        private String title;
        private String description;
        private int postponedMax;


        public String getUsername(){
            return TokenContext.current().getUsername();
        }
    }


    @Setter
    @Getter
    @ToString
    public static class SchemaRequest{

        private long id;
        private String schema;


        public String getUsername(){
            return TokenContext.current().getUsername();
        }
    }
}
