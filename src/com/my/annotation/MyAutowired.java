package com.my.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.my.dao.CustomerDAO;

@Retention(RUNTIME)
@Target(FIELD)
public @interface MyAutowired {
}
