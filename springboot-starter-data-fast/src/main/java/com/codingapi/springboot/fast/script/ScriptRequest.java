package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScriptRequest {

    private final HttpServletRequest request;

    public String getParameter(String key, String defaultValue) {
        String result = request.getParameter(key);
        return result == null ? defaultValue : result;
    }

    public int getParameter(String key, int defaultValue) {
        String result = request.getParameter(key);
        return result == null ? defaultValue : Integer.parseInt(result);
    }

    public float getParameter(String key, float defaultValue) {
        String result = request.getParameter(key);
        return result == null ? defaultValue : Float.parseFloat(result);
    }

    public double getParameter(String key, double defaultValue) {
        String result = request.getParameter(key);
        return result == null ? defaultValue : Double.parseDouble(result);
    }

    public long getParameter(String key, long defaultValue) {
        String result = request.getParameter(key);
        return result == null ? defaultValue : Long.parseLong(result);
    }

    public boolean getParameter(String key, boolean defaultValue) {
        String result = request.getParameter(key);
        return result == null ? defaultValue : Boolean.parseBoolean(result);
    }

    public PageRequest pageRequest(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

}
