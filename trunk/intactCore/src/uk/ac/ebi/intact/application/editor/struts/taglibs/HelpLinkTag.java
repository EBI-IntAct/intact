/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.taglibs;

import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.exception.SearchException;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

/**
 * Displays the help tag link. The server portion of the link defaults to the
 * current server.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class HelpLinkTag extends TagSupport {

    /**
     * The help tag.
     */
    private String myTag;

    /**
     * The title for the link; defaults to standard help title.
     */
    private String myTitle = EditorConstants.HELP_TITLE;

    /**
     * Skip the body content.
     */
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }

    // Set methods.

    public void setTag(String tag) {
        myTag = tag;
    }

    public void setTitle(String title) {
        myTitle = title;
    }

    /**
     * Displays the short labels.
     */
    public int doEndTag() throws JspException {

        // The servlet context to access application wide info.
        ServletContext ctx = pageContext.getServletContext();

        // To Allow access to Editor Service.
        EditorService service = (EditorService) ctx.getAttribute(
                EditorConstants.EDITOR_SERVICE);

        // The server path.
        String serverPath = (String) ctx.getAttribute(EditorConstants.SERVER_PATH);

        // The help link to write out.
        String helpLink = service.getHelpLinkAsHTML(serverPath, myTag, myTitle);

        // JSP writer to write the output.
        JspWriter out = pageContext.getOut();

        try {
            out.write(helpLink);
        }
        catch (IOException ioe) {
            throw new JspException(ioe.getMessage());
        }
        return EVAL_PAGE;
    }
}
