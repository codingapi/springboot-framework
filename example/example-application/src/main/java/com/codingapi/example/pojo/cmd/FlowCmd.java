package com.codingapi.example.pojo.cmd;

import com.codingapi.example.domain.Leave;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.Getter;
import lombok.Setter;


public class FlowCmd {

    @Setter
    @Getter
    public static class StartFlow{

        private long workId;
        private String advice;
        private Leave leave;


        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }


    @Setter
    @Getter
    public static class SubmitFlow{

        private long recordId;
        private Opinion opinion;
        private Leave leave;


        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }

    @Setter
    @Getter
    public static class SaveFlow{

        private long recordId;
        private Leave leave;


        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }


    @Setter
    @Getter
    public static class RecallFlow{

        private long recordId;


        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }


    @Setter
    @Getter
    public static class TransferFlow{

        private long recordId;

        private long targetUserId;

        private Leave leave;

        private String advice;


        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }


    @Setter
    @Getter
    public static class ReadFlow{

        private long recordId;


        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }


    @Setter
    @Getter
    public static class InterfereFlow{

        private long recordId;

        private Opinion opinion;
        private Leave leave;

        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }



    @Setter
    @Getter
    public static class UrgeFlow{

        private long recordId;


        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }



    @Setter
    @Getter
    public static class PostponedFlow{
        private long recordId;

        private long timeOut;

        public String getUserName() {
            return TokenContext.current().getUsername();
        }
    }


}
