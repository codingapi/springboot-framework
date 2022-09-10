package com.codingapi.springboot.fast.annotation;


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

    /**
     * execute jpa hql
     */
    String value() default "";


    /**
     * execute jpa count hql
     */
    String countQuery() default "";

    /**
     * mvc request method
     */
    RequestMethod method() default RequestMethod.GET;

    /**
     * mvc request url
     */
    String mapping() default "";

}
