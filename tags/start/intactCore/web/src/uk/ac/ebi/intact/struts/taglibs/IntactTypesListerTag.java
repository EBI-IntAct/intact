/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.taglibs;

import java.util.*;
import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.*;

/**
 * This tage class compiles a list of CV topics for a user to edit. The list of CV
 * topics are read from CVTopicResources.properties file. This file must be at
 * the top level of classes directory (Tomcat adds to this location to the class
 * path).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactTypesListerTag extends TagSupport {

    /**
     * The resource bundle.
     */
    private ResourceBundle myTopics;

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        return EVAL_BODY_INCLUDE;
    }

    /**
     * Sets the resource name for to load resources from.
     * @param resource the name of the resource to load CV topics from.
     */
    public void setResource(String resource) {
        // Load the resource bundle using default Locale.
        myTopics = ResourceBundle.getBundle(resource);
    }

    /**
     * Called when the JSP encounters the end of a tag. This will create the
     * option list.
     */
    public int doEndTag() throws JspException {
        // Get a writer object for output
        JspWriter writer = pageContext.getOut();

        try {
            writer.write("<SELECT name=\"topic\">");

            // We are only interested in keys
            Enumeration bundleKeys = myTopics.getKeys();

            // The first option is selected by default.
            writer.write("<OPTION selected>" + (String) bundleKeys.nextElement()
                + "</OPTION>");

            // Iterate through each topic adding as an option.
            while (bundleKeys.hasMoreElements()) {
                writer.write("<OPTION>" + (String)bundleKeys.nextElement()
                    + "</OPTION>");
            }
            writer.write("</SELECT>");
        }
        catch (IOException ioe) {
            throw new JspException(ioe.toString());
        }
        return EVAL_PAGE;
    }

    public static void main(String[] args) {
        System.out.println(new IntactTypesListerTag());
    }
}
