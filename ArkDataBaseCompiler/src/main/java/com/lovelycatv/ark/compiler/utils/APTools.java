package com.lovelycatv.ark.compiler.utils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class APTools {
    /**
     * @param targetElement get annotation from the target element
     * @param annotation target annotation
     * @param fieldName field name in annotation
     * @param isArray if the field in annotation is an array, set this parameter to true
     * @return List of declaredTypes in Class<?>[] or Class<?>
     */
    public static List<DeclaredType> getClassArrayFromAnnotation(Element targetElement, Class<? extends Annotation> annotation, String fieldName, boolean isArray) {
        AnnotationMirror mirror = getTargetAnnotationMirror(targetElement, annotation);
        if (mirror == null || fieldName == null || "".equals(fieldName)) {
            return null;
        }

        List<DeclaredType> result = new ArrayList<>();
        if (isTheSameAnnotation(mirror.getAnnotationType().asElement().asType(), annotation)) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValuesMap = mirror.getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationValuesMap.entrySet()) {
                if (entry.getKey().getSimpleName().toString().equals(fieldName)) {
                    if (isArray) {
                        List<? extends AnnotationValue> values = (List<? extends AnnotationValue>) entry.getValue().getValue();
                        for (AnnotationValue value : values) {
                            result.add((DeclaredType) value.getValue());
                        }
                    } else {
                        result.add((DeclaredType) entry.getValue().getValue());
                    }
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @param typeMirror typeMirror
     * @param annotation annotation
     * @return whether the typeMirror is the annotation
     */
    public static boolean isTheSameAnnotation(TypeMirror typeMirror, Class<? extends Annotation> annotation) {
        return typeMirror.toString().equals(annotation.getName());
    }

    /**
     * @param element target element
     * @param annotation annotation
     * @return target annotation mirror
     */
    public static AnnotationMirror getTargetAnnotationMirror(Element element, Class<? extends Annotation> annotation) {
        if (element == null || annotation == null) {
            return null;
        }
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (isTheSameAnnotation(mirror.getAnnotationType().asElement().asType(), annotation)) {
                return mirror;
            }
        }
        return null;
    }
}
