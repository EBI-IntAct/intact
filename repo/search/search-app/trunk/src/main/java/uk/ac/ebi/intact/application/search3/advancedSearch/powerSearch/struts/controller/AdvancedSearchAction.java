/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.controller;

import uk.ac.ebi.intact.application.search3.struts.controller.SearchActionBase;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.SearchEnvironment;
import uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.business.CvLists;
import uk.ac.ebi.intact.application.commons.util.UrlUtil;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.DynaActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Advanced search action querying directly the Database
 * 
 * @author Bruno Aranda
 * @version $Id$
 */
public class AdvancedSearchAction extends SearchActionBase
{

    private static final Log logger = LogFactory.getLog(AdvancedSearchAction.class);


    /**
     * Process the specified HTTP request, and create the corresponding HTTP response (or forward to another web
     * component that will create it). Return an ActionForward instance describing where and how control should be
     * forwarded, or null if the response has already been completed.
     *
     * @param mapping  - The <code>ActionMapping</code> used to select this instance
     * @param form     - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request  - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the controller servlet, <code>ActionServlet</code>, might be directed
     *         to perform a RequestDispatcher.forward() or HttpServletResponse.sendRedirect() to, as a result of
     *         processing activities of an <code>Action</code> class
     *
     * @throws java.io.IOException      ...
     * @throws javax.servlet.ServletException ...
     */
    @Override
    public ActionForward executeSearch( ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response )
            throws SearchException
    {
            return null;
    }


    public Class<? extends Searchable>[] getSearchableTypes()
    {
        DynaActionForm dyForm = (DynaActionForm) getForm();
        String searchClassString = (String) dyForm.get( "searchObject" );

        getIntactContext().getSession().setAttribute( SearchConstants.SEARCH_CLASS, searchClassString );

        logger.info("Search class: "+searchClassString);

        if (searchClassString.equals("any"))
        {
            return null;
        }

        try
        {
            Class clazz = Class.forName(searchClassString);
            return new Class[] { clazz };
        }
        catch (ClassNotFoundException e)
        {
            throw new IntactException(e.getMessage());
        }
    }

    public SearchableQuery createSearchableQuery()
    {
        DynaActionForm dyForm = (DynaActionForm) getForm();
        String searchClassString = (String) dyForm.get( "searchObject" );
        String ac = (String) dyForm.get( "acNumber" );
        String shortlabel = (String) dyForm.get( "shortlabel" );
        String description = (String) dyForm.get( "description" );
        String fulltext = (String) dyForm.get( "fulltext" );
        String annotation = (String) dyForm.get( "annotation" );
        String cvTopic = (String) dyForm.get( "cvTopic" );
        String xref = (String) dyForm.get( "xRef" );
        String cvDB = (String) dyForm.get( "cvDB" );
        //String iqlStmt = (String) dyForm.get( "iqlStatement" );
        String cvInteraction = (String) dyForm.get( "cvInteraction" );
        String cvInteractionType = (String) dyForm.get( "cvInteractionType" );
        String cvIdentification = (String) dyForm.get( "cvIdentification" );

        cvDB = cvDB.trim();
        cvTopic = cvTopic.trim();

        logger.info( "searchClass: " + searchClassString );
        logger.info( "acs: " + ac );
        logger.info( "shortlabel: " + shortlabel );
        logger.info( "description: " + description );
        logger.info( "annotation: " + annotation );
        logger.info( "cvtopic: " + cvTopic );
        logger.info( "xref: " + xref );
        logger.info( "cvDB: " + cvDB );
        logger.info( "cvInteraction: " + cvInteraction );
        logger.info( "cvInteractionType: " + cvInteractionType );
        logger.info( "cvIdentification: " + cvIdentification );

        // create the searchable query object
        SearchableQuery searchableQuery = new SearchableQuery();

        if (ac != null && ac.length() > 0)
        {
            searchableQuery.setAcs(commaSeparatedListToArray(ac));
        }
        searchableQuery.setShortLabel(shortlabel);
        searchableQuery.setDescription(description);
        searchableQuery.setAnnotationText(annotation);
        searchableQuery.setXref(xref);

        if (!cvTopic.equalsIgnoreCase(CvLists.ALL_TOPICS_SELECTED))
        {
            searchableQuery.setCvTopicLabel(cvTopic);
        }

        if (!cvDB.equalsIgnoreCase(CvLists.ALL_DATABASES_SELECTED)) {
            searchableQuery.setCvDatabaseLabel(cvDB);
        }

        if (!cvInteraction.equalsIgnoreCase(CvLists.NO_CV_INTERACTION_SELECTED))
        {
            searchableQuery.setCvInteractionLabel(cvInteraction);
        }

        if (!cvInteractionType.equalsIgnoreCase(CvLists.NO_CV_INTERACTION_TYPE_SELECTED))
        {
            searchableQuery.setCvInteractionTypeLabel(cvInteractionType);
        }

        if (!cvIdentification.equalsIgnoreCase(CvLists.NO_CV_IDENTIFICATION_SELECTED))
        {
            searchableQuery.setCvIdentificationLabel(cvIdentification);
        }

        return searchableQuery;
    }
}
