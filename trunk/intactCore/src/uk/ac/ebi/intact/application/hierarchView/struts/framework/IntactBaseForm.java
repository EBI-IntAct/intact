/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.framework;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.log4j.Logger;

import uk.ac.ebi.intact.application.hierarchView.business.Constants;

/**
 * Super class for all hierarchView related form classes.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class IntactBaseForm extends ActionForm {

    public static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /** The global Intact error key. */
    public static final String INTACT_ERROR = "IntactError";

    /** Error container */
    private ActionErrors myErrors = new ActionErrors();


    /**
     * return the error set
     * @return the error set
     */
    protected ActionErrors getErrors () {
        return myErrors;
    }

    /**
     * Clear error container.
     */
    protected void clearErrors() {
        if (!myErrors.isEmpty()) {
            myErrors.clear();
        }
    }

    /**
     * Adds an error with given key.
     *
     * @param key the error key. This value is looked up in the
     * IntactResources.properties bundle.
     */
    protected void addError(String key) {
        myErrors.add(INTACT_ERROR, new ActionError(key));
    }

    /**
     * Adds an error with given key and value.
     *
     * @param key the error key. This value is looked up in the
     * IntactResources.properties bundle.
     * @param value the value to substitute for the first place holder in the
     * IntactResources.properties bundle.
     */
    protected void addError(String key, String value) {
        myErrors.add(INTACT_ERROR, new ActionError(key, value));
    }

    /**
     * Specify if an the error set is empty.
     *
     * @return boolean false is there are any error registered, else true
     */
    protected boolean isErrorsEmpty () {
        return myErrors.isEmpty();
    }
}
