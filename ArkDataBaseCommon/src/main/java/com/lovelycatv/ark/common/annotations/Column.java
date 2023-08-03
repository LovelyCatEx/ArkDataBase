package com.lovelycatv.ark.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Column {
    String columnName();
    boolean primaryKey() default false;
    boolean unique() default false;
    boolean notNull() default false;
    boolean autoIncrease() default false;
}
