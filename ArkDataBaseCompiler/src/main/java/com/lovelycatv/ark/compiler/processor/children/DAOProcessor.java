package com.lovelycatv.ark.compiler.processor.children;

import com.lovelycatv.ark.common.annotations.common.Delete;
import com.lovelycatv.ark.common.annotations.common.Insert;
import com.lovelycatv.ark.common.annotations.common.Query;
import com.lovelycatv.ark.common.annotations.common.Update;
import com.lovelycatv.ark.compiler.exceptions.ProcessorError;
import com.lovelycatv.ark.compiler.pre.ProcessableDAO;
import com.lovelycatv.ark.compiler.pre.ProcessableDatabase;
import com.lovelycatv.ark.compiler.processor.children.base.AbstractDAOProcessor;
import com.lovelycatv.ark.compiler.processor.children.base.AbstractDatabaseProcessor;
import com.lovelycatv.ark.compiler.utils.APTools;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public final class DAOProcessor extends AbstractDAOProcessor {
    public DAOProcessor(AbstractDatabaseProcessor databaseProcessor) {
        super(databaseProcessor);
    }

    @Override
    public void start() throws ProcessorError {
        ProcessableDatabase database = getDatabaseProcessor().getProcessableDatabase();
        for (ProcessableDAO dao : database.getDaoController().getDAOList()) {
            buildDAO(dao);
        }

        debugging();
    }

    @Override
    public void buildDAO(ProcessableDAO processableDAO) throws ProcessorError {
        // Verify DAO
        verifyDAO(processableDAO);
    }

    @Override
    public void verifyDAO(ProcessableDAO processableDAO) throws ProcessorError {
        for (Element interfaceElement : processableDAO.getDAOClassElement().getEnclosedElements()) {
            if (!APTools.containsAnnotation(interfaceElement, Insert.class, Update.class, Delete.class, Query.class)) {
                throw new ProcessorError(String.format("The method %s() in %s must have only one of annotations like @Insert @Update @Delete or @Query",
                        interfaceElement.getSimpleName(), processableDAO.getDAOClassElement().asType().toString()));
            }
        }
    }

    @Override
    protected void debugging() {

    }
}
