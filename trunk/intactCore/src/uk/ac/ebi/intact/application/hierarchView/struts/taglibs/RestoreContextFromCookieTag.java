/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.taglibs;

import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * That tag allows to restore the eventual user context of the user
 * from a cookie which has been created and updated by <code>SaveContextInCookieTag</code>.
 *
 * @see SaveContextInCookieTag
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class RestoreContextFromCookieTag extends TagSupport {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /**
     * Skip the body content.
     */
    public int doStartTag() throws JspTagException {
        return SKIP_BODY;
    }

    /**
     * Restore the context if needed.
     */
    public int doEndTag() throws JspException {

        logger.warn("Try to restore the last user context");

        HttpSession session = pageContext.getSession();
        if (session == null) {
            logger.warn("Could not create a new session to restore the setting");
            return EVAL_PAGE;
        }

        IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
        // no point to restore the environment if the user already exists
        if (user != null) {
            logger.info("The user exists, exit the restore procedure");
            return EVAL_PAGE;
        }

        Cookie cookies[] = ((HttpServletRequest) pageContext.getRequest()).getCookies();
        if (cookies == null) return EVAL_PAGE;

        String cookieName = null;
        String AC = null;
        String method = null;
        String depth = null;
        for (int i = 0; i < cookies.length; i++) {
            cookieName = cookies[i].getName();
            logger.info("In the cookie : " + cookieName + " = " + cookies[i].getValue());
            if ("AC".equals(cookieName))
                AC = cookies[i].getValue();
            else if ("depth".equals(cookieName))
                 depth = cookies[i].getValue();
            else if ("method".equals(cookieName))
                 method = cookies[i].getValue();
        }

        String url = null;
        if (AC != null && method != null && depth != null) {
            url = "/hierarchView/display.do?AC="+ AC +"&method="+ method +"&depth="+ depth;
        } else {
            // simply display the current page
            return EVAL_PAGE;
        }

        try {


            // save the URL to forward to
            pageContext.getRequest().setAttribute ("restoreUrl", url);
            session.setAttribute ("restoreUrl", url);

            // redirect to the forward page (with waiting message)
            HttpServletRequest request = ((HttpServletRequest) pageContext.getRequest());
            String forwardUrl = request.getContextPath() + "/pages/restoreContext.jsp";

            logger.info("forward to: " + forwardUrl);

            ((HttpServletResponse) pageContext.getResponse()).sendRedirect(forwardUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EVAL_PAGE;
    }
}