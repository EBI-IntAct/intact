/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.taglibs;

import java.util.*;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.*;

/**
 * This tage class compiles a list of CV topics for a user to edit. The list of CV
 * topics are read from given resource file (must be at the top level of classes
 * directory) and saves it in a HTTP request object.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CreateIntactTypeListTag extends TagSupport {

    /**
     * The key to access the list from a request object.
     */
    private String myKey;

    /**
     * The name of the resource bundle.
     */
    private String myResourceName;

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        return EVAL_BODY_INCLUDE;
    }

    /**
     * Sets the key to access the resources.
     * @param key the key to access the resources.
     */
    public void setKey(String key) {
        myKey = key;
    }

    /**
     * Sets the resource name for to load resources from.
     * @param resource the name of the resource to load CV topics from.
     */
    public void setResource(String resource) {
        myResourceName = resource;
    }

    /**
     * Called when the JSP encounters the end of a tag. This will create the
     * option list.
     */
    public int doEndTag() throws JspException {
        // Load the resource bundle using default Locale.
        ResourceBundle rb = ResourceBundle.getBundle(myResourceName);

        // The collection to contain the the type names.
        Collection types = new ArrayList();

        // Iterate through each topic adding as an option.
        for (Enumeration keys = rb.getKeys(); keys.hasMoreElements();) {
            types.add(keys.nextElement());
        }
        // Save the list in a request.
        super.pageContext.getRequest().setAttribute(myKey, types);
        return EVAL_PAGE;
    }
}
