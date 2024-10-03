package com.codingapi.example.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApprovalRequest {

    @Setter
    @Getter
    public static class SubmitRequest{
        private long recordId;
        private String opinion;
        private boolean pass;
    }

    @Setter
    @Getter
    public static class RecallRequest{
        private long recordId;
    }
}
