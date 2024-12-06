package com.codingapi.springboot.authorization.mask.impl;

import com.codingapi.springboot.authorization.mask.ColumnMask;

import java.util.regex.Pattern;

/**
 * 电话号码脱敏
 */
public class PhoneMask implements ColumnMask {

    private static final Pattern PHONE_MATCHER_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern PHONE_MASK_PATTERN = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");

    @Override
    public boolean support(Object value) {
        if (value instanceof String) {
            return PHONE_MATCHER_PATTERN.matcher((String) value).matches();
        }
        return false;
    }

    @Override
    public Object mask(Object value) {
        if (value instanceof String) {
            return PHONE_MASK_PATTERN.matcher((String) value).replaceAll("$1****$2");
        }
        return value;
    }

}
