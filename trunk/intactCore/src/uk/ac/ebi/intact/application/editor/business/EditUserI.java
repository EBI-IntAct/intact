/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
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
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractROViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;

import java.util.Collection;
import java.util.Date;
import java.io.Serializable;

import org.apache.commons.beanutils.DynaBean;

import javax.servlet.http.HttpServletRequest;

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
//    public static final String EMPTY_LIST_ITEM = "---";

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

    // Methods to create forms.

    /**
     * Returns a DynaBean form constructed using given form name and Http request.
     * @param formName the name of the form.
     * @param request Http to extract the information to construct the form.
     * @return the DynaBean form; the form specification must be defined in the
     * struts configuration file under <code>name</code>. A cached form is
     * returned if a form exists in the local cache saved under
     * <code>formName</code>. A null form is retured for any errors (such
     * as unable to construct an instance).
     */
    public DynaBean getDynaBean(String formName, HttpServletRequest request);

    /**
     * Returns a EditForm constructed using given form name
     * @param formName the name of the form.
     * @return the <code>EditForm</code> form. A cached form is
     * returned if a form exists in the local cache saved under
     * <code>formName</code> or else a new form is created and
     * saved in the cache before returning it.
     */
    public EditForm getEditForm(String formName);

    // Search methods

    /**
     * Return an Object by classname and shortLabel.
     *
     * @param clazz the class object to search.
     * @param label the short label to search for.
     *
     * @exception IntactException thrown for search errors.
     */
    public Object getObjectByLabel(Class clazz, String label)
            throws IntactException;

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
     * Utility method to handle the logic for lookup, ie trying AC, label etc.
     *
     * @param className the intact type to search on
     * @param value the user-specified value.
     * @param cache cache the search result if <code>true</code>; generally,
     * this flag is set to true from a Search action.
     * @return Collection the results of the search - an empty Collection if no
     *  results found
     * @exception SearchException thrown if there were any search problems
     */
    public Collection lookup(String className, String value, boolean cache)
            throws SearchException;

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
     * Returns the class name of the last search.
     * @return the class name of the last search as a <code>String</code>.
     */
    public String getLastSearchClass();

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
     * @param cvobj the <code>AnnotatedObject</code> object to add to the cache if
     * <code>cvobj</code> is of as same type as existing items in the search cache (
     * avoid mixing diffrent types in the search cache).
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

    /**
     * Popluate the given form with search result.
     * @param dynaForm the form to populate.
     */
    public void fillSearchResult(DynaBean dynaForm);

    // Session methods

    /**
     * Logs off from the application. This will close the connection to
     * the persistent storage.
     * @exception IntactException for problems with logging off.
     */
    public void logoff() throws IntactException;

    public Date loginTime();

    public Date logoffTime();

    /**
     * Returns the read only view.
     * @param clazz the Class to return the view for.
     * @param shortLabel the short label to search the database for.
     * @return the read only view for <code>shortLabel</code> of
     * <code>clazz</code>.
     * @throws IntactException for errors in searching the database.
     */
    public AbstractROViewBean getReadOnlyView(Class clazz, String shortLabel)
            throws IntactException;
}
