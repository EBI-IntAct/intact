/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.taglibs;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.exception.SearchException;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

/**
 * Displays all the labels bar the short label of the current edit object.
 * Only the short labels for the current edit topic are considered. The current
 * edit object is retrieved from the session object.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class DisplayShortLabelsTag extends TagSupport {

    /**
     * Skip the body content.
     */
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }

    /**
     * Displays the short labels.
     */
    public int doEndTag() throws JspException {

        // Don't create a new session.
        HttpSession session = pageContext.getSession();
        if (session == null) {
            throw new JspException(
                    "Your session has expired; you need to login again");
        }
        EditUserI user = (EditUserI)
                session.getAttribute(EditorConstants.INTACT_USER);
        if (user == null) {
            throw new JspException("No user exists; you need to login again");
        }
        // The object we are editing at the moment.
        AnnotatedObject editObject = user.getView().getAnnotatedObject();
        // The current edit object's short label.
        String editLabel = editObject.getShortLabel();
        // The class name of the current edit object.
        String className = editObject.getClass().getName();

        // JSP writer to write the output.
        JspWriter out = pageContext.getOut();

        try {
            Collection results = user.search(className, "shortLabel", "*");
            // The counter to count line length.
            int lineLength = 0;
            // Flag to indicate the first item.
            boolean first = true;
            for (Iterator iter = results.iterator(); iter.hasNext();) {
                // Avoid this object's own short label.
                String label = ((AnnotatedObject) iter.next()).getShortLabel();
                if (label.equals(editLabel)) {
                    continue;
                }
                if (first) {
                    first = false;
                }
                else {
                    out.write(", ");
                }
                // Strip the package name from the class name.
                String topic = className.substring(className.lastIndexOf('.') + 1);
                out.write("<a href=\"" + "javascript:show('" + topic + "', '"
                        + label + "')\"" + ">" + label + "</a>");
                lineLength += label.length();
                if (lineLength > 80) {
                    out.write("<br/>");
                    first = true;
                    lineLength = label.length();
                }
            }
        }
        catch (SearchException se) {
            throw new JspException(se.getMessage());
        }
        catch (IOException ioe) {
            throw new JspException(ioe.getMessage());
        }
        return EVAL_PAGE;
    }
}
