/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.struts.taglibs;

import uk.ac.ebi.intact.application.hierarchView.business.Constants;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.Globals;

/**
 * That class allows to clear from the request scope (in pageRequest)
 * any errors collected by struts.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class ClearErrorsTag extends TagSupport {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /**
     * Skip the body content.
     */
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }

    /**
     * Clear Struts any Struts errors stored in the pageContext.
     */
    public int doEndTag() throws JspException {

        ActionErrors errors = (ActionErrors) pageContext.findAttribute(Globals.ERROR_KEY);

        if ( null != errors ) {
            errors.clear();
            pageContext.removeAttribute(Globals.ERROR_KEY);
        }

        return SKIP_BODY;
    }
}