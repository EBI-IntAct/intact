/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.search3.struts.view.beans;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-May-2006</pre>
 */
public abstract class AbstractView
{

    /**
     * Log for this class
     */
    public static final Log log = LogFactory.getLog(AbstractView.class);

    private HttpServletRequest request;
    private HttpSession session;

    public AbstractView(HttpServletRequest request)
    {
        this.request = request;
        this.session = request.getSession();
    }

    public int getCurrentPage(){
        int currentPage = 0;

        String strPage = request.getParameter("page");

        if (strPage != null && strPage.length() != 0)
        {
            currentPage = Integer.valueOf(strPage);
        }

        return currentPage;
    }

    public void setCurrentPage(int page){
        request.getParameterMap().put("page", page);
    }

    public int getItemsPerPage() {
        return SearchConstants.RESULTS_PER_PAGE;
    }

    public abstract int getTotalItems();

    protected HttpServletRequest getRequest()
    {
        return request;
    }

    protected HttpSession getSession()
    {
        return session;
    }

}
