/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

// intact
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.highlightment.source.HighlightmentSource;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;
import uk.ac.ebi.intact.application.hierarchView.struts.view.LabelValueBean;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;



/**
 * That class allow to initialize properly the HTTPSession object
 * with what will be neededlater by the user of the web application.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class DisplaySourceTag extends TagSupport {

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

            String AC = (String) session.getAttribute (StrutsConstants.ATTRIBUTE_AC);
            String method_class = (String) session.getAttribute (StrutsConstants.ATTRIBUTE_METHOD_CLASS);

            Properties properties = PropertyLoader.load (StrutsConstants.PROPERTY_FILE);
            String debug = null;
            if (null != properties) {
                debug = properties.getProperty ("application.debug");
            } else {
                debug = "disable";
            }

            if (debug.equalsIgnoreCase ("enable")) {
                pageContext.getOut().write ("AC : " + AC + "<br>");
                pageContext.getOut().write ("method : " + method_class + " <br>");
            }

            if (null != AC) {
                HighlightmentSource source = HighlightmentSource.getHighlightmentSource(method_class);

                if (null == source) {
                    pageContext.getOut().write ("source is null! <br>");
                } else {
                    Collection urls = source.getUrl(AC, session);

                    Iterator iterator = urls.iterator();
                    int size = urls.size();

                    if (0 == size) {

                        pageContext.getOut().write ("no existing source for that protein (AC=" + AC + ")");

                    } else if (1 == size) {

                        // only one source element, let's forward to the relevant page.
                        LabelValueBean url = (LabelValueBean) iterator.next();
                        String adress = url.getValue();
                        String absoluteUrl = adress;

                        /* redirection to this URL */
                        String msg = "<script language=\"JavaScript\">\n" +
                                "<!--\n" +
                                "     forward ( '" + absoluteUrl + "' );\n" +
                                "//-->\n" +
                                "</script>\n";

                        pageContext.getOut().write (msg);

                    } else {
                        // more than 1 source available : display a list of link
                        pageContext.getOut().write ("Select an element in the list : <br>");

                        while (iterator.hasNext()) {
                            LabelValueBean url = (LabelValueBean) iterator.next();
                            String adress = url.getValue();
                            String label = url.getLabel();
                            String description = url.getDescription();

                            if (null == description)
                                pageContext.getOut().write ("<a href=" + adress +" target=\"frameHierarchy\">" + label + "</a> <br>");
                            else
                                pageContext.getOut().write("<a href=" + adress +" target=\"frameHierarchy\">" + label + "</a> " + description + "<br>");
                        } // while
                    } // else
                } // else
            } // if

        } catch (Exception ioe) {
            throw new JspException ("Fatal error: init tag could not initialize user's HTTPSession.");
        }
        return EVAL_PAGE;
    } // doEndTag

} // DisplaySourceTag