/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.taglibs;

import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.*;

/**
 * This tage class generates a list of names for a JSP to display.
 * The name of the list determines the type to generate. The generated
 * list is stored under the given name in a request object. For example, a
 * list with topic names is stored under a request object as
 * IntactUserIF.TOPIC_NAMES if the name of list is given as
 * IntactUserIF.TOPIC_NAMES.
 * <p>
 * This tag class generates no output.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CreateNameListTag extends TagSupport {

    /**
     * The name of the list to generate.
     */
    private String  myListName;

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        return EVAL_BODY_INCLUDE;
    }

    /**
     * Sets the name of the list to generate
     *
     * @param name the name of the list to generate. It must be one of
     */
    public void setName(String name) {
        myListName = name;
    }

    /**
     * Called when the JSP encounters the end of a tag. This will create the
     * list for given name and store it in a session object.
     */
    public int doEndTag() throws JspException {
        // Handler to the Intact User via session.
        IntactUserIF user = (IntactUserIF)
            super.pageContext.getSession().getAttribute(
            WebIntactConstants.INTACT_USER);

        // Save the list in a request.
        super.pageContext.getRequest().setAttribute(
            myListName, user.getList(myListName));
        return EVAL_PAGE;
    }
}