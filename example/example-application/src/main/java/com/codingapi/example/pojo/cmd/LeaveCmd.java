package com.codingapi.example.pojo.cmd;

import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.Getter;
import lombok.Setter;

public class LeaveCmd {

    @Setter
    @Getter
    public static class StartLeave{
        private String desc;
        private int days;
        private long flowId;


        public String getUsername(){
            return TokenContext.current().getUsername();
        }
    }
}
