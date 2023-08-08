package com.lovelycatv.ark.compiler.utils;

import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;

public class APTools {

    public static ProcessingEnvironment processingEnvironment;

    public static List<? extends TypeMirror> getParameterizedType(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            return declaredType.getTypeArguments();
        }
        return null;
    }

    public static boolean isContains(TypeMirror a, TypeMirror b) {
        return processingEnvironment.getTypeUtils().contains(a, b);
    }

    public static boolean isSubtype(TypeMirror a, TypeMirror b) {
        return processingEnvironment.getTypeUtils().isSubtype(a, b);
    }

    /**
     * @param a from
     * @param b to
     * @return can a be assigned to b
     */
    public static boolean isAssignable(TypeMirror a, TypeMirror b) {
        return processingEnvironment.getTypeUtils().isAssignable(a, b);
    }

    public static TypeElement getTypeByName(String name) {
        return processingEnvironment.getElementUtils().getTypeElement(name);
    }

    public static TypeElement getTypeByClass(Class<?> aClass) {
        return getTypeByName(aClass.getName());
    }

    public static boolean isListWithoutParameterizedTypes(TypeMirror typeMirror) {
        String className = APTools.getClassNameFromTypeMirror(typeMirror);
        List<? extends TypeMirror> parameterizedType = getParameterizedType(typeMirror);
        if (parameterizedType != null) {
            className = className.replace("<","");
            className = className.replace(">","");
            className = className.replace(",","");
            for (TypeMirror mirror : parameterizedType) {
                className = className.replace(APTools.getClassNameFromTypeMirror(mirror),"");
            }
        }
        List<String> tmp = new ArrayList<>();
        tmp.add(List.class.getName());
        tmp.add(ArrayList.class.getName());
        tmp.add(LinkedList.class.getName());
        tmp.add(Vector.class.getName());
        return tmp.contains(className);
    }

    /**
     * @param element type method or field
     * @param annotations annotation(s) to be checked
     * @return whether the element is annotated with the annotation(s)
     */
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

    /**
     * @param classElement type
     * @return all public abstract methods in element
     */
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


    /**
     * @param typeMirror typeMirror
     * @return whether the type of typeMirror is null
     */
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

    /**
     * @param a toString() = java.util.List<com.lovelycatv.ark.test.User>
     * @param b toString() = com.lovelycatv.ark.test.User OR java.util.List
     * @return true
     */
    public static boolean isTheSimilarTypeMirror(TypeMirror a, TypeMirror b) {
        return getClassNameFromTypeMirror(a).contains(getClassNameFromTypeMirror(b));
    }

    /**
     * @param fieldElement targetFieldElement
     * @param elements all elements in the class where the target field are
     * @return
     */
    public static List<Element> findBeanMethod(Element fieldElement, List<? extends Element> elements) {
        List<Element> result = new ArrayList<>();
        for (Element methodElement : elements) {
            if (methodElement.getKind() == ElementKind.METHOD && methodElement.getModifiers().contains(Modifier.PUBLIC)) {
                if (!methodElement.getModifiers().contains(Modifier.STATIC)) {
                    if (methodElement instanceof ExecutableElement) {
                        ExecutableElement i = (ExecutableElement) methodElement;
                        if (i.getParameters() == null) {
                            continue;
                        }
                        String methodName = methodElement.getSimpleName().toString();
                        int parameterCount = i.getParameters().size();
                        if (parameterCount == 0) {
                            // Maybe getter
                            if (!methodName.startsWith("get")) {
                                continue;
                            }
                            methodName = methodName.substring(3);
                        } else if (parameterCount == 1) {
                            // Maybe setter or boolean is
                            VariableElement param = i.getParameters().get(0);
                            if (!methodName.startsWith("set") && !methodName.startsWith("is")) {
                                continue;
                            }
                            methodName = methodName.substring(methodName.startsWith("set") ? 3 : 2);

                            if (!APTools.isTheSameTypeMirror(param.asType(), fieldElement.asType())) {
                                continue;
                            }
                        } else {
                            continue;
                        }

                        if (StringX.getJavaBeanMethodName(fieldElement.getSimpleName().toString(), false, false, true)
                                .equals(methodName)) {
                            result.add(methodElement);
                        }

                    }
                }
            }
        }

        return result;
    }
}
