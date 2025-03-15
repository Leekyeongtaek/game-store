package com.mrlee.game_store.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP 통신 오류 발생시 재시도 하는 애노테이션
 */
//CLASS 컴파일 후 .class 파일에 남으며, 런타임에 접근 불가능
//RUNTIME 컴파일 후 .class 파일에 남으며, 런타임에도 접근 가능
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRetry {
    int value() default 1;
}
