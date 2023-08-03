package com.lovelycatv.ark.compiler.pre;

import lombok.Data;

import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.List;

public class ProcessableDAO extends AbstractProcessable {
    private Element DAOAbstractMethodElement;
    private Element DAOClassElement;

    public ProcessableDAO() {
        super(ProcessableType.DAO);
    }

    public static ProcessableDAO builder(Element DAOAbstractMethodElement, Element DAOClassElement) {
        ProcessableDAO processableDAO = new ProcessableDAO();
        processableDAO.setDAOAbstractMethodElement(DAOAbstractMethodElement);
        processableDAO.setDAOClassElement(DAOClassElement);
        return processableDAO;
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
}
