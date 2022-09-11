package com.codingapi.springboot.generator.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class IdKey {

    private String key;
    private long id;
    private long updateTime;

    public IdKey(String key, long id) {
        this.key = key;
        this.id = id;
        this.updateTime = System.currentTimeMillis();
    }

    public IdKey(String key) {
        this.key = key;
        this.id = 1;
        this.updateTime = System.currentTimeMillis();
    }

    public IdKey(String key, long id, long updateTime) {
        this.key = key;
        this.id = id;
        this.updateTime = updateTime;
    }
}
