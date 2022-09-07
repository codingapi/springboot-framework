package com.codingapi.springboot.framework.dto;

import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
public class ResponseTest {

    @Test
    void multi() {
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("234");

        MultiResponse<String> multiResponse = MultiResponse.of(list);
        Assert.isTrue(multiResponse.getData().getList().equals(list), "return data error.");
        Assert.isTrue(multiResponse.getData().getTotal() == list.size(), "return data error.");

    }

    @Test
    void multiSize() {
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("234");

        MultiResponse<String> multiResponse = MultiResponse.of(list, 10);
        Assert.isTrue(multiResponse.getData().getList().equals(list), "return data error.");
        Assert.isTrue(multiResponse.getData().getTotal() == 10, "return data error.");

    }


    @Test
    void single() {
        String data = "123";

        SingleResponse<String> singleResponse = SingleResponse.of(data);
        Assert.isTrue(singleResponse.getData().equals(data), "return data error.");
    }

    @Test
    void success() {
        Response response = Response.buildSuccess();
        Assert.isTrue(response.isSuccess(), "build success error.");
    }

    @Test
    void error() {
        Response response = Response.buildFailure("errorCode", "errorMessage");
        Assert.isTrue(!response.isSuccess(), "build error error.");
        Assert.isTrue(response.getErrCode().equals("errorCode"), "build error error.");
    }
}
