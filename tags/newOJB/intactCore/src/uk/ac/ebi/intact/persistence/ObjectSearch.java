/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import org.exolab.castor.jdo.OQLQuery;

import java.util.*;

/**
 * This class contains methods for performing specific object searches for
 * particular intact object types, based on an object's id or name.
 *
 * @author Sharmila
 */

public class ObjectSearch {

    /** A vector of objects that match the search criterai */
    protected Collection searchResults = null;

    /** The query string used to search for the objects */
    protected String searchString = null;

    /** The search string to be used  */
    protected String searchValue = null;

    /**
     * Default Constructor. Constructs an ObjectSearch.
     */
    public ObjectSearch() {
    }

    /**
     * Sets the search string based on an interaction accessionNumber
     *
     * @param key   the accessionNumber or Name
     */
    public void setInteractionAcSearch() {
        searchString = "SELECT i FROM java.uk.ac.ebi.intact.struts.data.Interaction i WHERE ac=$1";
    }
    public void setMoleculeAcSearch() {
        searchString = "SELECT b FROM java.uk.ac.ebi.intact.struts.data.Molecule b WHERE ac=$1";
    }
    public void setConditionAcSearch( ) {
        searchString = "SELECT b FROM java.uk.ac.ebi.intact.struts.data.Condition b WHERE ac=$1";
    }
    public void setInteractionNameSearch() {
        searchString = "SELECT i FROM java.uk.ac.ebi.intact.struts.data.Interaction i WHERE name=$1";
    }
    public void setMoleculeNameSearch( ) {
        searchString = "SELECT i FROM java.uk.ac.ebi.intact.struts.data.Molecule i WHERE name=$1";
    }
    public void setConditionNameSearch() {
        searchString = "SELECT i FROM java.uk.ac.ebi.intact.struts.data.Condition i WHERE name=$1";

    }

   /**
    *    Sets a vector of the search results to be paged.  The results vector
    *  is created by the datamodel object search.
    *
    *  @param results A Vector of search results
    */
   public void setsearchResults( Collection results )
   {
      searchString      = null;
      searchResults     = results;
   }

   /**
    *    Returns the entire result vector
    *
    *  @return A vector of the entire search result set.
    */
   public Collection getsearchResults()
   {
      return searchResults;
   }

   /**
    *    Returns the search string used to get the result set.
    *
    *  @return A sql string used to pull the results from the database.
    */
   public String getSearchString()
   {
      return searchString;
   }

    public String toString() {

        return searchResults.toString();
    }


}
