/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.search;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.application.commons.business.IntactUserI;

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
     * Peforms a simple search in the IntAct data for given search class and
     * value. This search is limited to AC and short label fields.
     * @param searchClass the search class to perform the search on
     * (e.g., Experiment)
     * @param query the search query; doesn't support mupltiple comma
     * separated values.
     * @param user the user to invoke search on.
     * @return a collection of Intact objects of same class as
     * <code>searchClass</code> or an empty collection if none found.
     * @throws IntactException for any errors in searching the persistent system.
     */
    public Collection doLookupSimple(String searchClass, String query,
                                     IntactUserI user) throws IntactException;

    /**
     * Search in the IntAct data for a colleciton of object (type=searchClass).
     * The objects found must match with the value given by the user (ac, shortlabel ...).
     *
     * @param searchClass the search class we are looking for in the IntAct data.
     * @param value the queryString for which the objects should match.
     * @param user the IntAct datasource.
     * @return A collection of Intact objects of the type <i>searchClass</i>.
     * @throws IntactException if an erro occurs when searching in the database.
     */
    public Collection doLookup( String searchClass, String value, IntactUserI user ) throws IntactException;

    /**
     * Search in the IntAct data for a colleciton of object (type=searchClass).
     * The objects found must match with the value given by the user (ac, shortlabel ...).
     *
     * @param searchClasses the search classes (ordered by preference) we are
     *        looking for in the IntAct data.
     * @param value the queryString for which the objects should match.
     * @param user the IntAct datasource.
     * @return A collection of Intact objects of the type <i>searchClass</i>.
     * @throws IntactException if an erro occurs when searching in the database.
     */
    public Collection doLookup( List searchClasses, String value, IntactUserI user ) throws IntactException;

    /**
     * Return a collection of search criteria.
     * @return a Collection of <code>CriteriaBean</code>.
     */
    public Collection getSearchCritera();
}
