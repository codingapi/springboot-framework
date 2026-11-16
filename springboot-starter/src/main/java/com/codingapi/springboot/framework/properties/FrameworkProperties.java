package com.codingapi.springboot.framework.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FrameworkProperties {

    /**
     *  事件异步线程数量，默认20
     */
    private int handlerThreadPoolSize = 20;

    /**
     * 事件相关配置。
     */
    private Event event = new Event();

    @Setter
    @Getter
    public static class Event {

        private Async async = new Async();
    }

    @Setter
    @Getter
    public static class Async {

        /**
         * 同一 trace 内的异步事件是否按推送顺序串行执行，默认 true。
         * 关闭后退回完全并发行为，适合无顺序依赖且追求极致吞吐的场景。
         */
        private boolean ordered = true;
    }

}
