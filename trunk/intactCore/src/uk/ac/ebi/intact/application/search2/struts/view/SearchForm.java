/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.view;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * This class holds the data from the searching an Intact object. It is basically
 * a wrapper for a String object, since currently no disitinction
 * is made between different search criteria (eg AC, label etc).
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class SearchForm extends ActionForm {

    /**
     * The value specified in the web page to be searched on
     */
    private String searchString;

    /**
     * The class (intact format) to be searched
     */
    private String className;

    /**
     * @param searchVal
     */
    public void setSearchString(String searchVal) {
        searchString = searchVal;
    }

    /**
     * @return the search string obtained from the user form
     */
    public String getSearchString() {
        return searchString;
    }

    public void setClassName(String name) {

        className = name;
    }

    public String getClassName() {

        return className;
    }

    /**
     *  this method of the <code>Actionform</code> class should be overriden
     * in subclasses to ensure Bean properties can be reset before they are re-populated
     * in subsequent requests
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        searchString = null;
    }
}
