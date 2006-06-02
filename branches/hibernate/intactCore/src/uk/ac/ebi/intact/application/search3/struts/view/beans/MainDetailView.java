/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.search3.struts.view.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-May-2006</pre>
 */
public class MainDetailView  extends AbstractView
{

    /**
     * Log for this class
     */
    public static final Log log = LogFactory.getLog(MainDetailView.class);

    private Experiment experiment;
    private MainDetailViewBean mainDetailViewBean;

    public MainDetailView(HttpServletRequest request, Experiment experiment, String link, String searchUrl)
    {
        super(request);
        this.experiment = experiment;

        // pagination preparation here
        // pagination preparation here
        int totalItems = getTotalItems();
        int maxResults = getItemsPerPage();

        if (getCurrentPage() == 0)
        {
            if (totalItems > getItemsPerPage())
            {
                setCurrentPage(1);
            }
        }

        int firstResult = (getCurrentPage()-1)*getItemsPerPage();

        if (firstResult > totalItems)
        {
            throw new RuntimeException("Page out of bounds: "+getCurrentPage()+" (Item: "+firstResult+" of "+getTotalItems()+")");
        }

        if (totalItems < getItemsPerPage()) firstResult = 0;

        // get the interactions to be shown
        List<Interaction> interactions = DaoFactory.getExperimentDao().getInteractionsForExpereimentWithAc(experiment.getAc(), firstResult, maxResults);

        if (log.isDebugEnabled())
        {
            log.debug("Experiment: "+experiment.getAc()+", showing interactions from "+firstResult+" to "+maxResults);
        }

        this.mainDetailViewBean = new MainDetailViewBean(experiment, interactions, link, searchUrl, request.getContextPath());
    }


    public MainDetailViewBean getMainDetailViewBean()
    {
        return mainDetailViewBean;
    }

    @Override
    public int getTotalItems()
    {
        String prefix = getClass().getName()+"_";

        String attName = prefix+experiment.getAc();

        int totalItems;

        if (getSession().getAttribute(attName) == null)
        {
            totalItems = DaoFactory.getExperimentDao().countInteractionsForExperimentWithAc(experiment.getAc());

            getSession().setAttribute(attName, totalItems);
        }
        else
        {
            totalItems = (Integer) getSession().getAttribute(attName);
        }

        getRequest().setAttribute(SearchConstants.TOTAL_RESULTS_ATT_NAME, totalItems);

        return totalItems;
    }
}
