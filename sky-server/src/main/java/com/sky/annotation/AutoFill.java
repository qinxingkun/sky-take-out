package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Target 是一个元注解（即注解的注解），它用于指定注解可以应用到哪些 Java 元素上。
 * @Retention 也是一个元注解，用于指定注解的生命周期。
 * RetentionPolicy.RUNTIME 表示该注解会在运行时保留，并且可以通过反射访问。
 * 。这意味着这个注解不仅在源代码中存在，还会被编译到字节码中，程序运行时仍然可以读取该注解。
 * 选择 RUNTIME 而不是 SOURCE 或 CLASS，是希望在运行时对带有该注解的方法进行特定的处理，例如自动填充数据库字段等。
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型: UPDATE INSERT
    OperationType value();
}
