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
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Jun-2006</pre>
 */
public class SessionExpiredFilter implements Filter
{
    private static final Log log = LogFactory.getLog(PsiReportBuilder.class);

    public static final String PAGE = "/sessionTimedOut.jsp";
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




        if (servletRequest != null && !servletRequest.isRequestedSessionIdValid())
        {
            String referer = servletRequest.getHeader("Referer");
            if (referer != null && referer.endsWith(".jsf")) {
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
