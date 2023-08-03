package com.lovelycatv.ark.compiler.processor.children;

import com.lovelycatv.ark.common.annotations.ArkDebug;
import com.lovelycatv.ark.common.annotations.Database;
import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.PreProcessException;
import com.lovelycatv.ark.compiler.exceptions.PreProcessUnexpectedError;
import com.lovelycatv.ark.compiler.pre.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.verify.entity.EntityVerification;
import com.lovelycatv.ark.compiler.pre.verify.parameter.MySQLSupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SQLiteSupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.verify.typeconverter.TypeConverterVerification;
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
    public void analysis(Element annotatedElement) throws PreProcessUnexpectedError, PreProcessException {
        Database databaseAnnotation = annotatedElement.getAnnotation(Database.class);
        if (databaseAnnotation == null) {
            throw new PreProcessUnexpectedError("Cannot find @DataBase annotation");
        }

        ArkDebug arkDebug = annotatedElement.getAnnotation(ArkDebug.class);
        if (arkDebug != null) {
            setDebugging(arkDebug.enabled());
        } else {
            setDebugging(false);
        }

        super.processableDatabase = new ProcessableDatabase(databaseAnnotation.dataBaseType(), databaseAnnotation.version());
        if (databaseAnnotation.dataBaseType() == DataBaseType.MYSQL) {
            super.supportedParameterManager = new MySQLSupportedParameterManager();
        } else if (databaseAnnotation.dataBaseType() == DataBaseType.SQLITE) {
            super.supportedParameterManager = new SQLiteSupportedParameterManager();
        } else {
            throw new PreProcessUnexpectedError("If you see this output error that means I've forgot to add statement in this part! class: " + this.getClass().getName());
        }


        // Analysis typeConverters
        List<ProcessableTypeConverter> processableTypeConverterList = analysisTypeConverters(annotatedElement, super.supportedParameterManager);

        if (isDebugging()) {
            // Debug
            for (ProcessableTypeConverter typeConverter : processableTypeConverterList) {
                processor.info(typeConverter.getTypeConverterType().asElement(), "Analysing typeConverter class: " + typeConverter.getTypeConverterType().asElement().asType().toString() +
                        String.format(" | %s converter(s)", typeConverter.getTypeConverterList().size()));
                for (ProcessableTypeConverter.Converter converter : typeConverter.getTypeConverterList()) {
                    processor.info(typeConverter.getTypeConverterType().asElement(), (converter.isConvertOut() ? "[in]" : "[out]") + converter.getFrom().toString() + " return " + converter.getTo());
                }

            }
        }

        super.getProcessableDatabase().getProcessableTypeConverterController().getProcessableTypeConverterList().addAll(processableTypeConverterList);

        // Analysis entities
        List<ProcessableEntity> processableEntityList = analysisEntities(annotatedElement);

        if (isDebugging()) {
            // Debug
            for (ProcessableEntity entity : processableEntityList) {
                processor.info(entity.getDeclaredEntityType().asElement(), "Analysing entity: " + entity.getTableName() +
                        String.format(" | %s column(s)", entity.getEntityColumnList().size()));
                for (ProcessableEntity.EntityColumn column : entity.getEntityColumnList()) {
                    processor.info(entity.getDeclaredEntityType().asElement(), "- column: " + column.getElement().getSimpleName() + " : " + column.getElement().asType().toString());
                }
            }
        }

        super.getProcessableDatabase().getProcessableEntityController().getEntityList().addAll(processableEntityList);

        // Verify typeConverters
        new TypeConverterVerification(super.getProcessableDatabase().getDataBaseType(), super.supportedParameterManager,
                super.getProcessableDatabase().getProcessableTypeConverterController().getProcessableTypeConverterList()).verify();

        // Verify entities
        for (ProcessableEntity entity : super.getProcessableDatabase().getProcessableEntityController().getEntityList()) {
            new EntityVerification(super.getProcessableDatabase().getDataBaseType(), super.supportedParameterManager, entity,
                    super.getProcessableDatabase().getProcessableTypeConverterController()).verify();
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
    protected List<ProcessableTypeConverter> analysisTypeConverters(Element annotatedElement, SupportedParameterManager supportedParameterManager)
            throws PreProcessUnexpectedError, PreProcessException {
        List<ProcessableTypeConverter> result = new ArrayList<>();
        List<DeclaredType> declaredTypesOfTypeConverter = APTools.getClassArrayFromAnnotation(annotatedElement, Database.class, Database.FILED_TYPE_CONVERTERS, true);
        if (declaredTypesOfTypeConverter == null) {
            throw new PreProcessUnexpectedError(String.format("Cannot read typeConverters in database %s", annotatedElement.asType().toString()));
        }
        for (DeclaredType typeConverterType : declaredTypesOfTypeConverter) {
            result.add(ProcessableTypeConverter.builder(typeConverterType, supportedParameterManager));
        }
        return result;
    }
}
