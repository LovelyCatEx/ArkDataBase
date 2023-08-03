package com.lovelycatv.ark.compiler.utils;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class APTools {

    public static boolean containsAnnotation(Element element, Class<? extends Annotation>... annotations) {
        boolean found = false;
        for (Class<? extends Annotation> annotation : annotations) {
            if (element.getAnnotation(annotation) != null) {
                found = true;
                break;
            }
        }
        return found;
    }

    public static List<Element> getAbstractMethods(Element classElement) {
        List<Element> result = new ArrayList<>();
        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                if (element.getModifiers().contains(Modifier.PUBLIC) && element.getModifiers().contains(Modifier.ABSTRACT)) {
                    result.add(element);
                }
            }
        }
        return result;
    }


    public static boolean isVoid(TypeMirror typeMirror) {
        return typeMirror.toString().equals("void") || typeMirror.toString().equals(Void.class.getName());
    }

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

    public static String getClassNameFromTypeMirror(TypeMirror typeMirror) {
        return typeMirror.toString()
                .replace("(","")
                .replace(")","");
    }

    public static boolean isTheSameTypeMirror(TypeMirror a, TypeMirror b) {
        return getClassNameFromTypeMirror(a).equals(getClassNameFromTypeMirror(b));
    }
}
