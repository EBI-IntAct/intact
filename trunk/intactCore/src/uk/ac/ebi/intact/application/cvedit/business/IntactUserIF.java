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
     * An empty list only contains this item.
     */
    public static final String EMPTY_LIST_ITEM = "-------------";

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

    /**
     * Returns the state of editing.
     * @return <code>true</code> if the user is in edit screen;
     * <code>false</code> is returned for all other instances.
     */
    public boolean isEditing();

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

    /**
     * Cancels the current editing session.
     */
    public void cancelEdit();

    // Other misc methods

    /**
     * Updates the current CV object with the new CV object.
     * @param cvobj the CV object to set as the current object the user is
     *  working presently.
     */
    public void updateView(CvObject cvobj);

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
     * @return the results of the search (empty if no matches were found).
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
     * Returns the last search query.
     * @return the last search query.
     */
    public String getLastSearchQuery();

    /**
     * Sets the last search query.
     * @param searchParam the search parameter; ex., ac, shortLabel
     * @param searchValue the search query.
     */
    public void setLastSearchQuery(String searchParam, String searchValue);

    /**
     * Returns the class name of the last search.
     * @return the class name of the last search as a <code>String</code>.
     */
    public String getLastSearchClass();

    /**
     * Sets the class name of the last search.
     * @param classname class name of the last search as a <code>String</code>.
     */
    public void setLastSearchClass(String classname);

    /**
     * Returns the cached search result.
     * @return the cached search result as an array of <code>ResultBean</code>
     * objects.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(ResultBean))
     * </pre>
     */
    public Collection getSearchCache();

    /**
     * Caches the last search result. Each object of <code>results</code> is
     * wrapped as a <code>ResultBean</code>.
     * @param results a collection of result beans from the search.
     *
     * <pre>
     * pre: results->forall(obj: Object | obj.oclIsTypeOf(ResultBean))
     * </pre>
     */
    public void addToSearchCache(Collection results);

    /**
     * Adds a new result item to the cache.
     * @param cvobj the CV object to add to the cache.
     */
    public void addToSearchCache(CvObject cvobj);

    /**
     * Updates the search cache; this is equivalent to removing the presvious
     * cache result (using getAC()) and adding <code>cvobj</code> again.
     * @param cvobj the CV object to update the search cache. The cache must
     * contain an object whose AC matches with this instance's AC.
     *
     * <pre>
     * pre: searchCache->includes(cvobj.ac)
     * </pre>
     */
    public void updateSearchCache(CvObject cvobj);

    /**
     * Removes the object with matching AC from search cache.
     * @param ac the accession number of the object to filter.
     */
    public void removeFromSearchCache(String ac);

    /**
     * Returns a unique short label.
     * @param clazz the <code>Class</code> to get the unique short label for.
     * @param shortlabel the new short label.
     * @return a unique short label as a <code>String</code> instance.
     * @exception SearchException for problems with searching the database.
     *
     * @see uk.ac.ebi.intact.util.GoTools#getUniqueShortLabel(
     * uk.ac.ebi.intact.business.IntactHelper, Class,
     * uk.ac.ebi.intact.model.AnnotatedObject, String, String)
     */
    public String getUniqueShortLabel(Class clazz, String shortlabel)
            throws SearchException;

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
