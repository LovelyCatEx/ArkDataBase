package com.lovelycatv.ark.compiler.pre.relational.verify.dao;

import com.lovelycatv.ark.common.enums.DataBaseType;
import com.lovelycatv.ark.compiler.exceptions.ProcessorException;
import com.lovelycatv.ark.compiler.exceptions.ProcessorUnexpectedError;
import com.lovelycatv.ark.compiler.pre.relational.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.relational.verify.AbstractProcessableVerification;
import com.lovelycatv.ark.compiler.pre.relational.verify.parameter.SupportedParameterManager;
import com.lovelycatv.ark.compiler.utils.APTools;

public class DAOVerification extends AbstractProcessableVerification<ProcessableDAO> {
    public DAOVerification(DataBaseType dataBaseType, SupportedParameterManager supportedParameterManager, ProcessableDAO processableObject) {
        super(dataBaseType, supportedParameterManager, processableObject);
    }

    @Override
    public void verify() throws ProcessorException, ProcessorUnexpectedError {
        ProcessableDAO processableDAO = getProcessableObject();
        for (ProcessableDAO.DAOMethod daoMethod : processableDAO.getDaoMethodList()) {
            if (daoMethod.getAnnotations() == null || daoMethod.getAnnotations().size() != 1) {
                throw new ProcessorException(String.format("%s() in dao %s must be annotated with one of @Query @Insert @Update or @Delete",
                        daoMethod.getElement().getSimpleName(), APTools.getClassNameFromTypeMirror(processableDAO.getDAOClassElement().asType())));
            }
        }
    }
}
