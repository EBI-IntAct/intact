/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.highlightment.source.HighlightmentSource;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;
import uk.ac.ebi.intact.application.hierarchView.struts.view.utils.SourceBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.simpleGraph.Node;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



/**
 * That class allow to initialize properly in the session the
 * sources to display according to the current central protein.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class DisplaySourceTag extends TagSupport {

    private static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    /**
     * Skip the body content.
     */
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag. This will create the
     * option list.
     */
    public int doEndTag() throws JspException {
        HttpSession session = pageContext.getSession();

        try {
            IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
            if (user == null) {
                logger.error ("No existing session");
                return EVAL_PAGE;
            }

            InteractionNetwork in = user.getInteractionNetwork();
            if (in == null) {
                logger.error ("No existing interaction network");
                return EVAL_PAGE;
            }

            Interactor interactor = in.getCentralProtein();
            ArrayList centrals = in.getCentralProteins();
            logger.info("Central protein AC: " + interactor.getAc());
            logger.info(centrals.size() + " central protein(s) referenced in the interaction network.");

            Collection xRefs = new ArrayList (50);

            int max = centrals.size();
            for (int x = 0; x<max; x++) {
                // union of disctinct xref
                Node centralProtein = (Node) centrals.get(x);
                interactor = centralProtein.getInteractor();
                Collection xRefs2 = interactor.getXrefs();
                Iterator iterator = xRefs2.iterator();
                while (iterator.hasNext()) {
                    Xref aXref = (Xref) iterator.next();
                    if (! xRefs.contains(aXref)) {
                        xRefs.add(aXref);
                    }
                }
            }

            String queryString = user.getQueryString();
            String method_class = user.getMethodClass();

            if (null != queryString) {
                // get the implementation class of the selected source
                HighlightmentSource source = HighlightmentSource.getHighlightmentSource (method_class);

                if (null == source) {
                    pageContext.getOut().write ("An error occured when trying to retreive source.<br>");
                    logger.error("Error when trying to load the source class: " + method_class);
                } else {
                    logger.info ("Display highlight source items for query = " + queryString +
                                 " SourceClass = " + method_class);

                    List urls = null;

                    try {
                        ServletRequest request = pageContext.getRequest();
                        String host     = (String) session.getAttribute( StrutsConstants.HOST );
                        String protocol = (String) session.getAttribute( StrutsConstants.PROTOCOL );

                        String protocolAndHost = "";
                        if (host != null && protocol != null) {
                            protocolAndHost = protocol + "//" + host;
                        }

                        HttpServletRequest httpRequest = (HttpServletRequest)request;

                        String contextPath = httpRequest.getContextPath();
                        System.out.println("CONTEXT PATH: " + contextPath);

                        String applicationPath = protocolAndHost + contextPath;
                        System.out.println("APPLICATION PATH: " + applicationPath);


                        Collection selectedKeys = user.getKeys();
                        String theClickedKeys = user.getSelectedKey();

                        Collection allSelectedKeys = new ArrayList ();

                        if (selectedKeys != null) {
                            allSelectedKeys.addAll (selectedKeys);
                        }
                        if (theClickedKeys != null) allSelectedKeys.add (theClickedKeys);

                        urls = source.getSourceUrls(xRefs, allSelectedKeys, applicationPath);
                    } catch (IntactException ie) {
                        String msg = "ERROR<br>The hierarchView system is not properly configured. Please warn your administrator.";
                        pageContext.getOut().write (msg);
                        return EVAL_PAGE;
                    }

                    /**
                     * We store the source collection in the session in order to display it
                     * with a dedicated tag (display:*).
                     */
                    session.setAttribute("sources", urls);

                    if (urls.size() == 1) {
                        // only one source element, let's display automatically the relevant page.
                        SourceBean url = (SourceBean) urls.get(0);
                        String absoluteUrl = url.getSourceBrowserUrl(); // Value();
                        user.setSourceURL (absoluteUrl);
                    } else {
                        user.setSourceURL (null);
                    }
                } // else
            } // if

        } catch (Exception ioe) {
            ioe.printStackTrace();
            throw new JspException ("Fatal error: could not display protein associated source.");
        }
        return EVAL_PAGE;
    } // doEndTag
}