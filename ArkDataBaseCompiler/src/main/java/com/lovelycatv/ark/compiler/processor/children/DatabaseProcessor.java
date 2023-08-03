package com.lovelycatv.ark.compiler.processor.children;

import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.objects.ProcessableDatabase;
import com.lovelycatv.ark.compiler.objects.ProcessableEntity;
import com.lovelycatv.ark.compiler.objects.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.processor.ArkDatabaseProcessor;
import com.lovelycatv.ark.compiler.utils.APTools;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProcessor extends AbstractDatabaseProcessor {
    public DatabaseProcessor(ArkDatabaseProcessor processor) {
        super(processor);
    }

    @Override
    public void analysis(Element annotatedElement) throws PreProcessUnexpectedError {
        Database databaseAnnotation = annotatedElement.getAnnotation(Database.class);
        if (databaseAnnotation == null) {
            throw new PreProcessUnexpectedError("Cannot find @DataBase annotation");
        }

        super.processableDatabase = new ProcessableDatabase(databaseAnnotation.dataBaseType(), databaseAnnotation.version());

        // Analysis typeConverters

        // Analysis entities
        List<ProcessableEntity> processableEntityList = analysisEntities(annotatedElement);
        // Debug
        for (ProcessableEntity entity : processableEntityList) {
            processor.info(annotatedElement, "Analysing entity: " + entity.getTableName());
            for (ProcessableEntity.EntityColumn column : entity.getEntityColumnList()) {
                processor.info(annotatedElement, "- column: " + column.getElement().getSimpleName() + ":" + column.getElement().asType().toString());
            }
        }

    }

    @Override
    protected List<ProcessableEntity> analysisEntities(Element annotatedElement) throws PreProcessUnexpectedError {
        List<ProcessableEntity> result = new ArrayList<>();
        List<DeclaredType> declaredTypesOfEntity = APTools.getClassArrayFromAnnotation(annotatedElement, Database.class, Database.FILED_ENTITIES, true);
        if (declaredTypesOfEntity == null) {
            throw new PreProcessUnexpectedError(String.format("Cannot read entities in database %s", annotatedElement.asType().toString()));
        }
        for (DeclaredType entityType : declaredTypesOfEntity) {
            result.add(ProcessableEntity.builder(entityType));
        }
        return result;
    }

    @Override
    protected List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement) throws PreProcessUnexpectedError {
        return null;
    }
}
