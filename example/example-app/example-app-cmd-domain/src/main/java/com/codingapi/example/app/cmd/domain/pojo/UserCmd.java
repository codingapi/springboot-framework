package com.codingapi.example.app.cmd.domain.pojo;

import lombok.Getter;
import lombok.Setter;

public class UserCmd {

    @Setter
    @Getter
    public static class UpdateRequest{
        private long id;
        private String name;
        private String username;
        private String password;
        private boolean flowManager;

        public boolean hasId(){
            return id > 0;
        }
    }


    @Setter
    @Getter
    public static class EntrustRequest{
        private long id;
        private long entrustUserId;

    }
}
