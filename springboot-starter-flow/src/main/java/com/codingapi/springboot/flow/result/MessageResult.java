package com.codingapi.springboot.flow.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MessageResult {

    /**
     * 展示的标题
     */
    private String title;
    /**
     * 展示的内容
     */
    private List<Message> items;
    /**
     * 是否可关闭流程
     */
    private boolean closeable;

    public MessageResult addMessage(String label, String value) {
        if (items == null) {
            items = new java.util.ArrayList<>();
        }
        items.add(new Message(label, value));
        return this;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Message {
        private String label;
        private String value;
    }

    public static MessageResult create(String title) {
        return create(title, null, false);
    }


    public static MessageResult create(String title, boolean closeable) {
        return create(title, null, closeable);
    }

    public static MessageResult create(String title, List<Message> items, boolean closeable) {
        MessageResult messageResult = new MessageResult();
        messageResult.setTitle(title);
        messageResult.setItems(items);
        messageResult.setCloseable(closeable);
        return messageResult;
    }
}
