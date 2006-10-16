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
package uk.ac.ebi.intact.webapp.search.advancedSearch.powerSearch.struts.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;
import uk.ac.ebi.intact.webapp.search.advancedSearch.powerSearch.struts.business.CvLists;
import uk.ac.ebi.intact.webapp.search.struts.controller.SearchActionBase;
import uk.ac.ebi.intact.webapp.search.struts.util.SearchConstants;

/**
 * Advanced search action querying directly the Database
 * 
 * @author Bruno Aranda
 * @version $Id$
 */
public class AdvancedSearchAction extends SearchActionBase
{

    private static final Log logger = LogFactory.getLog(AdvancedSearchAction.class);


    public Class<? extends Searchable>[] getSearchableTypes()
    {
        DynaActionForm dyForm = (DynaActionForm) getForm();
        String searchClassString = (String) dyForm.get( "searchObject" );

        getIntactContext().getSession().setAttribute( SearchConstants.SEARCH_CLASS, searchClassString );

        logger.debug("Search class: "+searchClassString);

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

        logger.debug( "searchClass: " + searchClassString );
        logger.debug( "acs: " + ac );
        logger.debug( "shortlabel: " + shortlabel );
        logger.debug( "description: " + description );
        logger.debug( "annotation: " + annotation );
        logger.debug( "cvtopic: " + cvTopic );
        logger.debug( "xref: " + xref );
        logger.debug( "cvDB: " + cvDB );
        logger.debug( "cvInteraction: " + cvInteraction );
        logger.debug( "cvInteractionType: " + cvInteractionType );
        logger.debug( "cvIdentification: " + cvIdentification );

        // create the searchable query object
        SearchableQuery searchableQuery = new SearchableQuery();

        searchableQuery.setAc(ac);
        searchableQuery.setShortLabel(shortlabel);
        searchableQuery.setFullText(fulltext);
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
