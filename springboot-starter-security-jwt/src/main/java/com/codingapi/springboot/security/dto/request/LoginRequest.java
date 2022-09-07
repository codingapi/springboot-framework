package com.codingapi.springboot.security.dto.request;

import lombok.ToString;

import java.util.HashMap;

@ToString
public class LoginRequest extends HashMap<String, Object> {

    public boolean isEmpty() {
        return getPassword() == null || getUsername() == null;
    }

    public String getPassword() {
        return getString("password");
    }

    public String getUsername() {
        return getString("username");
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return (boolean) get(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String getString(String key) {
        return (String) get(key);
    }
}
