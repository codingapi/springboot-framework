package com.codingapi.springboot.framework.event;

import java.lang.annotation.*;


/**
 * handler bean 注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Handler {


}
