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

    // Getter/Setter for topic selected.
    public String getSelectedTopic();
    public void setSelectedTopic(String topic);

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

    /**
     * Starts editing session. This is needed for Save & Continue operation as
     * editing is turned off automatically upon comitting.
     */
    public void startEditing();

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
     * Sets the view using given object.
     * @param obj either an Annotated object or a Class. The class type is used
     * when creating a view for a new object. For an existing object,
     * AnnotatedObject is used.
     */
    public void setView(Object obj);

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
     * Returns the last protein parse exception.
     * @return the last protein parse exception`.
     */
    public Exception getProteinParseException();

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
     * @return Collection the results of the search - an empty Collection if no
     *  results found
     * @exception SearchException thrown if there were any search problems
     */
    public Collection lookup(String className, String value) throws SearchException;

    /**
     * Returns the latest search query.
     * @return the latest search query.
     */
    public String getSearchQuery();

    /**
     * Collection of AnnotatedObjects to add to the search cache. Previous
     * results are removed before adding the new result.
     * @param results a collection of <code>AnnotatedObjects</code> from
     * the search.
     *
     * <pre>
     * pre: results->forall(obj: Object | obj.oclIsTypeOf(AnnotatedObjects))
     * </pre>
     */
    public void addToSearchCache(Collection results);

    /**
     * Clears existing search cache and replace it with given bean.
     * @param annotobj the AnnotatedObject to set as the search cache.
     */
    public void updateSearchCache(AnnotatedObject annotobj);

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
            * uk.ac.ebi.intact.business.IntactHelper, Class, String, String, String)
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
    public boolean shortLabelExists(String shortlabel) throws SearchException;

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
     * Adds the interaction to the currently edited/added interaction list.
     * @param intact the interaction to add to the list.
     */
    public void addToCurrentInteraction(Interaction intact);

    /**
     * Removes the current interaction from the currently edited/added
     * interaction list.
     * @param intact the interaction to remove from the list.
     */
    public void removeFromCurrentInteraction(Interaction intact);

    /**
     * Returns a list of currently edited/added interactions.
     * @return a set consists currently edited/added interactions.
     * An empty set is returned if there are no
     * interactions added or edited during the current session.
     *
     * <pre>
     * post: results->forall(obj: Object | obj.oclIsTypeOf(Interaction))
     * </pre>
     */
    public Set getCurrentInteractions();

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

    /**
     * Releases the lock held by the user.
     */
    public void releaseLock();
    
    /**
     * Returns the BioSource for given tax id.
     * @param taxId the tax id to get the BioSOurce for.
     * @return the BioSource for <code>taxid</code> or <code>null</code> if
     * none found. 
     * @throws SearchException for errors in searching for the tax id.
     */
	public BioSource getBioSourceByTaxId(String taxId) throws SearchException;
}
