/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.persistence;

import org.exolab.castor.jdo.*;

import java.util.*;
import org.apache.ojb.broker.util.logging.*;

/**
 *  <p> This interface defines the methods available from a Data
 * Access Object (DAO) implementation. It therefore allows clients
 * of this interface to use typical operations usually provided by
 * a connection class, such as submitting queries etc without
 * knowing the details of the specific DAO implementing this interface.
 * Each implementation of the interface will therefore be a wrapper
 * object around a particular DAO type (eg Castor, XML etc). </p>
 *
 * <p>Note that methods are included to either perform "encapsulated transactions"
 * (for example bulk object creation or simple searching), or to manage
 * transactions at the client level as one might usually do with, say, JDBC. </p>
 *
 * @author Chris Lewington
 *
 */
public interface DAO {


    /**
     *   Used to begin a transaction.
     *
     * @exception TransactionException - can be thrown if a transaction is already in progress
     *
     */
    public void begin() throws TransactionException;

    /**
     *   closes a DAO (connection).
     *
     * @exception DataSourceException - thrown if the DAO cannot be closed (details in specific errors)
     *
     */
    public void close() throws DataSourceException;

    /**
     *   Commits (finishes) a transaction. Usual meaning.
     *
     * @exception TransactionException - thrown if, say, a transaction was not in progress for example
     *
     */
    public void commit()throws TransactionException;

    /**
     * creates a single object in persistent store.
     *
     * @param obj - the object to be saved
     *
     * @exception CreateException
     *
     */
    public void create(Object obj) throws CreateException;

    /**
     *   checks to see if a transaction is currently in progress
     *
     * @return boolean - true if a transaction isa ctive, false otherwise
     */
    public boolean isActive();

    /**
     *   checks to see if object saving automatically is turned on
     *
     * @return boolean - true if auto saving is on, false otherwise
     */
    public boolean isAutoSave();

    /**
     *   sets whether or not auto saving is turned on
     *
     * @param val - true to turn on, false for off
     */
    public void setAutoSave(boolean val);

    /**
     *   checks to see if a transaction is closed
     *
     * @return boolean - true if closed, false otherwise
     */
    public boolean isClosed();

    /**
     *   checks to determine if a given object is persistent or not
     *
     * @param obj - the object to be checked
     *
     * @return boolean - true if the object is persistent, false otherwise
     */
    public boolean isPersistent(Object obj);

    /**
     *   removes an object from persistent store.
     *
     * @param obj - the object to be removed
     *
     * @exception TransactionException - thrown usually if the operation is called outside a transaction
     * @exception DataSourceException - used to flag other errors in the persistence layer
     *
     */
    public void remove(Object obj) throws TransactionException, DataSourceException;

    /**
     *  rollback a transaction. Usual meaning...
     *
     * @exception TransactionException - thrown if  the transaction couldn't be rolled back
     */
    public void rollback() throws TransactionException;

    /**
     * This method performs an object creation operation for a collection of
     * objects provided as parameters.
     *
     * @param objs - the collection of objects to be persisted
     *
     * @exception CreateException - thrown if an object could not be stored
     * @exception TransactionException - thrown if errors resulted from invalid transaction operations or errors
     *
     */
    public void makePersistent(Collection objs) throws CreateException, TransactionException;

    /**
     * <p>This method performs a simple search based on the object type to search over,
     * the parameter name and the search value. This method will begin a new transaction
     * and commit it before returning if it is not called from within the caller's
     * own transaction
     * </p>
     *
     * Note that at present this only performs very simple "single" text queries - and
     * may be updated later..
     *
     * @param type - the persistent object type to be searched
     * @param col - the parameter to be searched through - NB assumed to be a DB column name
     * @param val - the value to be used for the search
     *
     * @return a collection containing the search results
     *
     * @exception SearchException - thrown if there were problems during the search process itself
     * @exception TransactionException - thrown if errors resulted from invalid transaction operations or errors
     * @exception DataSourceException - thron if there were other problems with the persistence engine used
     *
     */
     public Collection find(String type, String col, String val) throws SearchException, TransactionException, DataSourceException;

    /**
     * <p>This method performs an object-based search. Specifically it provides a way to
     * search for a "complete" object given a partially defined object, ie an object which does
     * not have all fields filled. It provides equivalent functionality to the simple text-based
     * search when only a single (String) field is filled in. Searches may be performed using an example
     * object which only has a single object reference set, or even an example object containing a Collection
     * that has only a single element set.
     * </p>
     * <p>
     * NOTE - RESTRICTIONS ON CURRENT USE
     * </p>
     * <ul>
     * <li>Any object references contained in the obj parameter must have, at least, their Primary Key defined</li>
     * <li>if the obj parameter contains a non-empty Collection, the Collection must have at least one element with its Primary Key set
     * </ul>
     *
     * @param obj - the object search "value"
     *
     * @return a collection containing the search results
     *
     * @exception SearchException - thrown if there were problems during the search process itself
     * @exception TransactionException - thrown if errors resulted from invalid transaction operations or errors
     * @exception DataSourceException - thron if there were other problems with the persistence engine used
     *
     */
     public Collection find(Object obj) throws SearchException, TransactionException, DataSourceException;



    /**
     * <p>updates a given object which was oroginally obtained in
     * another transaction. This allows, for example, objects to be read
     * from persistent store in one transaction, modified elsewhere (eg via
     * a user interface object) and then have the changes reflected in persistent
     * store within the context of a <bold>different</bold> transaction. This therefore
     * removes the need to retrieve the object again in order to perform an update.
     * </p>
     *
     * @param obj - the object to be updated
     *
     * @exception CreateException  - thrown if the object could not me modified,
     * eg called outside a transaction scope
     *
     */
     public void update(Object obj) throws CreateException;

    /**
     *  allows a logging destination to be specified
     *
     * @param p A <code>PrintWriter</code> for logging output
     */
    public void setLogger(org.apache.ojb.broker.util.logging.Logger p);

    /**
     *  adds a class to a set of classes that should be cached. Note that this is
     * in addition to any standard "cache by primary key" facility provided by an implementation.
     *
     * @param clazz  class to be cached
     */
    public void addCachedClass(Class clazz);


}
