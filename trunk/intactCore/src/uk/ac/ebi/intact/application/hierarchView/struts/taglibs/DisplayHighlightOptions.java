/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.highlightment.source.HighlightmentSource;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;



/**
 * That class allows to include HTML code to Displays current highlight source options.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class DisplayHighlightOptions extends TagSupport {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        // evaluate the tag's body content and any sub-tag
        return EVAL_BODY_INCLUDE;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag.
     */
    public int doEndTag() throws JspException {
        HttpSession session = pageContext.getSession();

        try {
            IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
            String methodClass = user.getMethodClass();

            // Search the list of protein to highlight
            HighlightmentSource highlightmentSource = HighlightmentSource.getHighlightmentSource(methodClass);
            String htmlCode = "";
            if (null != highlightmentSource) {
                htmlCode = highlightmentSource.getHtmlCodeOption(session);
            } else {
                logger.error ("Unable to instanciate the current source: " + methodClass);
            }

            // write it
            pageContext.getOut().write (htmlCode);
        } catch (Exception ioe) {
            throw new JspException ("Error: could not display highlight options.");
        }
        return EVAL_PAGE;
    } // doEndTag

} // DisplayHighlightOptions