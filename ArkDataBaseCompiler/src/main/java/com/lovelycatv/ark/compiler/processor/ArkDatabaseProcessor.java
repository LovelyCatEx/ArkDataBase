package com.lovelycatv.ark.compiler.processor;

import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.processor.relational.children.DatabaseProcessor;

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
public class ArkDatabaseProcessor extends AbstractProcessor {
    private ProcessingEnvironment processingEnvironment;
    private RoundEnvironment roundEnvironment;
    private Filer filer;
    private Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Database.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnvironment = processingEnv;
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.roundEnvironment = roundEnv;

        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Database.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                continue;
            }
            Database database = annotatedElement.getAnnotation(Database.class);
            try {
                if (database.dataBaseType().type == DataBaseType.Type.RELATIONAL) {
                    new DatabaseProcessor(this).analysis(annotatedElement);
                } else {
                    throw new ProcessorError("Not support other database currently...");
                }
            } catch (PreProcessUnexpectedError | PreProcessException | ProcessorError e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    public void error(Element e, String msg, Object... args) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    public void info(Element e, String msg, Object... args) {
        this.messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), e);
    }

    public Filer getFiler() {
        return filer;
    }

    public Messager getMessager() {
        return messager;
    }

    public RoundEnvironment getRoundEnvironment() {
        return roundEnvironment;
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnvironment;
    }
}
