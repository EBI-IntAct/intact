/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.config.ExceptionConfig;
import uk.ac.ebi.intact.application.editor.exception.BaseException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Extends the struts default exception handler to Editor specific behaviour.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorExceptionHandler extends ExceptionHandler {

    /**
     * The Editor logger for logging.
     */
    private static final Logger myLogger = Logger.getLogger(EditorConstants.LOGGER);

    public ActionForward execute(Exception ex,
                                 ExceptionConfig config,
                                 ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws ServletException {
        // The path for the forward either from the exception element or from
        // the input attribute.
        String path = (config.getPath() != null)
                ? config.getPath() : mapping.getInput();
        // The forward object.
        ActionForward forward = new ActionForward(path);

        // The error to store.
        ActionError error;
        // The prtoperty name for this error.
        String property = null;

        // Logs the error.
        myLogger.info(ex);

        // Figure out what type of exception has been thrown.
        if (ex instanceof ValidationException) {
            ValidationException valex = (ValidationException) ex;
            property = valex.getFilterKey();
            error = new ActionError(valex.getMessageKey());
        }
        else if (ex instanceof BaseException) {
            // Editor specific exception.
            BaseException baseEx = (BaseException) ex;
            error = new ActionError(baseEx.getMessageKey(), baseEx.getMessage());
        }
        else {
            error = new ActionError(config.getKey());
            property = error.getKey();
        }
        // Store the error in the proper action using the super method.
        storeException(request, property, error, forward, config.getScope());

        return forward;
    }
}
