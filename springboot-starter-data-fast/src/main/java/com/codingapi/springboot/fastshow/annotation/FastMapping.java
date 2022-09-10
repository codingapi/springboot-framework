package com.codingapi.springboot.fastshow.annotation;


import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ResponseBody
public @interface FastMapping {

    String hql() default "";

    RequestMethod method() default RequestMethod.GET;


    String mapping() default "";

}
