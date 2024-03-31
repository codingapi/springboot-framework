package com.codingapi.springboot.security.redis;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SecurityRedisProperties {


    /**
     * 是否启用redis
     */
    private boolean enable = true;

    /**
     * 15分钟有效期 1000*60*15=900000
     */
    private int validTime = 900000;

    /**
     * 10分钟后更换令牌 1000*60*10=600000
     */
    private int restTime = 600000;


}
