/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import org.apache.commons.beanutils.DynaBean;
import uk.ac.ebi.intact.application.commons.business.IntactUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.GoServerProxy;
import uk.ac.ebi.intact.util.NewtServerProxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Provides methods specific to a user editing an Annotated object.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface EditUserI extends IntactUserI, Serializable {

    // Get/Set methods for topic selected.
    public void setSelectedTopic(String topic);
    public String getSelectedTopic();

    /**
     * Returns the Institution.
     */
    public Institution getInstitution();

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

    /**
     * Persists the object the user is editing.
     * @exception IntactException for errors in updating the persistent system.
     * @exception SearchException for search errors (unable to find an object
     * to update).
     */
    public void persist() throws IntactException, SearchException;

    /**
     * This method clears the view of the current edit object, remove it from
     * the search cache, deletes from the experiment list (if the current edit
     * is an instance of an Experiment class),  tand finally delete the current
     * edit object.
     * @exception IntactException for errors in deleting the current edit object.
     */
    public void delete() throws IntactException;

    public void cancelEdit();

    /**
     * True if given object is persistent.
     * @param obj the object to check for persistency.
     * @return true if <code>obj</code> is persistent.
     */
    public boolean isPersistent(Object obj);

    /**
     * True if the current edit object is persistent. When a new edit object
     * is created, it is not persistent until the form is submitted.
     * @return true if the current edit object is persistent.
     */
    public boolean isPersistent();

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

    /**
     * Returns the class name of the current view.
     * @return the class name of the current view without the package prefix.
     */
    public String getCurrentViewClass();

    // Search methods

    /**
     * Return an Object by classname and shortLabel.
     *
     * @param className the name of the class to search.
     * @param label the short label to search for.
     *
     * @exception SearchException thrown for a search failure; also thrown
     * if <code>label</code> already exists in <code>className</code>.
     */
    public Object getObjectByLabel(String className, String label)
            throws SearchException;

    /**
     * Return an Object by classname and shortLabel.
     *
     * @param clazz the class object to search.
     * @param label the short label to search for.
     *
     * @exception SearchException thrown for a search failure; also thrown
     * if <code>label</code> occurrs more than once for <code>clazz</code>.
     */
    public Object getObjectByLabel(Class clazz, String label)
            throws SearchException;

    /**
     * Return an Object by ac for given class.
     *
     * @param clazz the class to search for.
     * @param ac the accession number to search for.
     * @return an Object of <code>clazz</code> type for <code>ac</code>.
     *
     * @exception SearchException thrown for a search failure; also thrown
     * if <code>ac</code> occurs more than once for <code>clazz</code>; highly
     * unlikely given that <code>ac</code> is the primary key!
     */
    public Object getObjectByAc(Class clazz, String ac) throws SearchException;

    /**
     * Gets SPTR Proteins via SRS.
     * @param pid the primary id to search for.
     * @return collection of <code>Protein</code> instances for <code>pid</code>.
     */
    public Collection getSPTRProteins(String pid);

    /**
     * Gets a collection of Proteins by xref.
     * @param pid the primary id to search for.
     * @return collection of <code>Protein</code> instances for <code>pid</code>.
     * @throws SearchException thrown for a search failure.
     */
    public Collection getProteinsByXref(String pid) throws SearchException;

    /**
     * This method provides a means of searching intact objects, within the constraints
     * provided by the parameters to the method.
     * <p>
     * This method is named as search1 to avoid conflict with the similar named
     * method (with diffrent exception) of the super interface.
     *
     * @param objectType the object type to be searched
     * @param searchParam the parameter to search on (eg field)
     * @param searchValue the search value to match with the parameter
     * @return the results of the search (empty if no matches were found).
     * @exception SearchException thrown if problems are encountered during the
     * search process.
     */
    public Collection search1(String objectType, String searchParam,
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
     * Returns the latest search input.
     * @return the latest search input. This is basically as same as the
     *{@link #getSearchQuery()} without the search parameter.
     */
    public String getSearchInput();

    /**
     * Returns the latest search query.
     * @return the latest search query.
     */
    public String getSearchQuery();

    /**
     * Returns the class name of the latest search.
     * @return the class name of the latest search as a <code>String</code>.
     */
    public String getSearchClass();

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
//    public void addToSearchCache(AnnotatedObject cvobj);

    /**
     * Updates the search cache with the current edit object; this is required
     * to reflect changes to current edit object's short label.
     */
    public void updateSearchCache();

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
     * Check for duplicity of short label for the current edit object.
     * @param shortlabel the short label to check for duplicity.
     * @return true if <code>shortlabel</code> already exists (for the current edit object)
     * in the database.
     * @exception SearchException for errors in acccessing the database.
     */
    public boolean duplicateShortLabel(String shortlabel) throws SearchException;

    /**
     * Returns a list of existing short labels for the current edit object.
     * @return a list of String objects for short labels of current object type
     * minus the short label of the current edit object.
     * @throws SearchException for errors in search the database for short labels.
     *
     * <pre>
     * pre: results->forall(obj: Object | obj.oclIsTypeOf(String))
     * </pre>
     */
    public List getExistingShortLabels() throws SearchException;

    /**
     * Popluate the given form with search result.
     * @param dynaForm the form to populate.
     */
    public void fillSearchResult(DynaBean dynaForm);

    /**
     * Returns the search result as a list.
     * @return the search result; an empty list is returned if there are no search
     * results.
     *
     * <pre>
     * post: return != Null
     * post: return->forall(obj: Object | obj.oclIsTypeOf(ResultBean))
     * </pre>
     */
    public List getSearchResult();

    /**
     * Returns the Newt server proxy assigned for the current session.
     * @return an instance of Newt server. A new instance is created if no server
     * is created for the current session.
     */
    public NewtServerProxy getNewtProxy();

    /**
     * Returns the Go server proxy assigned for the current session.
     * @return an instance of Go server. A new instance is created if no server
     * is created for the current session.
     */
    public GoServerProxy getGoProxy();

    /**
     * Returns the help tag for the current view bean.
     * @return the help tag for the current view bean as a String object.
     */
    public String getHelpTag();

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
     * Adds the experiment to the currently edited/added experiment list.
     * @param exp the experiment to add to the list.
     */
    public void addToCurrentExperiment(Experiment exp);

    /**
     * Removes the current experiment from the currently edited/added
     * experiment list.
     * @param exp the experiment to remove from the list.
     */
    public void removeFromCurrentExperiment(Experiment exp);

    /**
     * Returns a list of currently edited/added experiments.
     * @return a set consists currently edited/added experiments.
     * An empty set is returned if there are no
     * experiments added or edited during the current session.
     *
     * <pre>
     * post: results->forall(obj: Object | obj.oclIsTypeOf(Experiment))
     * </pre>
     */
    public Set getCurrentExperiments();

    /**
     * Returns an <code>Annotation</code> constructed from the given bean.
     * @param cb the bean to extract information to construct an Anotation.
     * @return an Annotation constructed from <code>cb</code>.
     * @exception SearchException for errors in searching the database.
     */
    public Annotation getAnnotation(CommentBean cb) throws SearchException;

    /**
     * Returns a new instance of <code>Xref</code> constructed from the given bean.
     * @param xb the bean to extract information to construct an Xref.
     * @return an instance of <code>Xref</code> constructed from <code>xb</code>.
     * @exception SearchException for errors in searching the database.
     */
    public Xref getXref(XreferenceBean xb) throws SearchException;
}
