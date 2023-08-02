package com.lovelycatv.ark.compiler.processor;

import com.lovelycatv.ark.runtime.annotations.DataBase;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Set;

@SupportedAnnotationTypes("com.lovelycatv.ark.runtime.annotations.DataBase")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ArkDataBaseProcessor extends AbstractProcessor {
    private ProcessingEnvironment mProcessingEnvironment;
    private Filer filer;
    private Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(DataBase.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.mProcessingEnvironment = processingEnv;
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(DataBase.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                continue;
            }
            DataBase dataBaseAnnotation = annotatedElement.getAnnotation(DataBase.class);
            if (dataBaseAnnotation == null) {
                error(annotatedElement, "");
            }

        }

        return false;
    }

    public void error(Element e, String msg, Object... args) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }
}
