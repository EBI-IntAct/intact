/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.persistence;

import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.odmg.OJB;
import org.apache.ojb.odmg.TransactionImpl;
import org.odmg.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class provides ODMG specific ObjectBridge Data Access Object
 *
 * @author Sugath Mudali
 * @version $Id$
 */

public class ObjectBridgeODMG extends AbstractObjectBridgeDAO {

    /**
     * ODMG factory instance used to get DB instances
     * NB this is not serializable!!
     */
    private Implementation odmg;

    /**
     * an ODMG DB instance
     * NB this is not serializable!!
     */
    private Database db;

    /**
     * ODMG transaction - not Serializable!!
     */
    private Transaction tx;

    public ObjectBridgeODMG(PersistenceBroker broker) throws ODMGException {
        super(broker);
        PBKey pbkey = broker.getPBKey();

        //now also set up some ODMG stuff for operations needing
        //transaction control and locking (eg write/update)
        odmg = OJB.getInstance();
        db = odmg.newDatabase();

        String connDetails = pbkey.getAlias() + "#" + pbkey.getUser()
                + "#" + pbkey.getPassword();
        //connect using the user details supplied to the broker
        //NB ODMG expects jcdAlias, user, password to be seperated by #
        db.open(connDetails, Database.OPEN_READ_WRITE);
        System.out.println("Using the ObjectBridge ODMG version");
    }

    /**
     * Used to begin a transaction. The transaction level (object or JDBC) should be
     * specified. Defaults to JDBC level.
     *
     * @throws TransactionException - can be thrown if a transaction is already in progress
     * @see uk.ac.ebi.intact.business.BusinessConstants
     */
    public void begin(int x) throws TransactionException {
        try {
            tx = odmg.newTransaction();
            tx.begin();
            ourLogger.debug("STARTED the transaction");
        }
        catch (TransactionInProgressException pe) {
            //transaction already open - do something
            String msg = "failed begin: cannot begin a transaction as one is already open";
            throw new TransactionException(msg, pe);
        }
    }

    /**
     * Locks the specified object to a transaction for WRITE. Note
     * that this method can ONLY be used if you are using object-level transactions
     * (it doesn't make sense for JDBC transactions)
     *
     * @param obj The object to lock for write.
     * @throws org.odmg.TransactionNotInProgressException thrown if no object TX is running
     */
    public void lock(Object obj) throws TransactionNotInProgressException {
        if ((tx == null) || (!tx.isOpen()))
            throw new TransactionNotInProgressException();
        tx.lock(obj, Transaction.WRITE);
    }

    /**
     * closes a DAO (connection).
     *
     * @throws DataSourceException - thrown if the DAO cannot be closed (details in specific errors)
     */
    public void close() throws DataSourceException {
        //this releases the broker instance - assumes doing this also
        //releases the DB connection....(may need changing for OJB 0.9.7)
        //PersistenceBrokerFactory.releaseInstance(broker);
//        checkForOpenStore();
        PersistenceBrokerFactory.releaseAllInstances();
//            if (!broker.isClosed()) {
//                broker.close();
//            }

        //clean up the ODMG stuff too
        try {
            db.close();
        }
        catch (ODMGException e) {
            throw new DataSourceException("Error in closing the database");
        }

    }

    /**
     * Commits (finishes) a transaction. Usual meaning.
     *
     * @throws TransactionException - thrown if, say, a transaction was not in progress for example
     */
    public void commit() throws TransactionException {
        tx.commit();
        // reset the TX reference to mark that no transaction in progress
        tx = null;
    }

    /**
     * creates a single object in persistent store.
     *
     * @param obj - the object to be saved
     * @throws CreateException
     */
    public void create(Object obj) throws CreateException {
        // Assume transaction has started already.
        boolean localTransaction = false;

        // Start a local transaction if none in progress
        if (!isActive()) {
            localTransaction = true;
            try {
                begin(1);
            }
            catch (TransactionException e) {
                throw new CreateException("Unable to start a local transaction");
            }
        }
        //create called as part of another TX, so lock it
        //note that the lock for write flag tells ODMG to write upon commit
        //NB ODMG associates the current thread with a TX, so we
        //should join the TX just in case the thread has changed since
        //the client begin() was called...
        ourLogger.debug("Doing a object transaction");
        tx.lock(obj, Transaction.WRITE);

        // Comit only for a local transaction.
        if (localTransaction) {
            try {
                commit();
            }
            catch (TransactionException e) {
                throw new CreateException("Unable to commit the local transaction");
            }
        }
    }

