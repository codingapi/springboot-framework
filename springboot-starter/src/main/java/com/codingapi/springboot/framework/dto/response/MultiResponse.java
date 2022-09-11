package com.codingapi.springboot.framework.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Collection;

/**
 * @author lorne
 * @date 2020/12/17
 */
@Setter
@Getter
public class MultiResponse<T> extends Response {

    private Content<T> data;

    public MultiResponse() {
        this.data = new Content<>();
    }

    public static <T> MultiResponse<T> of(Collection<T> data, long total) {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(true);
        multiResponse.getData().setTotal(total);
        multiResponse.getData().setList(data);
        return multiResponse;
    }

    public static <T> MultiResponse<T> of(Page<T> page) {
        return of(page.getContent(), page.getTotalElements());
    }

    public static <T> MultiResponse<T> empty() {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(true);
        return multiResponse;
    }

    public static <T> MultiResponse<T> of(Collection<T> data) {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(true);
        long total = 0;
        if (data != null) {
            total = data.size();
        }
        multiResponse.getData().setTotal(total);
        multiResponse.getData().setList(data);
        return multiResponse;
    }

    @Setter
    @Getter
    public static class Content<T> {
        private long total;
        private Collection<T> list;
    }


}
