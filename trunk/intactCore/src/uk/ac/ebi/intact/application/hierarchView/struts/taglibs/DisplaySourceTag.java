/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

// intact
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserIF;
import uk.ac.ebi.intact.application.hierarchView.highlightment.source.HighlightmentSource;
import uk.ac.ebi.intact.application.hierarchView.struts.view.LabelValueBean;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;



/**
 * That class allow to initialize properly the HTTPSession object
 * with what will be neededlater by the user of the web application.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class DisplaySourceTag extends TagSupport {

    private static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    /**
     * Evaluate the tag's body content.
     */
    public int doStartTag() throws JspTagException {
        // evaluate the tag's body content and any sub-tag
        return EVAL_BODY_INCLUDE;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag. This will create the
     * option list.
     */
    public int doEndTag() throws JspException {
        HttpSession session = pageContext.getSession();

        try {
            IntactUserIF user = (IntactUserIF) session.getAttribute (Constants.USER_KEY);
            String AC = user.getAC();
            String method_class = user.getMethodClass();

            if (null != AC) {
                // get the implementation class of the selected source
                HighlightmentSource source = HighlightmentSource.getHighlightmentSource (method_class);

                if (null == source) {
                    pageContext.getOut().write ("An error occured when trying to retreive source.<br>");
                } else {
                    logger.info ("Display highlight source items for AC = " + AC +
                                 " SourceClass = " + method_class);

                    Collection urls = null;

                    try {
                        urls = source.getSourceUrls(AC, session);
                    } catch (IntactException ie) {
                        String msg = "ERROR<br>The hierarchView system is not properly configured. Please warn your administrator.";
                        pageContext.getOut().write (msg);
                        return EVAL_PAGE;
                    }


                    Iterator iterator = urls.iterator();
                    int size = urls.size();

                    if (0 == size) {
                        pageContext.getOut().write ("No source found for that protein (AC = " + AC + ")");
                    } else if (1 == size) {
                        // only one source element, let's forward to the relevant page.
                        LabelValueBean url = (LabelValueBean) iterator.next();
                        String adress = url.getValue();
                        String absoluteUrl = adress;

                        // inject the javascript code which allow to forward in a frame
                        String rightFrameName = Constants.RIGHT_FRAME_NAME ;
                        String forwardFunctionCode =
                                "<script language=\"JavaScript\">\n" +
                                "<!--   \n" +
                                "   /** \n" +
                                "    * Allows to forward to a specified URL inside a frame \n" +
                                "    */ \n" +
                                "   function forward ( absoluteUrl ) {  \n" +
                                "      parent." + rightFrameName + ".location.href = absoluteUrl; \n" +
                                "   } \n" +
                                "//--> \n" +
                                "</script>\n\n";
                        pageContext.getOut().write (forwardFunctionCode);

                        /* forward to this URL */
                        String msg = "<script language=\"JavaScript\">\n" +
                                     "<!--\n" +
                                     "     forward ( '" + absoluteUrl + "' );\n" +
                                     "//-->\n" +
                                     "</script>\n\n";
                        pageContext.getOut().write (msg);

                    } else {
                        // more than 1 source available : display a list of link
                        StringBuffer sb = new StringBuffer();
                        sb.append ("Select an element in the list : <br>");

                        while (iterator.hasNext()) {
                            LabelValueBean url = (LabelValueBean) iterator.next();
                            String adress = url.getValue();
                            String label = url.getLabel();
                            String description = url.getDescription();
                            String rightFrameName = Constants.RIGHT_FRAME_NAME ;

                            sb.append ("<a href=" + adress +" target=\"" + rightFrameName + "\">" + label + "</a>");
                            if (null != description)
                                sb.append (" : " + description);

                            sb.append("<br>");
                        } // while

                        pageContext.getOut().write (sb.toString());
                    } // else
                } // else
            } // if

        } catch (Exception ioe) {
            throw new JspException ("Fatal error: could not display protein associated source.");
        }
        return EVAL_PAGE;
    } // doEndTag

} // DisplaySourceTag