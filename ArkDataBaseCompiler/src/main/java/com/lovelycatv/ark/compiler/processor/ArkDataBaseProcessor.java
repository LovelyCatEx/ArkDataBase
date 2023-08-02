package com.lovelycatv.ark.compiler.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("com.lovelycatv.arkdatabase.annotations.DataBase")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ArkDataBaseProcessor extends AbstractProcessor {
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        return false;
    }
}
