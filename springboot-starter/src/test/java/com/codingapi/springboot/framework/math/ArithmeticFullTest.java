package com.codingapi.springboot.framework.math;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Arithmetic 全量重载方法单元测试
 */
class ArithmeticFullTest {

    @Test
    void factoriesAndConstructors() {
        assertEquals(0, Arithmetic.zero().getIntValue());
        assertEquals(1, Arithmetic.one().getIntValue());

        assertEquals(5, Arithmetic.parse(5).getIntValue());
        assertEquals(5, Arithmetic.parse(5L).getIntValue());
        assertEquals(1.5, Arithmetic.parse(1.5d).getDoubleValue(), 0.0001);
        assertEquals(1.5, Arithmetic.parse(1.5f).getDoubleValue(), 0.0001);
        assertEquals(2.5, Arithmetic.parse("2.5").getDoubleValue(), 0.0001);

        assertEquals(3, new Arithmetic(3).getIntValue());
        assertEquals(3, new Arithmetic(3L).getIntValue());
        assertEquals(0.5, new Arithmetic(0.5d).getDoubleValue(), 0.0001);
        assertEquals(0.5, new Arithmetic(0.5f).getDoubleValue(), 0.0001);
        assertEquals(4.5, new Arithmetic("4.5").getDoubleValue(), 0.0001);
    }

    @Test
    void addOverloads() {
        assertEquals(3, Arithmetic.one().add(Arithmetic.parse(2)).getIntValue());
        assertEquals(3, Arithmetic.one().add("2").getIntValue());
        assertEquals(3, Arithmetic.one().add(2).getIntValue());
        assertEquals(3, Arithmetic.one().add(2.0d).getIntValue());
        assertEquals(3, Arithmetic.one().add(2.0f).getIntValue());
        assertEquals(3, Arithmetic.one().add(2L).getIntValue());
    }

    @Test
    void subOverloads() {
        assertEquals(1, Arithmetic.parse(3).sub(Arithmetic.parse(2)).getIntValue());
        assertEquals(1, Arithmetic.parse(3).sub("2").getIntValue());
        assertEquals(1, Arithmetic.parse(3).sub(2).getIntValue());
        assertEquals(1, Arithmetic.parse(3).sub(2.0d).getIntValue());
        assertEquals(1, Arithmetic.parse(3).sub(2.0f).getIntValue());
        assertEquals(1, Arithmetic.parse(3).sub(2L).getIntValue());
    }

    @Test
    void mulOverloads() {
        assertEquals(6, Arithmetic.parse(3).mul(Arithmetic.parse(2)).getIntValue());
        assertEquals(6, Arithmetic.parse(3).mul("2").getIntValue());
        assertEquals(6, Arithmetic.parse(3).mul(2).getIntValue());
        assertEquals(6, Arithmetic.parse(3).mul(2.0d).getIntValue());
        assertEquals(6, Arithmetic.parse(3).mul(2.0f).getIntValue());
        assertEquals(6, Arithmetic.parse(3).mul(2L).getIntValue());
    }

    @Test
    void divOverloads() {
        assertEquals(3, Arithmetic.parse(6).div(Arithmetic.parse(2)).getIntValue());
        assertEquals(3, Arithmetic.parse(6).div("2").getIntValue());
        assertEquals(3, Arithmetic.parse(6).div(2).getIntValue());
        assertEquals(3, Arithmetic.parse(6).div(2.0d).getIntValue());
        assertEquals(3, Arithmetic.parse(6).div(2.0f).getIntValue());
        assertEquals(3, Arithmetic.parse(6).div(2L).getIntValue());
    }

    @Test
    void valueGetters() {
        Arithmetic value = Arithmetic.parse("12.34");
        assertEquals(new BigDecimal("12.34"), value.getValue());
        assertEquals("12.34", value.getStringValue());
        assertEquals(12, value.getIntValue());
        assertEquals(12L, value.getLongValue());
        assertEquals(12.34, value.getDoubleValue(), 0.0001);
        assertEquals(12.34f, value.getFloatValue(), 0.0001);
        assertEquals(12, value.getBigIntegerValue().intValue());
    }

    @Test
    void halfUpScale() {
        // 1.005 保留两位四舍五入 -> 1.01(half up)
        assertEquals("1.01", Arithmetic.parse("1.005").halfUpScale2().getStringValue());
        assertEquals("1.005", Arithmetic.parse("1.0051").halfUpScale(3).getStringValue());
        assertEquals("3.14", Arithmetic.parse("3.14159").halfUpScale2().getStringValue());
    }

    @Test
    void chainedCalculation() {
        // ((10 - 2) x 3) / 4 = 6
        assertEquals(6, Arithmetic.parse(10).sub(2).mul(3).div(4).getIntValue());
        // 0.1 + 0.2 精确等于 0.3
        assertEquals("0.3", Arithmetic.parse("0.1").add("0.2").getStringValue());
    }
}
