/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.business;

import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.persistence.TransactionException;
import uk.ac.ebi.intact.persistence.CreateException;

import java.util.Collection;

/**
 * This interface represents an Intact user.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), modified by Chris Lewington
 * @version $Id$
 */
public interface IntactUserIF {

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
     * @exception for errors in searching; this is also thrown if the search
     * produced more than a single Institution.
     */
    public Institution getInstitution() throws IntactException;

    /**
     * Returns the reference to the DAO object. Just a wrapper for
     * <code>getDAO</code> method of <code>DAOSource</code> class.
     */
    public DAO getDAO();

    /**
     * Returns the list for given list name.
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
     * Wrapper to begin a transaction via OJB layer.
     *
     * @exception TransactionException for errors in starting a transaction.
     */
    public void begin() throws IntactException;

    /**
     * Wrapper to commit a transaction via OJB layer.
     *
     * @exception TransactionException for errors in committing a transaction.
     */
    public void commit() throws IntactException;

    /**
     * Returns the state of the transaction via OJB layer.
     */
    public boolean isActive();

    /**
     * A wrapper to roll back a transaction vi OJB layer.
     *
     * @exception TransactionException for errors in rolling back a transaction.
     */
    public void rollback() throws IntactException;

    /**
     * Wrapper to persist an object in the underlying persistence system.
     *
     * @param object the object to create.
     *
     * @exception CreateException for errors in creating an object in the
     * persistent system.
     */
    public void create(Object object) throws IntactException;

    /**
     * Wrapper to update an object in the underlying persistence system.
     *
     * @param object the object to create.
     *
     * @exception CreateException for errors in updating an object in the
     * persistent system.
     */
    public void update(Object object) throws IntactException;

    /**
     * Wrapper to delete an object from the underlying persistence system.
     *
     * @param object the object to delete.
     */
    public void delete(Object object) throws IntactException;

    /**
     * Sets the given CV Object as the current object the user is working
     * presently.
     *
     * @param cvobj the CV object to set as the current object the user is
     *  working presently.
     *
     * <pre>
     * post: return = cvobj
     * </pre>
     */
    public void setCurrentEditObject(CvObject cvobj);

    public CvObject getCurrentEditObject();

    /**
     * Return an Object by classname and shortLabel.
     *
     * @param clazz the class object to search.
     * @param label the short label to search for.
     *
     * @exception for errors in searching; this is also thrown if the search
     * produced more than a single object.
     */
    public Object getObjectByLabel(Class clazz, String label)
        throws IntactException;

    /**
     * Return an Object by classname and primary ID.
     *
     * @param clazz the class object to search.
     * @param id the primary ID (currently from GO) to base th search on.
     *
     * @exception for errors in searching; this is also thrown if the search
     * produced more than a single object.
     */
    public Object getObjectByXref(Class clazz, String id)
        throws IntactException;

    /**
     * Return an Object by classname and ac.
     *
     * @param clazz the class object to search.
     * @param ac the AC to search for.
     *
     * @exception for errors in searching; this is also thrown if the search
     * produced more than a single object.
     */
    //public Object getObjectByAc(Class clazz, String ac) throws SearchException;

    /**
     * Removes the given object from cache of the underlying persistence system.
     *
     * @param object the object to clear from the cache.
     */
    public void removeFromCache(Object object);

    /**
     * Return an Object by classname and AC.
     *
     * @param clazz the class object to search.
     * @param ac the AC to search for.
     *
     * @return true if object of <code>clazz</code> with <code>ac</code>
     * exists of the persistence system.
     */
    //public boolean objectExistsByAc(Class clazz, String ac);

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
                              String searchValue) throws IntactException;

}
