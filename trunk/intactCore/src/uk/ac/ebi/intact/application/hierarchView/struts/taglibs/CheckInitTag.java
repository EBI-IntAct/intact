/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUser;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.WebServiceManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * That class allow to check if the initialisation has been properly done.
 * Prevents the user to use the application in that state.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public class CheckInitTag  extends TagSupport {

    // LOGGER
    private static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    private String forwardOnError;

    public String getForwardOnError() {
        return forwardOnError;
    }

    public void setForwardOnError(String forwardOnError) {
        this.forwardOnError = forwardOnError;
    }


    public int doStartTag() throws JspTagException {
        // evaluate the tag's body content and any sub-tag
        return SKIP_BODY;
    } // doStartTag


    /**
     * Called when the JSP encounters the end of a tag.
     * This will check if the datasource and the web service are OK.
     */
    public int doEndTag() throws JspException {

        // check the datasource
        HttpSession session = pageContext.getSession();
        IntactUser user = (IntactUser) session.getAttribute (Constants.USER_KEY);

        if (null == user) {
            logger.error ("Data source unavailable, forward to home page");
            // user doesn't exists
            try {
                // FORWARD
                super.pageContext.forward (this.forwardOnError);
                return SKIP_PAGE;
            } catch (ServletException e) {
                logger.error (e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                logger.error (e.toString());
                e.printStackTrace();
            }
        }

        // check the web service deployment
        ServletContext servletContext = pageContext.getServletContext();
        WebServiceManager webServiceManager =
                (WebServiceManager) servletContext.getAttribute (Constants.WEB_SERVICE_MANAGER);

        if ((null == webServiceManager) || (false == webServiceManager.isRunning() )) {
            logger.error ("Web Service not properly deployed, forward to home page");
            try {
                // FORWARD
                super.pageContext.forward (this.forwardOnError);
                return SKIP_PAGE;
            } catch (ServletException e) {
                logger.error (e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                logger.error (e.toString());
                e.printStackTrace();
            }
        }

        return EVAL_PAGE;

    } // doEndTag

} // CheckInitTag
