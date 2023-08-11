package com.lovelycatv.ark.compiler.processor.relational.children;

import com.lovelycatv.ark.ArkVars;
import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.exceptions.ProcessorException;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDatabase;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableEntity;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableTypeConverter;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDAOProcessor;
import com.lovelycatv.ark.compiler.processor.relational.children.base.AbstractDatabaseProcessor;
import com.lovelycatv.ark.compiler.processor.relational.objects.EntityAdapterInfo;
import com.lovelycatv.ark.compiler.utils.APTools;
import com.lovelycatv.ark.runtime.supported.ArkJDBC;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public List<MethodSpec.Builder> buildAllAdapterMethods(ProcessableDAO processableDAO) throws ProcessorUnexpectedError {
        List<MethodSpec.Builder> result = new ArrayList<>();
        for (ProcessableDAO.DAOMethod method : processableDAO.getAdapterMethods()) {
            if (method.getAnnotations() == null || method.getAnnotations().size() == 0 || method.getAnnotations().get(0) == null) {
                throw new ProcessorUnexpectedError(String.format("An error occurred while trying to build methods of adapter in %s, cannot get annotations of %s",
                        APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType()), method.getElement().getSimpleName()));
            }
            Annotation annotation = method.getAnnotations().get(0);
            ExecutableElement i = (ExecutableElement) method.getElement();
            VariableElement firstParamElement = i.getParameters().get(0);
            ProcessableEntity entityIndistinct = super.getDatabaseProcessor().getProcessableDatabase().getEntityController().getEntityIndistinct(firstParamElement.asType());
            if (entityIndistinct == null) {
                throw new ProcessorUnexpectedError(String.format("An error occurred while trying to build methods of adapter in %s, cannot get target entity that %s refers to",
                        APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType()), method.getElement().getSimpleName()));
            }
            // This is a dangerous new instance, because I only use the method getFieldName(Class<? extends Annotation> target) in it.
            EntityAdapterInfo entityAdapterInfo = new EntityAdapterInfo(null, null, null);
            // Do not forget to set target entity
            entityAdapterInfo.setTargetEntity(entityIndistinct);
            MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(method.getElement().getSimpleName().toString())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(firstParamElement.asType()), "_entity");

            String adapterMethodName = "NOT_DEFINED";
            if (annotation.annotationType().getName().equals(Insert.class.getName())) {
                adapterMethodName = "insert";
            } else if (annotation.annotationType().getName().equals(Update.class.getName())) {
                adapterMethodName = "update";
            } else if (annotation.annotationType().getName().equals(Delete.class.getName())) {
                adapterMethodName = "delete";
            }
            methodSpec.addStatement("this.$L.$L(_entity)", entityAdapterInfo.getFieldName(annotation.annotationType()), adapterMethodName);

            result.add(methodSpec);
        }

        return result;
    }

    @Override
    public List<MethodSpec.Builder> buildAllQueryMethods(ProcessableDAO processableDAO) throws ProcessorUnexpectedError, ProcessorException {
        List<MethodSpec.Builder> result = new ArrayList<>();
        List<ProcessableDAO.DAOMethod> queryMethods = processableDAO.getQueryMethods();
        for (ProcessableDAO.DAOMethod method : queryMethods) {
            if (!(method.getElement() instanceof ExecutableElement)) {
                throw new ProcessorUnexpectedError(String.format("An error occurred while trying to build method %s() in %s, cannot cast Element to ExecutableElement",
                        method.getElement().getSimpleName(), APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType())));
            }
            ExecutableElement i = (ExecutableElement) method.getElement();
            List<? extends VariableElement> paramsOfMethod = i.getParameters();
            if (!(method.getAnnotations().get(0) instanceof Query)) {
                throw new ProcessorUnexpectedError(String.format("An error occurred while trying to build method %s() in %s, cannot cast Annotation to Query",
                        method.getElement().getSimpleName(), APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType())));
            }
            Query query = (Query) method.getAnnotations().get(0);

            MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(method.getElement().getSimpleName().toString())
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);
            CodeBlock.Builder returnCodeBlock = CodeBlock.builder();

            String queryStr = query.sql();

            // Apply parameters
            if (paramsOfMethod != null) {
                for (VariableElement param : paramsOfMethod) {
                    String paramName = param.getSimpleName().toString();
                    queryStr = queryStr.replace(":" + paramName, "\" + " + paramName + " + \"");

                    // Add to methodSpec
                    methodSpec.addParameter(ClassName.get(param.asType()), param.getSimpleName().toString());
                }
            }

            queryStr = "\"" + queryStr + "\"";

            // hasReturnValue
            final TypeMirror returnType = i.getReturnType();
            if (APTools.isVoid(returnType)) {
                if (!query.executeOnly()) {
                    throw new ProcessorException(String.format("method %s() in %s is annotated with @Query but returns null. If you want to execute this statement only, try using executeOnly = true in this @Query.",
                            method.getElement().getSimpleName(), APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType())));
                }
            } else {
                methodSpec.returns(ClassName.get(returnType));
            }

            // Prepare
            boolean isList = APTools.isListWithoutParameterizedTypes(returnType);
            boolean isArray = returnType.getKind() == TypeKind.ARRAY;

            final String varName_result = "_result";
            ProcessableEntity processableEntity = null;
            TypeMirror entityTypeMirror = returnType;
            if (!query.executeOnly()) {
                if (isList) {
                    List<? extends TypeMirror> parameterizedType = APTools.getParameterizedType(returnType);
                    if (parameterizedType == null || parameterizedType.size() == 0) {
                        throw new ProcessorUnexpectedError(String.format("An error occurred when get parameterized types from return type of %s() in %s",
                                method.getElement().getSimpleName(), APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType())));
                    }
                    entityTypeMirror = parameterizedType.get(0);
                    if (APTools.getClassNameFromTypeMirror(returnType).startsWith(List.class.getName())) {
                        methodSpec.addStatement("$T<$T> $L = new $T<>()", ArrayList.class, entityTypeMirror, varName_result, ArrayList.class);
                    } else {
                        methodSpec.addStatement("$T $L = new $T()", returnType, varName_result, returnType);
                    }
                    returnCodeBlock.add("return $L;", varName_result);
                } else if (isArray) {
                    entityTypeMirror = APTools.getTypeByName(APTools.getClassNameFromTypeMirror(returnType).replace("[", "").replace("]","")).asType();
                    methodSpec.addStatement("$T<$T> $L = new $T<>()", ArrayList.class, entityTypeMirror, varName_result, ArrayList.class);
                    returnCodeBlock.add("return $L.toArray(new $T{});", varName_result, returnType);
                } else {
                    methodSpec.addStatement("$T $L = null", returnType, varName_result);
                    returnCodeBlock.add("return $L;", varName_result);
                }

                processableEntity = super.getDatabaseProcessor().getProcessableDatabase().getEntityController().getEntityIndistinct(entityTypeMirror);
                if (processableEntity == null) {
                    throw new ProcessorUnexpectedError(String.format("An error occurred when get entity from return type of %s() in %s",
                            method.getElement().getSimpleName(), APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType())));
                }
            }


            methodSpec.beginControlFlow("try");

            final String varName_preparedStatement = "_preparedStatement";
            final String varName_resultSet = "_resultSet";
            // PreparedStatement _preparedStatement = this.__db.getDatabaseManager().getConnection().prepareStatement("SELECT * FROM users");
            methodSpec.addStatement("$T $L = this.$L.getDatabaseManager().getConnection().prepareStatement($L)",
                    PreparedStatement.class, varName_preparedStatement, FIELD_DAO_DATABASE, queryStr);
            // ResultSet _resultSet = _preparedStatement.executeQuery();
            if (query.executeOnly()) {
                methodSpec.addStatement("$L.execute()", varName_preparedStatement);
            } else {
                methodSpec.addStatement("$T $L = $L.executeQuery()", ResultSet.class, varName_resultSet, varName_preparedStatement);
                // While
                {
                    final String varName_resultEntity = "_entity";
                    methodSpec.beginControlFlow("while($L.next())", varName_resultSet);
                    methodSpec.addStatement("$T $L = new $L()", entityTypeMirror, varName_resultEntity, entityTypeMirror);

                    final SupportedParameterManager supportedParameterManager = super.getSupportedParameterManager();
                    final List<ProcessableTypeConverter> typeConverterList = super.getDatabaseProcessor().getProcessableDatabase().getTypeConverterController().getTypeConverterList();
                    for (ProcessableEntity.EntityColumn column : processableEntity.getEntityColumnList()) {
                        String resultSetGetterMethodName = "NOT_DEFINED";
                        for (Map.Entry<Class<?>, String> entry : ArkJDBC.getJavaTypeClassWithResultSetMethodNameMap().entrySet()) {
                            TypeMirror columnFinalType = column.getColumnType(supportedParameterManager, typeConverterList);
                            if (APTools.getClassNameFromTypeMirror(columnFinalType).equals(entry.getKey().getName())) {
                                resultSetGetterMethodName = entry.getValue();
                                break;
                            }
                        }

                        if (column.isAboutToTypeConverter(supportedParameterManager)) {
                            ProcessableTypeConverter.Converter converter = column.getInTypeConverter(typeConverterList);
                            methodSpec.addStatement("$L.$L($L.$L($L.$L($S)))",
                                    varName_resultEntity, column.getSetter().getMethodElement().getSimpleName(),
                                    ArkVars.getTypeConverterClassname(super.getDatabaseProcessor().getProcessableDatabase().getClassElement().getSimpleName().toString()),
                                    converter.getMethodNameInDAO(),
                                    varName_resultSet, resultSetGetterMethodName, column.getColumnName());
                        } else {
                            methodSpec.addStatement("$L.$L($L.$L($S))",
                                    varName_resultEntity, column.getSetter().getMethodElement().getSimpleName(),
                                    varName_resultSet, resultSetGetterMethodName, column.getColumnName());
                        }

                    }

                    if (!isList && !isArray) {
                        methodSpec.addStatement("$L = $L", varName_result, varName_resultEntity);
                        methodSpec.addStatement("break");
                    } else {
                        methodSpec.addStatement("$L.add($L)", varName_result, varName_resultEntity);
                    }

                    methodSpec.endControlFlow();
                }
                methodSpec.addStatement("$L.close()", varName_resultSet);
                methodSpec.addStatement("$L.close()", varName_preparedStatement);
            }

            methodSpec.nextControlFlow("catch ($T e)", Exception.class);
            methodSpec.addStatement("throw new $T(e)", RuntimeException.class);
            methodSpec.endControlFlow();

            methodSpec.addCode(returnCodeBlock.build());

            //methodSpec.addJavadoc("Generated by ark automatically");

            result.add(methodSpec);
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
