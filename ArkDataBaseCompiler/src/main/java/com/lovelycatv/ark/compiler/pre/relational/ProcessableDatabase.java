package com.lovelycatv.ark.compiler.pre.relational;

import com.lovelycatv.ark.common.enums.DataBaseType;

import javax.lang.model.element.Element;

public final class ProcessableDatabase extends AbstractProcessable {
    private final DataBaseType dataBaseType;
    private final int version;
    private final Element classElement;

    private final ProcessableEntity.Controller entityController = new ProcessableEntity.Controller();
    private final ProcessableTypeConverter.Controller typeConverterController = new ProcessableTypeConverter.Controller();
    private final ProcessableDAO.Controller daoController = new ProcessableDAO.Controller();

    public ProcessableDatabase(DataBaseType dataBaseType, int version, Element classElement) {
        super(ProcessableType.DATABASE);
        this.dataBaseType = dataBaseType;
        this.version = version;
        this.classElement = classElement;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public int getVersion() {
        return version;
    }

    public final ProcessableDAO.Controller getDaoController() {
        return daoController;
    }

    public ProcessableEntity.Controller getEntityController() {
        return entityController;
    }

    public ProcessableTypeConverter.Controller getTypeConverterController() {
        return typeConverterController;
    }

    public Element getClassElement() {
        return classElement;
    }
}