    public void forceUpdate(Object obj) throws UpdateException {
        update(obj, true);
    }

    /**
     * @see DAO#update(Object)
     */
    public void update(Object obj) throws UpdateException {
        update(obj, false);
    }

    /**
     * Does the update here.
     *
     * @param obj the object to update.
     * @param force true if update is to be forced.
     * @throws UpdateException
     */
    private void update(Object obj, boolean force) throws UpdateException {
        if (!isPersistent(obj)) {
            throw new UpdateException();
        }
        // Assume transaction has started already.
        boolean localTransaction = false;

        //do this with ODMG....
        if (!isActive()) {
            localTransaction = true;
            try {
                begin(1);
            }
            catch (TransactionException e) {
                throw new UpdateException("Unable to start a local transaction");
            }
        }
        ((TransactionImpl) odmg.currentTransaction()).markDirty(obj);

        // Comit only for a local transaction.
        if (localTransaction) {
            try {
                commit();
            }
            catch (TransactionException e) {
                throw new UpdateException("Unable to commit the local transaction");
            }
        }
    }

    /**
     * checks to see if a transaction is currently in progress. This will
     * test either an Object or JDBC level transaction, depending upon which
     * may be active (if any).
     *
     * @return boolean - true if a transaction is active, false otherwise
     */
    public boolean isActive() {
        if (tx != null) {
            return tx.isOpen();
        }
        return false;
    }

    /**
     * checks to see if a transaction is closed.
     *
     * @return boolean - true if closed (default), false otherwise
     */
    public boolean isClosed() {
        if (tx != null) {
            return !tx.isOpen();
        }
        return false;
    }

    /**
     * removes an object from persistent store.
     *
     * @param obj - the object to be removed
     * @throws TransactionException - thrown usually if the operation is called outside a transaction
     */
    public void remove(Object obj) throws TransactionException {
        // Assume transaction has started already.
        boolean localTransaction = false;

        //do this with ODMG....
        if (!isActive()) {
            localTransaction = true;
            begin(1);
        }
        db.deletePersistent(obj);

        // Comit only for a local transaction.
        if (localTransaction) {
            commit();
        }
    }

    /**
     * rollback a transaction. Usual meaning...
     *
     * @throws TransactionException - thrown if  the transaction couldn't be rolled back
     */
    public void rollback() throws TransactionException {
        tx.abort();
    }

    /**
     * This method performs a simple creation for a number of objects.
     *
     * @param objs - the collection of objects to be persisted
     * @throws CreateException - thrown if an object could not be stored
     * @throws TransactionException - thrown if errors resulted from invalid transaction operations or errors
     */
    public void makePersistent(Collection objs) throws CreateException, TransactionException {
        // Assume transaction has started already.
        boolean localTransaction = false;

        //do this with ODMG....
        if (!isActive()) {
            localTransaction = true;
            begin(1);
        }
        try {
            for (Iterator it = objs.iterator(); it.hasNext();) {
                create(it.next());
            }
        }
        catch (CreateException ce) {
            if (localTransaction) {
                rollback();
            }
            throw ce;
        }
        // Commit only for a local transaction.
        if (localTransaction) {
            commit();
        }
    }

//    public List executeOQLQuery(Class clazz, String searchParam, String searchValue)
//            throws OQLQueryException {
//        System.out.println("Doing OQL using ODMG");
//        OQLQuery query = odmg.newOQLQuery();
//        try {
//            System.out.println("I am doing the count");
//            query.create("select count(" + searchParam + ") from " + clazz.getName()
//                    + " where " + searchParam + " like $1");
//        }
//        catch (QueryInvalidException e) {
//            throw new OQLQueryException("Query invalid", e);
//        }
//        // Replace * with % for SQL
//        String sqlValue = searchValue.replaceAll("\\*", "%");
//        try {
//            query.bind(sqlValue);
//        }
//        catch (QueryParameterCountInvalidException e) {
//            throw new OQLQueryException("Query invalid parameter count", e);
//        }
//        catch (QueryParameterTypeInvalidException e) {
//            throw new OQLQueryException("Query invalid parameter type", e);
//        }
//        try {
//            return (List) query.execute();
//        }
//        catch (QueryException e) {
//            throw new OQLQueryException("Query execute error", e);
//        }
//    }
}
