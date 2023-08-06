package com.lovelycatv.ark.compiler.utils;

import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtils {

    public static List<Class<? extends Annotation>> getArkSQLAnnotations() {
        List<Class<? extends Annotation>> result = new ArrayList<>();
        result.add(Query.class);
        result.addAll(getArkSQLAdapterAnnotations());
        return result;
    }

    public static List<Class<? extends Annotation>> getArkSQLAdapterAnnotations() {
        List<Class<? extends Annotation>> result = new ArrayList<>();
        result.add(Insert.class);
        result.add(Update.class);
        result.add(Delete.class);
        return result;
    }

    public static List<Class<? extends Annotation>> filterAnnotations(Iterable<Class<? extends Annotation>> annotations, Iterable<Class<? extends Annotation>> includes) {
        List<Class<? extends Annotation>> result = new ArrayList<>();
        for (Class<? extends Annotation> annotation : annotations) {
            for (Class<? extends Annotation> include : includes) {
                if (annotation.getName().equals(include.getName())) {
                    result.add(annotation);
                    break;
                }
            }
        }
        return result;
    }
}
