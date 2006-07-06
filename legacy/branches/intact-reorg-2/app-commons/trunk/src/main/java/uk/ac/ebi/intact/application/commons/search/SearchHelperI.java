/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.search;

import org.apache.ojb.broker.query.Query;
import uk.ac.ebi.intact.application.commons.business.IntactUserI;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.IntactObject;

import java.util.Collection;
import java.util.List;

/**
 * Interface describing how to search data in IntAct.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public interface SearchHelperI {

    /**
     * Search in the IntAct data for a colleciton of object (type=searchClass). The objects found
     * must match with the value given by the user (ac, shortlabel ...).
     *
     * @param searchClass the search class we are looking for in the IntAct data.
     * @param value       the queryString for which the objects should match.
     * @param user        the IntAct datasource.
     * @return A collection of Intact objects of the type <i>searchClass</i>.
     * @throws IntactException if an erro occurs when searching in the database.
     */
    public Collection<IntactObject> doLookup(SearchClass searchClass, String value, IntactUserI user)
            throws IntactException;

    /**
     * Search in the IntAct data for a colleciton of object (type=searchClass). The objects found
     * must match with the value given by the user (ac, shortlabel ...).
     *
     * @param searchClasses the search classes (ordered by preference) we are looking for in the
     *                      IntAct data.
     * @param value         the queryString for which the objects should match.
     * @param user          the IntAct datasource.
     * @return A collection of Intact objects of the type <i>searchClass</i>.
     * @throws IntactException if an erro occurs when searching in the database.
     */
    public Collection<IntactObject> doLookup(List<SearchClass> searchClasses, String value, IntactUserI user)
            throws IntactException;

    /**
     * Return a collection of search criteria.
     *
     * @return a Collection of <code>CriteriaBean</code>.
     */
    public Collection getSearchCritera();

    /**
     * Returns a result wrapper which contains the result for given search type. The search uses the
     * OQL.
     *
     * @param searchClass the class to search for. Eg., Experiment. It has to be a <b>concrete</b>
     *                    class not an interface.
     * @param searchParam the search criteria to search. Eg., ac, shortLabel
     * @param searchValue the search value. Eg., ga-*
     * @param max         the maximum number of entries to retrieve
     * @return the result wrapper which contains the result of the search
     * @throws IntactException for errors in searching for persistent system. This is not thrown if
     *                         the search produces no output.
     */
    public ResultWrapper searchByQuery(SearchClass searchClass, String searchParam, String searchValue,
                                       int max) throws IntactException;

    /**
     * Returns a result wrapper which contains the result for given search type. The search uses the
     * OQL.
     *
     * @param queries an array of queries, The first query is the query to count
     * the search result. The second query is to do the actual search.
     * @param max the maximum number of entries to retrieve
     * @return the result wrapper which contains the result of the search.
     * @throws IntactException for errors in searching for persistent system.
     * This is not thrown if the search produces no output.
     */
    public ResultWrapper searchByQuery(Query[] queries, int max) throws IntactException;

    /**
     * Returns true if the DataResource behind the SearchHelper is available, return false if not
     *
     * @return true if the DataSource is aailable
     */
    public boolean connected();


}
