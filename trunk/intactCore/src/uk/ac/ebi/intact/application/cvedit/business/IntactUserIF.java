/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.business;

import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.exception.SessionExpiredException;

import java.util.Collection;
import java.util.Date;
import java.io.Serializable;

/**
 * This interface provides methods specific to a user.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactUserIF extends Serializable {

    /**
     * Return the view of what the user sees on the screen.
     */
    public CvViewBean getView();

    /**
     * Returns the user id of the user used to login into cvedit application.
     */
    public String getUser();

    /**
     * Sets the topic selected by the user.
     *
     * @param topic the selected topic.
     */
    public void setSelectedTopic(String topic);

    /**
     * Return the topic selected by the user.
     */
    public String getSelectedTopic();

    /**
     * Returns the Institution.
     *
     * @exception SearchException for errors in searching; this is also
     *  thrown if the search produced more than a single Institution.
     */
    public Institution getInstitution() throws SearchException;

    // Methods releated to drop down lists

    /**
     * Returns a list of topic names.
     * @return a list of topic names (short labels) to display as a drop
     *  down list from the edit page. If the current editing object is of
     *  CvTopic then the returned list doesn't include it.
     */
    public Collection getTopicList();

    /**
     * Returns a list of database names.
     * @return a list of database names (short labels) to display as a drop
     *  down list from the edit page.
     */
    public Collection getDatabaseList();

    /**
     * Returns a list of qualifier names.
     * @return a list of qualifier names (short labels) to display as a drop
     *  down list from the edit page.
     */
    public Collection getQualifierList();

    /**
     * Returns true if there are no qualifier names.
     * @return true if there are no qualifier names or else true is returned.
     */
    public boolean isQualifierListEmpty();

    /**
     * Refresh the drop down list for the current CV edit object. This involves
     * contruction of a new list by retrieveing matching records from the
     * persistent system. No action is taken if the current edit object
     * is not involved with drop down lists.
     *
     * @exception SearchException for errors in searching the persistent system
     * to update the list.
     */
    public void refreshList() throws SearchException;

    // Transaction Methods

    /**
     * Wrapper to begin a transaction via OJB layer.
     *
     * @exception IntactException for errors in starting a transaction.
     */
    public void begin() throws IntactException;

    /**
     * Wrapper to commit a transaction via OJB layer.
     *
     * @exception IntactException for errors in committing a transaction.
     */
    public void commit() throws IntactException;

    /**
     * A wrapper to roll back a transaction vi OJB layer.
     *
     * @exception IntactException for errors in rolling back a transaction.
     */
    public void rollback() throws IntactException;

    // Persistent Methods

    /**
     * Wrapper to persist an object in the underlying persistence system.
     *
     * @param object the object to create.
     *
     * @exception IntactException for errors in creating an object in the
     * persistent system.
     */
    public void create(Object object) throws IntactException;

    /**
     * Wrapper to update an object in the underlying persistence system.
     *
     * @param object the object to create.
     *
     * @exception IntactException for errors in updating an object in the
     * persistent system.
     */
    public void update(Object object) throws IntactException;

    /**
     * Wrapper to delete an object from the underlying persistence system.
     *
     * @param object the object to delete.
     *
     * @exception IntactException for errors in deleting an object from the
     * persistent system.
     */
    public void delete(Object object) throws IntactException;

    // Other misc methods

    /**
     * Sets the given CV Object as the current object the user is working
     * presently.
     *
     * @param cvobj the CV object to set as the current object the user is
     *  working presently.
     *
     * <pre>
     * post: getCurrentEditObject = cvobj
     * </pre>
     */
    public void setCurrentEditObject(CvObject cvobj);

    /**
     * Returns the current object the user is working presently.
     */
    public CvObject getCurrentEditObject();

    // Search methods

    /**
     * Return an Object by classname and shortLabel.
     *
     * @param clazz the class object to search.
     * @param label the short label to search for.
     *
     * @exception SearchException for errors in searching; this is also
     *  thrown if the search produced more than a single object.
     */
    public Object getObjectByLabel(Class clazz, String label)
        throws SearchException;

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
     * @exception SearchException thrown if problems are encountered during the
     * search process.
     */
    public Collection search(String objectType, String searchParam,
                              String searchValue) throws SearchException;

    /**
     * Sets the status of last search.
     * @param size the size to determine the last search. If size is 1 then
     * the last search is a single entry search. For any other value, the
     * last search produces multiple entries.
     */
    public void setSearchResultStatus(int size);

    /**
     * Returns true if the last search produced a single entry.
     * @return true if the last search produced a single entry.
     */
    public boolean hasSingleSearchResult();

    /**
     * Sets the last search criteria.
     * @param query the last search query.
     */
    public void setSearchQuery(String query);

    /**
     * Returns the last search query.
     * @return the last search query.
     */
    public String getSearchQuery();

    /**
     * Caches the last search result. Each object of <code>results</code> is
     * wrapped as a <code>ListObject</code>.
     * @param results holds the results from the search.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(ListObject))
     * </pre>
     */
    public void cacheSearchResult(Collection results);

    /**
     * Returns the cached search result.
     * @return the cached search result.
     */
    public Collection getCacheSearchResult();

    /**
     * Removes the object with matching AC from search cache.
     * @param ac the accession number of the object to filter.
     */
    public void removeFromSearchCache(String ac);

    // Session methods

    /**
     * Logs off from the application. This will close the connection to
     * the persistent storage.
     *
     * @exception IntactException for problems with logging off.
     */
    public void logoff() throws IntactException;

    /**
     * Returns the session started time.
     */
    public Date loginTime();

    /**
     * Returns the time the use logs off from the application.
     */
    public Date logoffTime();
}
