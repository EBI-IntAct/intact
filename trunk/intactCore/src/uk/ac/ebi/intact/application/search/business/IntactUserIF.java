/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.business;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.util.XmlBuilder;

import java.util.Collection;

/**
 * This interface represents an Intact user.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), modified by Chris Lewington
 * @version $Id$
 */
public interface IntactUserIF {

    /**
     * Return the search criteria.
     * @return the search criteria as a <code>String</code> object.
     */
    public String getSearchCritera();

    /**
     * This method provides a means of searching intact objects, within the constraints
     * provided by the parameters to the method.
     *
     * @param objectType the object type to be searched
     * @param searchParam the parameter to search on (eg field)
     * @param searchValue the search value to match with the parameter
     *
     * @return the results of the search (empty if no matches were found).
     *
     * @exception IntactException thrown if problems are encountered during the
     * search process.
     */
    public Collection search(String objectType, String searchParam,
                              String searchValue) throws IntactException;

    /**
     * Convenience method to provide general access to a User's IntactHelper.
     *
     * @return IntactHelper the helper instance for this user
     */
    public IntactHelper getHelper();

    /**
     * Provides a builder class for generating and manipulating XML as rewuired.
     * @return
     */
    public XmlBuilder getXmlBuilder();
}
