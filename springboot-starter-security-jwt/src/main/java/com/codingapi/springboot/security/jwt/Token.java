package com.codingapi.springboot.security.jwt;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.serializable.JsonSerializable;
import com.codingapi.springboot.security.crypto.MyAES;
import com.codingapi.springboot.security.exception.TokenExpiredException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Token implements JsonSerializable {

    private String username;
    private String extra;
    private String iv;

    private String token;
    private List<String> authorities;
    private long expireTime;
    private long remindTime;


    public Token(String username, String iv,String extra, List<String> authorities, int expireValue, int remindValue){
        this.username = username;
        this.extra = extra;
        if(iv!=null) {
            this.iv = MyAES.getInstance().encode(iv);
        }
        this.authorities = authorities;
        this.expireTime = System.currentTimeMillis() + expireValue;
        this.remindTime = System.currentTimeMillis() + remindValue;
    }


    public void verify() throws TokenExpiredException {
        if (isExpire()) {
            throw new TokenExpiredException("token expired.");
        }
    }

    public boolean isExpire() {
        return expireTime <= System.currentTimeMillis();
    }

    public String decodeIv(){
        if(iv==null){
            return null;
        }
        return MyAES.getInstance().decode(iv);
    }


    public <T> T parseExtra(Class<T> clazz){
        if(extra==null){
            return null;
        }
        return JSONObject.parseObject(extra,clazz);
    }


    public boolean canRestToken() {
        return !isExpire() && remindTime <= System.currentTimeMillis();
    }


    @Transient
    public UsernamePasswordAuthenticationToken getAuthenticationToken() {
        Collection<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        for (String authority : authorities) {
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        TokenContext.pushExtra(extra);
        return new UsernamePasswordAuthenticationToken(this, iv, simpleGrantedAuthorities);
    }


}
