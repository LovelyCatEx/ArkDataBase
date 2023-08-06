package com.lovelycatv.ark.compiler.pre.relational;

import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.object.JavaSupportedType;
import com.lovelycatv.ark.compiler.utils.APTools;
import com.lovelycatv.ark.compiler.utils.AnnotationUtils;
import lombok.Data;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;

public class ProcessableDAO extends AbstractProcessable {
    private Element DAOAbstractMethodElement;
    private Element DAOClassElement;

    private final List<DAOMethod> daoMethodList = new ArrayList<>();

    public ProcessableDAO() {
        super(ProcessableType.DAO);
    }

    public static ProcessableDAO builder(Element DAOAbstractMethodElement, Element DAOClassElement) {
        ProcessableDAO processableDAO = new ProcessableDAO();
        processableDAO.setDAOAbstractMethodElement(DAOAbstractMethodElement);
        processableDAO.setDAOClassElement(DAOClassElement);

        for (Element enclosedElement : DAOClassElement.getEnclosedElements()) {
            DAOMethod daoMethod = new DAOMethod();
            daoMethod.setElement(enclosedElement);
            daoMethod.setAnnotations(new ArrayList<>());
            for (Class<? extends Annotation> annotationClass : AnnotationUtils.getArkSQLAnnotations()) {
                if (APTools.containsAnnotation(enclosedElement, annotationClass)) {
                    daoMethod.getAnnotations().add(enclosedElement.getAnnotation(annotationClass));
                }
            }
            processableDAO.getDaoMethodList().add(daoMethod);
        }


        return processableDAO;
    }

    public List<DAOMethod> getDaoMethodList() {
        return daoMethodList;
    }

    public void setDAOClassElement(Element DAOClassElement) {
        this.DAOClassElement = DAOClassElement;
    }


    public Element getDAOClassElement() {
        return DAOClassElement;
    }

    public String getFileName() {
        return DAOAbstractMethodElement.getSimpleName().toString() + "_Impl";
    }

    public void setDAOAbstractMethodElement(Element DAOAbstractMethodElement) {
        this.DAOAbstractMethodElement = DAOAbstractMethodElement;
    }

    public Element getDAOAbstractMethodElement() {
        return DAOAbstractMethodElement;
    }

    @Data
    public static class Controller {
        private final List<ProcessableDAO> DAOList = new ArrayList<>();
    }

    @Data
    public static class DAOMethod {
        private Element element;
        private List<Annotation> annotations;

    }
}
