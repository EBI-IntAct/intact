/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.business;

import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.simpleGraph.*;


import java.util.*;

public interface SearchI {

    /**
     *  This method provides a means of searching intact objects, within the constraints
     * provided by the parameters to the method.
     *
     * @param objectType - the object type to be searched
     * @param searchParam - the parameter to search on (eg field)
     * @param searchValue - the search value to match with the parameter
     * @param includeSubClass - if true, known subclasses of objectType will be searched, too.
     * @param matchSubString - if true, substring matches of searchValue to object properties will also be hits.
     *
     * @return Collection - the results of the search (empty Collection if no matches found)
     *
     * @exception IntactException - thrown if problems are encountered during the search process
     */
    public Collection paramSearch(String objectType, String searchParam, String searchValue,
                                  boolean includeSubClass,
                                  boolean matchSubString) throws IntactException;


    /**
     * Searches objects by searchString in all "relevant" fields.
     * The definition of "relevant" depends on the class.
     * Resulting objects are ordered on descending relevance.
     *
     * @param objectType - the object type to be searched
     * @param searchString - the String to search for
     * @param includeSubClass - if true, known subclasses of objectType will be searched, too.
     * @param matchSubString - if true, substring matches of searchValue to object properties will also be hits.
     *
     * @return List - the results of the search (empty List if no matches found)
     *
     * @exception IntactException - thrown if problems are encountered during the search process
     */
    public List stringSearch(String objectType, String searchString,
                             boolean includeSubClass,
                             boolean matchSubString) throws IntactException;
}
