package com.codingapi.springboot.authorization.mask.impl;

import com.codingapi.springboot.authorization.mask.ColumnMask;

import java.util.regex.Pattern;

/**
 * 身份证脱敏
 */
public class IDCardMask implements ColumnMask {
    private final static Pattern ID_CARD_MATCHER_PATTERN = Pattern.compile("^(\\d{15}|\\d{18}|\\d{17}[Xx])$");
    private final static Pattern ID_CARD_MASK_PATTERN = Pattern.compile("(\\d{6})\\d{8}(\\w{4})");

    @Override
    public boolean support(Object value) {
        if (value instanceof String) {
            return ID_CARD_MATCHER_PATTERN.matcher((String) value).matches();
        }
        return false;
    }

    @Override
    public Object mask(Object value) {
        if (value instanceof String) {
            return ID_CARD_MASK_PATTERN.matcher( (String) value).replaceAll("$1********$2");
        }
        return value;
    }
}
