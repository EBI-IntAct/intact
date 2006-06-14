/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.imex.psivalidator.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.imex.psivalidator.PsiReportBuilder;

/**
 * This filter, configured in the web.xml file, checks if the session is valid. If not,
 * redirects to a session timed out page
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Jun-2006</pre>
 */
public class SessionExpiredFilter implements Filter
{
    private static final Log log = LogFactory.getLog(PsiReportBuilder.class);

    /**
     * The page to redirect when the session is not valid
     */
    public static final String PAGE = "/sessionTimedOut.jsp";

    /**
     * FilterConfig, that contains the configuration parameters of the filter (not used)
     */
    private FilterConfig fc;

    public void destroy()
    {
        this.fc = null;
    }


    public void init(FilterConfig fc) throws ServletException
    {
        this.fc = fc;
    }


    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException
    {

        HttpServletRequest servletRequest = (HttpServletRequest) request;

        // we check if the session is valid
        if (servletRequest != null && !servletRequest.isRequestedSessionIdValid())
        {
            // we will redirect if we come from a jsf file only
            // (in this application I know that files different to JSP redirect to the main page
            // or is the session timed out page).
            String referer = servletRequest.getHeader("Referer");
            if (referer != null && referer.endsWith(".jsf")) {
       
                // redirect to the session timed out page
                RequestDispatcher rd = request.getRequestDispatcher(PAGE);
                rd.forward(request, response);
            }
        }
        else
        {
            chain.doFilter(request, response);
        }
    }
}
