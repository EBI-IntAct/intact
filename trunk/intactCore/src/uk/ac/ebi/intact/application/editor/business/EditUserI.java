/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.DuplicateLabelException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.io.Serializable;

import org.apache.commons.beanutils.DynaBean;

/**
 * Provides methods specific to a user editing an Annotated object.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface EditUserI extends Serializable {

    /**
     * An empty list only contains this item.
     */
    public static final String EMPTY_LIST_ITEM = "---";

    /**
     * Retturns the user name.
     * @return the user name of the current user.
     */
    public String getUser();

    // Get/Set methods for topic selected.
    public void setSelectedTopic(String topic);

    public String getSelectedTopic();

    /**
     * Returns the Institution.
     * @exception DuplicateLabelException thrown if the search produced more
     * than a single Institution.
     */
    public Institution getInstitution() throws DuplicateLabelException;

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

    public void begin() throws IntactException;

    public void commit() throws IntactException;

    public void rollback() throws IntactException;

    // Persistent Methods

    public void create(Object object) throws IntactException;

    public void update(Object object) throws IntactException;

    public void delete(Object object) throws IntactException;

    public void cancelEdit();

    // The current view.

    /**
     * Returns the user's current edit view.
     * @return user's current edit view.
     */
    public AbstractEditViewBean getView();

    /**
     * Updates the current Annotated object the user is about to edit.
     * @param annot the <code>AnnotatedObject</code> to set as the current
     * object the user is working presently.
     */
    public void updateView(AnnotatedObject annot);

    // Search methods

    /**
     * Return an Object by classname and shortLabel.
     *
     * @param clazz the class object to search.
     * @param label the short label to search for.
     *
     * @exception DuplicateLabelException thrown if the search produced more
     * than a single object.
     */
    public Object getObjectByLabel(Class clazz, String label)
            throws DuplicateLabelException;

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
//    public Collection getSearchCache();

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
     * @param cvobj the <code>AnnotatedObject</code> object to add to the cache.
     */
    public void addToSearchCache(AnnotatedObject cvobj);

    /**
     * Updates the search cache with the current edit object; this is required
     * to reflect changes to current edit object's short label.
     */
    public void updateSearchCache();

    /**
     * Removes the current edit object from the search cache.
     */
    public void removeFromSearchCache();

    /**
     * Returns a unique short label.
     * @param shortlabel the new short label.
     * @return a unique short label as a <code>String</code> instance. This
     * could be <code>shortlabel</code> if it is unique or current object's
     * AC for otherwise.
     * @exception SearchException for problems with searching the database.
     *
     * @see #getUniqueShortLabel(String, String)
     */
    public String getUniqueShortLabel(String shortlabel) throws SearchException;

    /**
     * Returns a unique short label.
     * @param shortlabel the new short label.
     * @param extAc the external ac to be used if <code>shortlabel</code> is not
     * unique.
     * @return a unique short label as a <code>String</code> instance.
     * @exception SearchException for problems with searching the database.
     *
     * @see uk.ac.ebi.intact.util.GoTools#getUniqueShortLabel(
            * uk.ac.ebi.intact.business.IntactHelper, Class,
            * uk.ac.ebi.intact.model.AnnotatedObject, String, String)
     */
    public String getUniqueShortLabel(String shortlabel, String extAc)
            throws SearchException;

    // Methods to create forms

    /**
     * Returns a new DynaBean instance for given form name. This new bean is
     * stored in <code>request</code> object.
     * @param formName the name of the form configured in the struts
     * configuration file.
     * @param request the HTTP request to get the application configuration.
     * The new bean is stored in this object under <code>formName</code>.
     * @return a <code>DynaBean</code> instance.
     * @throws InstantiationException errors in creating the bean
     * @throws IllegalAccessException errors in creating the bean
     */
    public DynaBean createForm(String formName, HttpServletRequest request)
            throws InstantiationException, IllegalAccessException;

    /**
     * Popluate the given form with search result.
     * @param dynaForm the form to populate.
     */
    public void populateSearchResult(DynaBean dynaForm);

    // Session methods

    /**
     * Logs off from the application. This will close the connection to
     * the persistent storage.
     * @exception IntactException for problems with logging off.
     */
    public void logoff() throws IntactException;

    public Date loginTime();

    public Date logoffTime();
}
