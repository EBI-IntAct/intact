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
     * The name of the topic list.
     */
    public static final String TOPIC_NAMES = "TopicNames";

    /**
     * The name of the database list.
     */
    public static final String DB_NAMES = "DatabaseNames";

    /**
     * The name of qualifier list.
     */
    public static final String QUALIFIER_NAMES = "QualifierNames";

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
     * Returns the list for given list name. Lists are cached to provide
     * efficient list constructions.
     *
     * @param name the name of the list.
     *
     * <pre>
     * pre: name.equals(TOPIC_NAMES) or name.equals(DB_NAMES) or
     *      name.equals(QUALIFIER_NAMES)
     * post: return != null
     * post: return.notEmpty
     * post: return->forall(obj : Object | obj.oclIsTypeOf(String))
     * </pre>
     */
    public Collection getList(String name);

    /**
     * Returns <code>true</code> only if the list for given name contains
     * a single item and that item equals to the empty list item identifier.
     *
     * @param name the name of the list.
     *
     * <pre>
     * pre: name.equals(TOPIC_NAMES) or name.equals(DB_NAMES) or
     *      name.equals(QUALIFIER_NAMES)
     * </pre>
     */
    public boolean isListEmpty(String name);

    /**
     * Update lists for given name. This involves contruction of a new list
     * by retrieveing matching records from the persistent system. No action
     * is taken if <code>clazz</code> is not of valid list type.
     *
     * @param clazz the type to update the list. The list only updates for
     * the following types: CvTopic, CvDatabase and CvXrefQualifier.
     *
     * @exception SearchException for errors in searching the persistent system
     * to update the list.
     */
    public void updateList(Class clazz) throws SearchException;

    // Transaction Methods

    /**
     * Wrapper to begin a transaction via OJB layer.
     *
     * @exception TransactionException for errors in starting a transaction.
     */
    public void begin() throws IntactException;

    /**
     * Wrapper to commit a transaction via OJB layer.
     *
     * @exception IntactException for errors in committing a transaction.
     */
    public void commit() throws IntactException;

    /**
     * Returns the state of the transaction via OJB layer.
     */
    public boolean isActive();

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
     * Removes the given object from cache of the underlying persistence system.
     *
     * @param object the object to clear from the cache.
     */
//    public void removeFromCache(Object object);

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
