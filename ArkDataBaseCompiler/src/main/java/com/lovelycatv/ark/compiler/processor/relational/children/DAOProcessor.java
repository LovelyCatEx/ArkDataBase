package com.lovelycatv.ark.compiler.processor.relational.children;

import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDAOProcessor;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.relational.objects.EntityAdapterInfo;
import com.lovelycatv.ark.compiler.utils.APTools;
import com.lovelycatv.ark.compiler.utils.StringX;
import com.lovelycatv.ark.runtime.ArkRelationalDatabase;
import com.lovelycatv.ark.runtime.constructures.base.relational.RelationalDatabase;
import com.squareup.javapoet.*;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;

public final class DAOProcessor extends AbstractDAOProcessor {
    public DAOProcessor(AbstractDatabaseProcessor databaseProcessor) {
        super(databaseProcessor);
    }

    @Override
    public void verifyDAO(ProcessableDAO processableDAO) throws ProcessorError {
        for (Element interfaceElement : processableDAO.getDAOClassElement().getEnclosedElements()) {
            boolean isAnnotatedWithCommon = APTools.containsAnnotation(interfaceElement, Insert.class, Update.class, Delete.class);
            if (!APTools.containsAnnotation(interfaceElement, Query.class) && !isAnnotatedWithCommon) {
                throw new ProcessorError(String.format("The method %s() in %s must have only one of annotations like @Insert @Update @Delete or @Query",
                        interfaceElement.getSimpleName(), processableDAO.getDAOClassElement().asType().toString()));
            } else if (isAnnotatedWithCommon) {
                if (!APTools.isVoid(((ExecutableElement) interfaceElement).getReturnType())) {
                    throw new ProcessorError(String.format("The method %s() annotated with @Insert @Update or @Delete in %s must return void",
                            interfaceElement.getSimpleName(), processableDAO.getDAOClassElement().asType().toString()));
                }
            }
        }
    }

    @Override
    public List<EntityAdapterInfo> scanAllUsedAdapters(ProcessableDAO processableDAO) throws ProcessorError {
        List<EntityAdapterInfo> result = new ArrayList<>();
        for (Element interfaceElement : processableDAO.getDAOClassElement().getEnclosedElements()) {
            if (!APTools.containsAnnotation(interfaceElement, Insert.class, Update.class, Delete.class)) {
                continue;
            }
            EntityAdapterInfo entityAdapterInfo = new EntityAdapterInfo(super.getDatabaseProcessor(), super.getSupportedParameterManager(),
                    super.getDatabaseProcessor().getProcessableDatabase().getTypeConverterController().getTypeConverterList());

            ExecutableElement i = (ExecutableElement) interfaceElement;
            List<? extends VariableElement> parameters = i.getParameters();

            ProcessableDatabase database = getDatabaseProcessor().getProcessableDatabase();

            if (parameters == null || parameters.size() != 1 ||
                    !database.getEntityController().containsEntityType(parameters.get(0).asType())) {
                throw new ProcessorError(String.format("The method %s() in %s must have only one parameter of entity",
                        interfaceElement.getSimpleName(), processableDAO.getDAOClassElement().asType().toString()));
            }

            VariableElement var = parameters.get(0);
            ProcessableEntity entity = database.getEntityController().getEntityIndistinct(var.asType());
            entityAdapterInfo.setTargetEntity(entity);

            // If exists
            boolean exists = false;
            for (EntityAdapterInfo info : result) {
                if (APTools.isTheSameTypeMirror(info.getTargetEntity().getDeclaredEntityType().asElement().asType(), entity.getDeclaredEntityType().asElement().asType())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                result.add(entityAdapterInfo);
            }
        }
        return result;
    }

    @Override
    protected void debugging() {

    }

    @Override
    public void determineSupportedParametersManager() {
        super.setSupportedParameterManager(super.getDatabaseProcessor().getSupportedParameterManager());
    }
}
