package com.codingapi.springboot.authorization.mask.impl;

import com.codingapi.springboot.authorization.mask.ColumnMask;

import java.util.regex.Pattern;

/**
 * 银行卡脱敏
 */
public class BankCardMask implements ColumnMask {

    private final static Pattern BANK_CARD_MATCHER_PATTERN = Pattern.compile("^\\d{13,19}$");
    private final static Pattern BANK_CARD_MASK_PATTERN = Pattern.compile("(\\d{6})\\d{3,9}(\\d{4})");

    @Override
    public boolean support(Object value) {
        if (value instanceof String) {
            return BANK_CARD_MATCHER_PATTERN.matcher((String) value).matches();
        }
        return false;
    }

    @Override
    public Object mask(Object value) {
        if (value instanceof String) {
            String bankCard = (String) value;
            int length = bankCard.length();
            // 获取字符串的长度
            int maskLength = length - 10;

            // 手动构造星号部分
            StringBuilder maskedPart = new StringBuilder(maskLength);
            for (int i = 0; i < maskLength; i++) {
                maskedPart.append("*");
            }

            // 用构造好的星号替换原始字符串中的部分内容
            return BANK_CARD_MASK_PATTERN.matcher((String) value)
                    .replaceAll("$1" + maskedPart + "$2");
        }
        return value;
    }
}
