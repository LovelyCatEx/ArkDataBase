package com.lovelycatv.ark.common.annotations;

import com.lovelycatv.ark.common.enums.DataBaseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Database {
    String FILED_DATABASE_TYPE = "dataBaseType";
    String FILED_ENTITIES = "entities";
    String FILED_TYPE_CONVERTERS = "typeConverters";
    String FILED_VERSION = "version";
    DataBaseType dataBaseType();
    Class<?>[] entities();
    Class<?>[] typeConverters() default {};
    int version() default 1;
}
