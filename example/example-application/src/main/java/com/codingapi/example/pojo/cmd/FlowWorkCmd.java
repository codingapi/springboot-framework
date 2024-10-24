package com.codingapi.example.pojo.cmd;

import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.Getter;
import lombok.Setter;

public class FlowWorkCmd {

    @Setter
    @Getter
    public static class CreateRequest{

        private long id;
        private String title;
        private String description;


        public String getUsername(){
            return TokenContext.current().getUsername();
        }
    }
}
