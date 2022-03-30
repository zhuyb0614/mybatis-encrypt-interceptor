package com.github.zhuyb0614.mei.anno;

import java.lang.annotation.*;

/**
 * 加密字段注解
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {
    String sourceFiledName();

    Class sourceFiledType() default String.class;

    String sourceFiledSetMethod() default "";

    String sourceFiledGetMethod() default "";
}
