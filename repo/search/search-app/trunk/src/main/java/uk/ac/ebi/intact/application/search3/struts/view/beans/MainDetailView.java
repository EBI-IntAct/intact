/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.search3.struts.view.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    public MainDetailView(HttpServletRequest request, Experiment experiment)
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

        int firstResult = (getCurrentPage() - 1) * getItemsPerPage();

        if (firstResult > totalItems)
        {
            throw new RuntimeException("Page out of bounds: " + getCurrentPage() + " (Item: " + firstResult + " of " + getTotalItems() + ")");
        }

        if (totalItems <= getItemsPerPage()) firstResult = 0;

        // get the interactions to be shown
        // When coming from the partners view, the interactions queried have to be placed in the first position if we are in the first page.
        // We exclude those interactions explicitly from the paginated search, and we will load them directly, and add them to the list
        List<Interaction> interactions = new ArrayList<Interaction>();

        String[] priorInteractionAcs = new String[0];

        // only we will put the searched at the beginning if we are in the first page (or the view is not paginated)
        if (getCurrentPage() <= 1)
        {
            Object objSearchableQuery = request.getSession().getAttribute(SearchConstants.SEARCH_CRITERIA);

            String searched = null;

            if (objSearchableQuery != null)
            {
                searched = objSearchableQuery.toString();

                // FIXME not only the search is an AC. Now, if another thing is used, the interaction won't be placed prominently
                priorInteractionAcs = searched.replaceAll("'", "").split(",");
            }

            if (log.isDebugEnabled())
            {
                for (String ac : priorInteractionAcs)
                {
                    log.info("Interaction placed prominently: " + ac);
                }
            }


            for (String priorAc : priorInteractionAcs)
            {
                Interaction inter = getDaoFactory().getInteractionDao().getByAc(priorAc);
                if (inter != null)
                {
                    interactions.add(inter);
                }
            }
        }

        // we load the rest of interactions for that experiment
        // if we are in the first page
        interactions.addAll(getDaoFactory().getExperimentDao()
                .getInteractionsForExperimentWithAcExcluding(experiment.getAc(), priorInteractionAcs, firstResult, maxResults - priorInteractionAcs.length));


        if (log.isDebugEnabled())
        {
            log.debug("Experiment: " + experiment.getAc() + ", showing interactions from " + firstResult + " to " + maxResults);
        }

        // if specific interactions for this experiment are searched, fetch them from the database

        this.mainDetailViewBean = new MainDetailViewBean(experiment, interactions );
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
            totalItems = getDaoFactory().getExperimentDao().countInteractionsForExperimentWithAc(experiment.getAc());

            getSession().setAttribute(attName, totalItems);
        }
        else
        {
            totalItems = (Integer) getSession().getAttribute(attName);
        }

        getRequest().setAttribute(SearchConstants.TOTAL_RESULTS_ATT_NAME, totalItems);

        return totalItems;
    }

    private DaoFactory getDaoFactory()
    {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }
}
