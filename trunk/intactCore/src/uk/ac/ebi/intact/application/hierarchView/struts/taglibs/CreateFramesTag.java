/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

// intact
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.struts.view.OptionGenerator;
import uk.ac.ebi.intact.application.hierarchView.struts.view.LabelValueBean;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.IOException;

/**
 * Create application frames, and in case the index page receive a query,
 * it's forwarded to the left frame.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class CreateFramesTag extends TagSupport {

    private static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        // evaluate the tag's body content and any sub-tag
        return SKIP_BODY;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag.
     */
    public int doEndTag() throws JspException {

        String leftUrl  = null,
               rightUrl = "hierarchy.jsp"; // no parameter to pass

        // retreive the eventual request
        String AC    = (String) pageContext.getAttribute ("AC");
        String depth = (String) pageContext.getAttribute ("depth");

        logger.info ("parameter given to index.jsp: AC=" + AC + ", depth=" + depth);

        if ((null != AC) || (null != depth)) {
            // There is a query to forward to the left frame
            if (null == AC) AC = "";
            if (null == depth) depth = "";

			// get a collection of highlightment sources.
			ArrayList sources = OptionGenerator.getHighlightmentSources ();
            Iterator iterator = sources.iterator ();

            String firstSource = "none" ;
            if (iterator.hasNext()) {
                LabelValueBean lvb = (LabelValueBean) iterator.next();
                firstSource = lvb.getValue();
            }

            // Create the proper URL
            leftUrl = "visualize.do?AC=" + AC + "&depth=" + depth + "&method=" + firstSource;
        } else {
            // no query
            leftUrl = "view.jsp";
        }

        // Create frames
        StringBuffer sb = new StringBuffer ();

        String rightFrameName = Constants.RIGHT_FRAME_NAME ;
        String leftFrameName = Constants.LEFT_FRAME_NAME ;

        sb.append("  <frameset cols=\"50%, 50%\">\n");
        sb.append("    <frame name=\"" + leftFrameName  + "\" src=\""+ leftUrl +"\">\n");
        sb.append("    <frame name=\"" + rightFrameName + "\" src=\""+ rightUrl +"\">\n");
        sb.append("  </frameset>\n");
        sb.append("  <noframes>\n");
        sb.append("    <p>This frameset document contains:\n");
        sb.append("    <ul>\n");
        sb.append("      <li><a href=\""+ leftUrl +"\"> Visualization page </a>\n");
        sb.append("      <li><a href=\""+ rightUrl +"\"> Hierarchy page </a>\n");
        sb.append("    </ul>\n");
        sb.append("  </noframes>\n");

        try {
            // write the frame definition
            pageContext.getOut().write( sb.toString() );
        } catch (IOException e) {
            logger.error ("When trying to write the frame definition", e);
        }

        return EVAL_PAGE;
    } // doEndTag

} // CreateFramesTag