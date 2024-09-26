package com.codingapi.example.domain;

import com.codingapi.example.event.NodeEvent;
import com.codingapi.springboot.framework.em.IEnum;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.framework.serializable.EnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author lorne
 * @since 1.0.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Node implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(columnDefinition = "text")
    private String url;

    @JsonSerialize(using = EnumSerializer.class)
    private State state;

    @SneakyThrows
    public void swap(Node target) {
        Node old = (Node) this.clone();
        this.url = target.getUrl();
        this.name = target.getName();
        EventPusher.push(new NodeEvent(old, this));
    }


    public enum State implements IEnum {
        /**
         * 禁用
         */
        DISABLE(0),
        /**
         * 正常
         */
        ENABLE(1);


        private final int code;

        State(int code) {
            this.code = code;
        }

        @Override
        public int getCode() {
            return code;
        }

        public static State of(int code) {
            for (State state : State.values()) {
                if (state.getCode() == code) {
                    return state;
                }
            }
            return null;
        }
    }

}
