package com.codingapi.springboot.framework.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArithmeticTest {

    @Test
    void test() {
        // 1 + 1 x 3 / 4 = 1.25
        assertEquals(Arithmetic.one().add(1).mul(3).div(4).getDoubleValue(),1.5);

        // 0.1+0.2=0.3
        assertEquals(Arithmetic.parse(0.1).add(0.2).getDoubleValue(),0.3);

        // (1.5 x 3.1) + (3.4 x 4.5) = 19.95
        assertEquals((Arithmetic.parse(1.5).mul(3.1)).add(Arithmetic.parse(3.4).mul(4.5)).getDoubleValue(),19.95);
    }
}