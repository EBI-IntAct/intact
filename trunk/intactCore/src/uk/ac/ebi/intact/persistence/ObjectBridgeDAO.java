/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.persistence;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

import org.apache.ojb.broker.*;
import org.apache.ojb.broker.util.logging.*;
import org.apache.ojb.broker.query.*;
import org.apache.ojb.broker.util.configuration.impl.*;
import org.apache.ojb.broker.util.configuration.*;
import org.apache.ojb.broker.metadata.*;

//ODMG
import org.odmg.*;
import org.apache.ojb.odmg.*;

/**
 *  <p>This class provides an ObjectBridge-specific Data Access Object, which
 * effectively wraps up an ObjectBridge broker object. Note that this DAO only
 * provides a wrapped object from the ObjectBridge kernel, NOT the ODMG interface.</p>
 *
 * @author Chris Lewington, Sugath Mudali
 * @version $Id$
 */

public class ObjectBridgeDAO implements DAO, Serializable {


    /**
     *  holds the instance of the Database (ie a connection)
     * NB this is not serializable!!
     */
    private transient PersistenceBroker broker;

    /**
     * A log utility, used if required
     */
    private Logger logger;

    /** cache to be used for caching by unique search parameters. The parameter
     * is the key, and the value is the key used by the OJB cache.
     *
     */
    private HashMap cache = new HashMap();

    /** determines which classes are to be cached.
     *
     */
    private HashSet cachedClasses = new HashSet();

    /**
     * ODMG factory instance used to get DB instances
     * NB this is not serializable!!
     */
    private transient Implementation odmg;

    /**
     * an ODMG DB instance
     * NB this is not serializable!!
     */
    private transient Database db;

    /**
     * ODMG transaction - not Serializable!!
     */
    private transient Transaction tx;

    /**
     * OJB repository file (contains mappings).
     */
    private String repositoryFile;

    /**
     * The user name to access the database.
     */
    private String user;

    /**
     * The password to access the database.
     */
    private String password;

    //used to save connection info in ODMG format
    private String connectionDetails;


    public ObjectBridgeDAO(PersistenceBroker broker) {

        this.broker = broker;
        repositoryFile = broker.getDescriptorRepository().getPBkey().getRepositoryFile();

        //get user details and cache for later use
        user = broker.getPBKey().getUser();
        password = broker.getPBKey().getPassword();

        try {

            logger = LoggerFactory.getLogger(Class.forName("org.apache.ojb.broker.util.logging.Log4jLoggerImpl"));
        }
        catch(ClassNotFoundException c) {

            //try and set up a simple logger instead here....
        }

        //now also set up some ODMG stuff for operations needing
        //transaction control and locking (eg write/update)
        odmg = OJB.getInstance();
        db = odmg.newDatabase();

        try {

            //connect using the user details supplied to the broker
            //NB ODMG expects reopsitory location, user, password to be seperated by #
            connectionDetails = "config/repository.xml#" + user +"#" + password;
            db.open(connectionDetails, Database.OPEN_READ_WRITE);

        }
        catch(ODMGException e) {

            logger.error("failed to open database!!", e);
        }

    }

    /**
     * changes the username and password details used for connection.
     * Note that this is NOT currently a runtime change, but will change
     * to a different user if eg the DAO is serialized. This may be modified
     * later to (if possible) provide runtime user switching.
     *
     * @param user the new username
     * @param password the new user password
     */
    public void resetUserInfo(String user, String password) {

        this.user = user;
        this.password = password;
        connectionDetails = "config/repository.xml#" + user +"#" + password;
    }

    public void addCachedClass(Class clazz) {

        cachedClasses.add(clazz);
    }

    public boolean isCachedClass(Class clazz) {
        return cachedClasses.contains(clazz);
    }

    public void clearCache() throws PersistenceBrokerException {
        broker.clearCache();
    }

    public void setLogger(org.apache.ojb.broker.util.logging.Logger p) {

        logger = p;
    }


    /**
     * tries to restore a connection if it has been lost due to
     * serialization (PBs and ODMG DB/TX objects are not serializable)
     *
     */
    private void checkForOpenStore() {

        if((broker == null) & (db == null) & (odmg == null)) {

            //the DAO has been serialized and the connections need to be
            //restored.
            //NB does this mean we lose transactions?....
            try {
                logger.debug("db class is null, reopening.....before open()");
                this.open();

                //get the current transaction back as it couldn't be serialised..
                tx = odmg.currentTransaction();

            }
            catch(Exception e) {
                logger.error("unable to reconnect to store after serialization", e);
            }
            //return true;
        }
        //return false;
    }

    /**
     * validates a username and password against the datastore.
     *
     * @return boolean true if user credentials are valid, false if not or if a null username is supplied.
     */
    public boolean isUserValid(String userName, String password) {

        //NB simple validation for now, probably to be enhanced later -
        //just validate against user details in OJB repository.xml..
        if (userName == null) return false;

        String knownUser = DescriptorRepository.getDefaultInstance().getDefaultJdbcConnection().getUserName();
        String knownPassword = DescriptorRepository.getDefaultInstance().getDefaultJdbcConnection().getPassWord();
        if(userName.equals(knownUser)) {
            //check password - could be null..
            if(((password == null) & (knownPassword == null)) ||
               (password.equals(knownPassword))) return true;
        }
        return false;
    }

    /**
     *   Used to begin a transaction.
     *
     * @exception TransactionException - can be thrown if a transaction is already in progress
     *
     */
    public void begin() throws TransactionException {

        try {
            //ODMG transaction...
            tx = odmg.newTransaction();
            tx.begin();

            //broker.beginTransaction();
        }
        catch(org.odmg.TransactionInProgressException pe) {

            //transaction already open - do something
            String msg = "failed begin: cannot begin a transaction as one is already open";
            throw new TransactionException(msg, pe);
        }

    }

    public void lock(Object obj) {
        tx.lock(obj, Transaction.WRITE);
    }

    /**
     *   closes a DAO (connection).
     *
     * @exception DataSourceException - thrown if the DAO cannot be closed (details in specific errors)
     *
     */
    public void close() throws DataSourceException {

        //this releases the broker instance - assumes doing this also
        //releases the DB connection....(may need changing for OJB 0.9.7)
        //PersistenceBrokerFactory.releaseInstance(broker);
        checkForOpenStore();
        try {
            broker.close();

            //clean up the ODMG stuff too
            db.close();
        }
        catch(Exception e) {
            throw new DataSourceException("failed to close datasource!", e);
        }

    }

    /**
     *   opens a DAO (connection). Note that connections are opened also via
     * the DAO constructor method, so this method should not be called unless
     * a previous call to close() has been made.
     *
     * @exception DataSourceException - thrown if the DAO cannot be closed (details in specific errors)
     *
     */
    public void open() throws DataSourceException {

        try {

            odmg = OJB.getInstance();
            db = odmg.newDatabase();

            //check for a user/password override of default
            if(user != null) {
                db.open(connectionDetails, Database.OPEN_READ_WRITE);
                broker = PersistenceBrokerFactory.createPersistenceBroker(new PBKey(repositoryFile, user, password));
            }
            else {
                //should creeate based on default user details
                db.open("config/repository.xml", Database.OPEN_READ_WRITE);
                broker = PersistenceBrokerFactory.createPersistenceBroker(new PBKey(repositoryFile));
            }

        }
        catch(ODMGException e) {

            throw new DataSourceException("failed to open database!!", e);
        }


    }

    /**
     *   Commits (finishes) a transaction. Usual meaning.
     *
     * @exception TransactionException - thrown if, say, a transaction was not in progress for example
     *
     */
    public void commit() throws TransactionException {

        try {

            //ODMG transaction...
            if(tx != null) {
                tx.commit();

                //reset the TX reference to avoid confusion
                tx = null;
            }
            else {

                //error - trying to close a non-existent TX
                throw new TransactionException("error during commit - no transaction exists!");
            }

            //broker.commitTransaction();
        }
        catch(org.odmg.TransactionAbortedException tae) {

            //couldn't commit for some reason - do something
            String msg = "transaction commit failed";
            throw new TransactionException(msg, tae);
        }
        catch(org.odmg.TransactionNotInProgressException tne) {

            //method called outside a transaction - do something
            String msg = "transaction commit failed";
            throw new TransactionException(msg, tne);
        }


    }

    /**
     * creates a single object in persistent store.
     *
     * @param obj - the object to be saved
     *
     * @exception CreateException
     *
     */
    public void create(Object obj) throws CreateException {

        //do this with ODMG....
        checkForOpenStore();
        Transaction tx1 = null;
        boolean localTx = false;
        try {

            if(isActive()) {
                logger.debug("Transaction already started");

                //create called as part of another TX, so lock it
                //note that the lock for write flag tells ODMG to write upon commit
                //NB ODMG associates the current thread with a TX, so we
                //should join the TX just in case the thread has changed since
                //the client begin() was called...
                tx.join();
                tx.lock(obj, tx.WRITE);
            }
            else {
                logger.debug("Starting Local Transaction");

                //run a local transaction
                tx1 = odmg.newTransaction();
                localTx = true;
                tx1.begin();
                tx1.lock(obj, tx1.WRITE);
            }
            if(localTx) {

                //local transaction, so commit here instead...
                logger.debug("committing local TX");
                tx1.commit();
            }
        }
        catch(Exception e) {

            if (localTx) {
                //local transaction failed - should rollback here..
                logger.error("aborting local TX");
                logger.error(e);
                tx1.abort();
            }
            logger.debug("Exception from create", e);
            //Question: if the op fails, should the client TX abort too?...
            String msg = "create failed for object of type " + obj.getClass().getName();
            throw new CreateException(msg, e);
        }

    }

    public void removeFromCache(Object obj) {
        broker.removeFromCache(obj);
    }

    /**
     * <p>updates a given object which was originally obtained in
     * another transaction. This allows, for example, objects to be read
     * from persistent store (or created) in one transaction, modified elsewhere (eg via
     * a user interface object) and then have the changes reflected in persistent
     * store within the context of a <bold>different</bold> transaction. This therefore
     * obviates the need to retrieve the object again in order to perform an update.
     * Note: if the object parameter cannot be found in the persistent store, it will
     * be created as in such cases an update is not possible.
     * </p>
     *
     * @param obj - the object to be updated (or created if it does not exist in store)
     *
     * @exception CreateException  - thrown if the object could not me modified,
     * eg called outside a transaction scope
     *
     */
    public void update(Object obj) throws CreateException {

        //use ODMG...

        //old PB code
        //ObjectModificationDefaultImpl mod = new ObjectModificationDefaultImpl();
        //mod.setNeedsUpdate(true);
        checkForOpenStore();
        Transaction tx1 = null;
        boolean localTx = false;
        Object dummy = null;

        logger.debug("doing update - searching for old data...");
        // Removed the object from cache for a database load to get the
        // true old data.
        broker.removeFromCache(obj);
         try {

             //NB To do a proper (intelligent) update:
             //1) get the "old" obj data from the DB
             //2) lock it for write in an ODMG transaction
             //3) copy the new data into it (harder than it sounds!!)
             //4) commit
             //
             dummy = broker.getObjectByIdentity(new Identity(obj));
             if(dummy == null) {
                 logger.debug("unable to update " + obj.getClass().getName() + " : no object exists in store; creating it...");
                 create(obj);
             }
             else {
                 logger.debug("object retrieved : " + dummy.toString());

                 //check TX details and lock object into appropriate TX
                 if(isActive()) {

                     //NB ODMG associates the current thread with a TX, so we
                    //should join the TX just in case the thread has changed since
                    //the client begin() was called...
                    logger.debug("client transaction detected - locking retrieved object for write..");
                    tx.join();
                    tx.lock(dummy, tx.WRITE);
                 }
                 else {
                     //start a local TX
                     logger.debug("beginning local transaction - locking object for write...");
                     tx1 = odmg.newTransaction();
                     tx1.begin();
                     tx1.lock(dummy, tx1.WRITE);
                     localTx = true;
                 }

                 //do some reflection/security stuff so we can set the fields to new values..

                 //first though, need to get all the superclasses as we don't get them directly from a Class object...

                 Collection superClasses = new ArrayList();
                 Class tester = dummy.getClass();
                 logger.debug("getting classes - first class is " + tester.getName());
                 while(tester != Object.class) {

                    logger.debug("superclass added to collection: " + tester.getName());
                    superClasses.add(tester);

                     //now get the next parent up
                     tester = tester.getSuperclass();
                 }

                 logger.debug("parent classes obtained - getting all fields in hierarchy...");
                 Collection fieldList = new ArrayList();
                 Iterator it = superClasses.iterator();
                 while(it.hasNext()) {
                     Field[] superclassFields = ((Class)it.next()).getDeclaredFields();
                     for(int i=0; i < superclassFields.length; i++) {
                         logger.debug("field name: " + superclassFields[i].getName());
                         fieldList.add(superclassFields[i]);
                     }
                 }

                 //convert list of fields to an array, as EccessibleObject needs
                 //an array of AccessibleObjects as a param
                 logger.debug("converting field list to array for security processing...");
                 Field[] fields = (Field[]) fieldList.toArray(new Field[0]);

                 try {
                     //set the permissions on the fields - if this ever fails (eg if a
                     //SecurityManager is set), need to
                     //go via the AccessController...
                     logger.debug("setting fields to be accessible for write...");
                     AccessibleObject.setAccessible(fields, true);
                 }
                 catch(SecurityException se) {
                     logger.error("failure during update - field access denied!!", se);
                 }

                 //now update them..
                 Object value = null;
                 logger.debug("The number of fields: " + fields.length);
                 for(int i=0; i < fields.length; i++) {

                     try {
                         value = fields[i].get(obj);
                         logger.debug("field: " + fields[i].getName());
                         logger.debug("old value: " + fields[i].get(dummy));
                         logger.debug("new value: " + value);
                         fields[i].set(dummy, value);
                         logger.debug("field updated OK...");
                     }
                     catch(Exception e) {
                         logger.error("failed to update field " + fields[i].getName(), e);
                     }
                 }

                 if(localTx) {

                    //local transaction, so commit here instead...
                    logger.debug("committing local TX");
                    tx1.commit();
                 }
             }

        }
        catch(Exception e) {

             if (localTx) {

                //local transaction failed - should rollback here..
                logger.error("aborting local TX");
                logger.error(e);
                tx1.abort();
             }
            //client should be responsible for aborting their own TX...
            //problem doing DB begin/commit, or updating object - do something
            String msg = "object update failed: problem saving object of type " + obj.getClass().getName();
            logger.debug("exception details: " + e.getMessage());
            logger.debug(e.toString());
             throw new CreateException(msg, e);
        }
    }



    /**
     *   checks to see if a transaction is currently in progress
     *
     * @return boolean - true if a transaction isa ctive, false otherwise
     */
    public boolean isActive() {

        checkForOpenStore();
        if(tx != null) {
            return tx.isOpen();
        }
        else{
            return false;
        }
    }

    /**
     *   checks to see if object saving automatically is turned on
     *
     * @return boolean - true if auto saving is on, false otherwise
     */
    public boolean isAutoSave() {

        checkForOpenStore();
        Configuration con = OjbConfigurator.getInstance().getConfigurationFor(null);
        OjbConfiguration ojbCon = (OjbConfiguration)con;
        if(ojbCon.useAutoCommit()==1) return true;
        return false;
    }

    /**
     *   sets whether or not auto saving is turned on
     *
     * @param val - true to turn on, false for off
     */
    public void setAutoSave(boolean val) {

        checkForOpenStore();
            try {
                broker.getConnectionManager().getConnection().setAutoCommit(val);
            }
            catch(Exception se) {

                logger.error("unable to reset autocommit value - reset failed", se);
            }
    }


    /**
     *   checks to see if a transaction is closed.
     *
     * @return boolean - true if closed (default), false otherwise
     */
    public boolean isClosed() {

        checkForOpenStore();
        if(tx != null) {
            return tx.isOpen();
        }
        else {
            return false;
        }
    }

    /**
     *   checks to determine if a given object is persistent or not
     *
     * @param obj - the object to be checked
     *
     * @return boolean - true if the object is persistent (default), false otherwise
     */
    public boolean isPersistent(Object obj) {

        checkForOpenStore();
        Object stored = null;
        stored = broker.getObjectByIdentity(new Identity(obj));
        if(stored == null) return false;
        return true;

    }

    /**
     *   removes an object from persistent store.
     *
     * @param obj - the object to be removed
     *
     * @exception TransactionException - thrown usually if the operation is called outside a transaction
     *
     */
    public void remove(Object obj) throws TransactionException {

        //do this through ODMG - removes from the cache too...
        checkForOpenStore();
        Transaction tx1 = null;
        boolean localTx = false;
        try {

            //broker.delete(obj);
            if(!isActive()) {

                //start local transaction - don't need to lock for write as db.deletePersistent
                //does that for us
                tx1 = odmg.newTransaction();
                tx1.begin();
                logger.debug("local TX started to delete object " + obj.getClass().getName());
                localTx = true;
            }

            //NB for multi-threaded calls, need to make sure that the current thread
            //is always connected to the client transaction as this is how ODMG works...
            if(!localTx) tx.join();
            db.deletePersistent(obj);
            logger.debug("object " + obj.getClass().getName() + " successfully locked for delete...");

            if(localTx) {

                //commit the local transaction
                logger.debug("commiting delete on local transaction for object " + obj.getClass().getName());
                tx1.commit();
                logger.debug("local TX committed successfully");
            }
        }
        catch(Exception pbe) {

            if(localTx) {

                //should rollback the local TX
                logger.debug("error - local TX aborting...");
                logger.debug("failed due to " + pbe);
                tx1.abort();
            }

            //should we rollback the client TX also??
            String msg = "remove for object type " + obj.getClass().getName() + " failed: see OBJ exception details";
            throw new TransactionException(msg, pbe);
        }
    }

    /**
     *  rollback a transaction. Usual meaning...
     *
     * @exception TransactionException - thrown if  the transaction couldn't be rolled back
     */
    public void rollback() throws TransactionException {

        checkForOpenStore();
        try {

            //broker.abortTransaction();
            if(tx != null) {

                //make sure the current thread is associated with the TX first, to be safe
                tx.join();
                tx.abort();
            }
            else {
                //error - no TX exists...
                throw new TransactionException("error - cannto perform rollback: no transaction exists!");
            }
        }
        catch(org.odmg.TransactionNotInProgressException e) {


            String msg = "rollback failed: see OJB exception details";
            throw new TransactionException(msg, e);
        }

    }

    /**
     * This method performs a simple creation for a number of objects.
     *
     * @param objs - the collection of objects to be persisted
     *
     * @exception CreateException - thrown if an object could not be stored
     * @exception TransactionException - thrown if errors resulted from invalid transaction operations or errors
     *
     */
    public void makePersistent(Collection objs) throws CreateException, TransactionException {

        //local check flag for a localised transaction
        //do this in ODMG....
        checkForOpenStore();
        boolean startedHere = false;

        if(objs != null) {

            Iterator it = objs.iterator();

            try {
                //check for a current transaction - if not, start one
                if (!isActive()) {

                    begin();
                    startedHere = true;
                }
                while(it.hasNext()) {

                    create(it.next());
                }
                if(startedHere) {

                   commit();
                }
            }
            catch(TransactionException te) {

                rollback();
                throw te;

            }
            catch(CreateException ce) {

                 rollback();
                throw ce;
            }
        }
        else {

            String msg = "creation error - no objects specified to be created!";
            throw new CreateException(msg);

        }

    }


    /**
     *   <p>This method searches the DB based on the information in the
     *   parameters. Note that this only supports a single parameter
     *   (simple) search at present - bulk searches are TBD </p>
     *
     * @param type - the class name (ie table) to be searched
     * @param col - the parameter (column name) to search on (usually ac number or name)
     * @param val - the value of the search parameter
     *
     * @return a collection containing the results of the searches
     *
     * @exception SearchException -  thrown for errors during the search process, eg query problems
     *
     */
    public Collection find(String type, String col, String val) throws SearchException {

        checkForOpenStore();
        Query query = null;
        Collection results = new ArrayList();
        Class searchClass = null;

        //debug info..
        long timer = 0;

        //first check the params
        if(type == null) {

            //no table specified
            String msg = "search failed: no object type specified in request to search on";
            logger.info(msg);
            throw new SearchException(msg);
        }
        if(col == null) {

            //no table column
            String msg = "search failed: no search parameter specified in request";
            logger.info(msg);
            throw new SearchException(msg);
        }
        if(val==null) {

            //no search value
            String msg = "search failed: no search value specified in request";
            logger.info(msg);
            throw new SearchException(msg);
        }

        try {

            searchClass = Class.forName(type);
            Criteria crit = null;

            //check local cache first for the object, in case the col is not the PK
            if(isCachedClass(searchClass)) {

                logger.info("search class may be unique by criteria other than PK - trying local cache...");

                Object obj = cache.get(searchClass + "-" + col + "-" + val);

                if(obj != null) {

                    logger.debug("class " + obj.getClass().getName()+ " found in local cache");
                    logger.debug("returning locally cached object as find result - class: " + obj.getClass().getName());
                    //as obj is unique, we are done
                    results.add(obj);
                    return results;

                }
            }
            //build a normal Criteria query, if class is cached locally or not (
            //if found locally, would have returned by here)
            /*if(val.indexOf('*') != -1) {

                //search value contains a wildcard - check to see if 'like' or
                //a full wildacrd search is needed
                if(val.equals("*")) {

                //wildcard search - setting a null criteria should,
                    //according to OJB, result in all records retrieved (!)
                    logger.info("wildcard search requested - using null criteria to get all matches..");
                }
                else {

                    //must be a 'like' query...
                    logger.info("search criteria: looking for " + col + " 'like' " + val);
                    crit = new Criteria();
                    crit.addLike(col, val);
                }
            }
            else {*/

                //fully specified search value
                crit = new Criteria();
                crit.addEqualTo(col, val);
            //}
            logger.info("criteria built OK");
            query = new QueryByCriteria(searchClass, crit);
            logger.info("query by criteria built OK: " + type + " " + col + " " + val);

            //simple timing
            //System.gc();
            long tmp = System.currentTimeMillis();
            //System.gc();
            timer = System.currentTimeMillis();

            results = broker.getCollectionByQuery(query);

            tmp = System.currentTimeMillis();
            timer = tmp - timer;
            logger.info("******************************************");
            logger.info("time in OJB query execute (ms): " + timer);
            logger.info("******************************************");

        }
        catch(PersistenceBrokerException pbe) {

            //problem doing the query....
            String msg = "search failed: problem executing query - Query details:\n";
            String details = "Criteria: " + query.getCriteria().toString() + "\n";
            String details2 = "class: " + query.getSearchClass().getName() + "\n";
            String details3 = "attribute requested: " + query.getRequestedAttribute() + "\n";
            logger.info(msg);
            throw new SearchException(msg, pbe);

        }
        catch(ClassNotFoundException c) {

            //problem finding the class to search on....
            String msg = "search failed: problem executing query - class to search not found! ";
            logger.info(msg);
            throw new SearchException(msg, c);


        }

        if((isCachedClass(searchClass)) & (!results.isEmpty())) {

            //must have a unique result that could be queried other than by PK, so cache it locally
            Iterator it = results.iterator();
            Object obj = it.next();
            if(it.hasNext()) {

                //some problem - expecting a unique result...
                throw new SearchException("search error - expecting a unique result but got more than one");
            }
            cache.put(searchClass + "-" + col + "-" + val, obj);
        }

        return results;
    }

    /**
     *   @see uk.ac.ebi.intact.persistence.DAO
     * Note: the local cache of non-PK fields is not currently used in this method (to do)
     */
    public Collection find(Object obj) throws SearchException {

        //IMPORTANT!! OJB queryByExample only works on IDs again, ie it is not a "real"
        //search by object. If the example has no ID set it returns all for that class.
        //see private method buildQueryByExample for an attempt to do it properly.....

        checkForOpenStore();
        Query query = null;
            Collection results = new ArrayList();

            //debug info..
            long timer = 0;

            //first check the param
            if(obj==null) {

                //no search value
                String msg = "search failed: no search value specified in request";
                logger.info(msg);
                throw new SearchException(msg);
            }

            try {

                query = buildQueryByExample(obj);
                logger.info("query by example built OK");

                timer = System.currentTimeMillis();

                //get a Collection - may be more than one object matching the example
                results = broker.getCollectionByQuery(query);

                long tmp = System.currentTimeMillis();
                timer = tmp - timer;
                logger.info("******************************************");
                logger.info("search by object: time in OJB query execute (ms): " + timer);
                logger.info("******************************************");

            }
            catch(PersistenceBrokerException pbe) {

                //problem doing the query....
                String msg = "search failed: problem executing query " + query.toString();
                logger.info(msg);
                throw new SearchException(msg, pbe);

            }

            return results;
        }


//-------------------------- private helper methods ----------------------------------------

    /**
     *  This method builds a query string based on the table information
     * provided. Currently only a simple SELECT...WHERE statement is built;
     * if more complex ones are needed in future then this method is the place
     * to modify the logic - note however that this is not a trivial exercise..
     */
//    private String buildQuery(String className, String col) {
//
//        StringBuffer buf = new StringBuffer("SELECT p FROM ");
//        buf.append(className);
//        buf.append(" p WHERE ");
//        buf.append(col);
//        buf.append("=$1");
//
//        return buf.toString();
//    }

    /**
     * <p>
     *  This method builds an OJB query based on the contents of the object parameter.
     * Specifically a query is built from any non-null field, object reference or
     * collection of the object parameter.</p>
     * <p>
     * NOTE: A query will be built from an example object as follows:
     * - only ONE example item from any of the groups fields, references or collections, is
     * needed to build a query. In some cases (eg where an m:n relation is supplied in the
     * example object) then q special query will be built, overriding any previous search criteria.
     * This is because a different query is needed in those cases, and the search result will be the same.
     * </p>
     * <p>
     * LIMITATIONS:
     *
     * a) reference objects set within the example must have their primary key
     *    attribute set
     * b) for example objects with Collections, a collection must have at least one element
     *    and the element must have at least its Primary Key set
     * </p>
     *
     * @param obj The example object to search on
     *
     * @return Query A suitable search query object
     */
    private Query buildQueryByExample(Object obj) {

        Query query = null;
        Criteria criteria = new Criteria();

        //get some info on the parameter object's OJB details....
		ClassDescriptor cld = DescriptorRepository.getDefaultInstance().getDescriptorFor(obj.getClass());
		FieldDescriptor[] fds = cld.getFieldDescriptions();
        String objClassName = obj.getClass().getName();

        logger.debug("DAO: field descriptions for object " + objClassName + " :");

		//worker variables to hold persistent fields and their values set in the example obj
        PersistentField field;
		Object value;

		//handle "simple" fields eg String, Date etc
        logger.debug("field info for object " + objClassName + ":");
        for (int i = 0; i < fds.length; i++)
		{
			try {

				field = fds[i].getPersistentField();
                value = field.get(obj);

                logger.debug("name: " + field.getName());
                logger.debug("type: " + field.getType());

				if (value != null)
				{
					//OK to use the field name - its mapping to a column name is handled by OJB...
                    criteria.addEqualTo(field.getName(), value);
                    logger.debug("value: " + value.toString());
				}
			}
			catch (Throwable ex) {

				logger.error(ex);
			}
		}

        //try object refs..
        Vector objRefs = cld.getObjectReferenceDescriptors();
        Iterator it1 = objRefs.iterator();
        logger.debug("reference object details for " + objClassName + ":");

        while(it1.hasNext()) {
            ObjectReferenceDescriptor refDesc = (ObjectReferenceDescriptor)it1.next();

            logger.debug("name: " + refDesc.getAttributeName());
            logger.debug("type: " + refDesc.getClass().getName());

            buildObjectRefCriteria(refDesc, criteria, obj);

        }

        //try Collection fields...
        Vector collectionDescs = cld.getCollectionDescriptors();
        Iterator it = collectionDescs.iterator();
        logger.debug("collection descriptor details for " + objClassName + ":");
        while(it.hasNext()) {

            //go through the collections until we find a single one
            //containing data - only one is needed to obtain the target PK we want
            CollectionDescriptor collectionDesc = (CollectionDescriptor)it.next();

            logger.debug("name: " + collectionDesc.getAttributeName());
            logger.debug("item type:" + collectionDesc.getItemClass().getName());
            logger.debug("persistent field: " + collectionDesc.getPersistentField().getName());

            try {
                field = collectionDesc.getPersistentField();
                Collection col = (Collection)field.get(obj);

                //got a Collection - now  need to add its details to the search info..
                if((col != null) & (!col.isEmpty())) {

                    if (collectionDesc.isMtoNRelation()) {

                        //need to get a query as there is a specific one for m:n mappings#
                        //NB can ignore any criteria supplied so far
                        query = buildMtoNQuery(cld, collectionDesc, obj);

                        //only need ONE collection example....
                        break;
                    }
                    else {

                        buildCollectionCriteria(cld, collectionDesc, criteria, obj);

                        //only need to find one entry as we can get the target PK from it
                        query = new QueryByCriteria(obj.getClass(), criteria);
                        break;
                    }
                }
            }
            catch(Throwable ex) {

                logger.error(ex);
            }
       }

        if (query == null) {

            //didn't get any info from collection - so buld a query on what we have
            query = new QueryByCriteria(obj.getClass(), criteria);

        }
        return query;

    }

    /**
     * private method to handle query building based on an example object that contains
     * a non-null collection. Note that for this to be possible the collection must contain
     * at least one object which has (at least) its primary key set.
     *
     * This method handles collections which model 1:n relations only - for m:n relations
     * the method buildMtoNQuery should be used.
     *
     * @param targetDesc The Class Descriptor of the target object
     * @param collectionDesc The CollectionDescriptor for the example object
     * @param obj The example search object
     *
     * @exception PersistenceBrokerException Thrown if there were OJB problems
     *
     */
    private void buildCollectionCriteria(ClassDescriptor targetDesc, CollectionDescriptor collectionDesc, Criteria criteria, Object obj) throws PersistenceBrokerException {

        //get some basic info first
        PersistentField field = collectionDesc.getPersistentField();
        Collection collection = (Collection)field.get(obj);
        Vector inverseFkIds = collectionDesc.getForeignKeyFields();

        logger.debug("search by object (collection): field in example object " + obj.getClass().getName() + " with collection is " + field.getName());


        if(inverseFkIds != null) {

            //must be: a) elements with inverse FKs (ie 1:n mappings)....
            /*
            * sketch:
            *
            * - get the FK pointing to the target obj from any non-null Collection element
            *   (if it is not set via the example obj, go to the DB for it via the element PK)
            * - build in an addEqualTo criteria for the target obj's PK
            */

            //must only be one, as we know by now the relation to the Collection is 1:n
            int inverseFkId = ((Integer)inverseFkIds.firstElement()).intValue();
            logger.debug("ID of inverse foreign key is " + inverseFkId);

            //get the PK value from the FK in the example if set, or from the DB if not...
            if(collection != null) {

                Iterator it = collection.iterator();

                //only need one example element...
                if(it.hasNext()) {

                    Object element = it.next();
                    logger.debug("class of collection elements is " + element.getClass().getName());

                    //get basic info on the element and its relevant FK
                    ClassDescriptor elemDesc = DescriptorRepository.getDefaultInstance().getDescriptorFor(element.getClass());
                    FieldDescriptor elemFieldDesc = elemDesc.getFieldDescriptorByIndex(inverseFkId);
                    PersistentField persistentField = elemFieldDesc.getPersistentField();

                    Object val = persistentField.get(element);

                    if (val == null) {

                        logger.debug("search by object (collection sub-field): no inverse key set in element....");
                        logger.debug("querying DB to get the inverse key to target...");

                        //assumes collection element in example has PK set
                        Object dbElement = broker.getObjectByIdentity(new Identity(element));
                        if(dbElement != null) {

                            logger.debug("DB element obtained OK - collection element details:");
                            logger.debug(dbElement.toString());

                            //now get the FK value...
                            val = persistentField.get(dbElement);
                        }
                        else {

                            logger.debug("error - no collection element found in DB either.");
                            logger.debug("unable to build FK relationship between collection elements and target object!!");
                            throw new PersistenceBrokerException("search by object: failed to find example details in DB");
                        }
                    }

                    //now add in the criteria details...
                    FieldDescriptor[] fieldDescs = targetDesc.getPkFields();
                    if((fieldDescs == null) || (fieldDescs.length > 1)) {

                        //a problem - should only have one PK field!!
                        logger.debug("search by object (collection): target object has zero or > 1 primary key fields!!");
                        throw new PersistenceBrokerException("search by object: descriptor for class " + targetDesc.getClassOfObject().getName() + " has zero or > 1 primary keys!!");

                    }
                    String pkField = fieldDescs[0].getAttributeName();
                    criteria.addEqualTo(pkField, val);

                    logger.debug("search by object (collection): searching for object of type " + targetDesc.getClassOfObject().getName() + "with details:");
                    logger.debug("PK field: " + pkField);
                    logger.debug("value: " + val.toString());

                }
            }

        }
        else {

            //something wrong - Collection descriptor is not 1:n or m:n, so unknown!!
            throw new PersistenceBrokerException("object search (collection): collection is not 1:n or m:n relation, and so invalid!!");
        }

    }


    /**
     * Builds a specific query for use with m:n mappings. This case must be handled differently
     * to a 1:n collection mapping as indirection tables are involved.
     *
     * @param targetDesc The Class Descriptor of the target object of the search
     * @param collectionDesc The Collection Descriptor of the m:n mapping
     * @param obj The example object to base a search on
     *
     * @return Query A complete query containing all previous criteria plus m:n (indirection table) details
     *
     * @exception PersistenceBrokerException Thrown if there are OJB problems
     *
     */ private Query buildMtoNQuery(ClassDescriptor targetDesc, CollectionDescriptor collectionDesc, Object obj) throws PersistenceBrokerException {

        //b) indierection table case (ie m:n mappings).....
        logger.debug("search by object (collection): need to check indirection table...");

        Query query = null;

        //get some basic info
        PersistentField field = collectionDesc.getPersistentField();
        Collection collection = (Collection)field.get(obj);

        /*
        * sketch:
        *
        * Need to build a special m:n query with the following info:
        * - the name of the indirection table;
        * - the PK column name of the target object;
        * - the column name of the indirection table that matches the target object PK column;
        * - the PK of a single element in the collection example;
        * - the column of the indirection table matching the element PK
        *
        */

        //NB these return the column names from the indirection table as String objects
        //(undocumented in the OJB javadoc - Object[] is not very helpful!!)
        Object[] elemClassFks = collectionDesc.getFksToItemClass();
        Object[] targetClassFks = collectionDesc.getFksToThisClass();

        String indirectionTable = collectionDesc.getIndirectionTable();

        logger.debug("indirection table: " + indirectionTable);
        logger.debug("column name holding element PK: " + elemClassFks[0]);
        logger.debug("column name holding target PK: " + targetClassFks[0]);

        if(collection != null) {

            Iterator it = collection.iterator();

            if (it.hasNext()) {

                //only need one example element as they would all relate to the same target PK..
                Object elem = it.next();
                ClassDescriptor elemDesc = DescriptorRepository.getDefaultInstance().getDescriptorFor(elem.getClass());
                FieldDescriptor[] elemFieldDesc = elemDesc.getPkFields();

                //assume only one PK for the collection element (not unreasonable!!...)
                PersistentField pkField = elemFieldDesc[0].getPersistentField();
                Object elemPk = pkField.get(elem);

                //now build criteria to match up the element PK, indirection table and target..
                //NB ASSUMES that only one FK to an element class is defined in the Collection Descriptor
                //(which must be true - I think!)
                Criteria criteria = new Criteria();
                criteria.addEqualToColumn(indirectionTable + "." + targetClassFks[0], targetDesc.getPkFields()[0].getAttributeName());

                if (elemPk != null) {

                    logger.debug("PK field for element class " + elemDesc.getClassOfObject().getName() + " : " + elemPk);
                    criteria.addEqualTo(indirectionTable + "." + (String)elemClassFks[0], elemPk);
                }
                else {

                    //must have at least the PK of the example element set, otherwise can't search...
                    throw new PersistenceBrokerException("search by object (m:n collection): collection element must have at least its Primary Key set to perofrm a search");
                }

                //now build the m:n query
                query = new QueryByMtoNCriteria(targetDesc.getClassOfObject(), indirectionTable, criteria);

            }
            else {

                //error - can't work with an empty collection...
                throw new PersistenceBrokerException("search by object (m:n collection): empty collection in search example!!");
            }

        }

        return query;

    }

    /**
     * For a search example containing an object reference, builds the appropriate
     * search criteria. Note: it is assumed that a contained object reference
     * has at least its Primary Key defined.
     *
     * @param refDesc The descriptor for the object reference
     * @param criteria The Criteria object that the result is to be added to
     * @param obj The example search object
     *
     */
    private void buildObjectRefCriteria(ObjectReferenceDescriptor refDesc, Criteria criteria, Object obj) {

        /*
        * Sketch:
        * for each object reference of the target object that has a value set in the example,
        * - get the PK of the reference;
        * - find its corresponding FK field in the target object;
        * - build a query with a Criteria containing addEqualTo for the FK/PK pair
        *
        *
       */

        try {

            //get the value of the reference field from the example object supplied
            PersistentField field = refDesc.getPersistentField();
            Object value = field.get(obj);

            if(value != null) {

                // subqueries not supported by OJB, so can't use them -
                //need then to work with the ClassDescriptor of the reference supplied
                //in the example obj....

                ClassDescriptor valueCd = DescriptorRepository.getDefaultInstance().getDescriptorFor(value.getClass());
                Object[] keys = valueCd.getKeyValues(value);
                if((keys.length > 1) || (keys.length == 0)){

                    //something odd - reference object has zero or more than one PK
                    logger.error("object-based search: contained reference object has zero or more than one PK!!");
                }
                else {

                    //also need the FK in the target that this PK is held in..
                    //the ID of the FK field can be obtained from the ObjectReferenceDescriptor
                    //as a Vector of Integer - but this is likely only ever to contain one ID!!
                    Vector fkIds = refDesc.getForeignKeyFields();
                    if((fkIds.size() > 1) || (fkIds.size() == 0)) {

                        //something odd - reference has zero or more than one FK attribute in the target!!
                        logger.error("object-based search: zero or more than one FK attribute present for an object reference!!");
                    }
                    else {

                        int fkId = ((Integer)fkIds.firstElement()).intValue();

                        //now find the field in the target with this ID and compare to the PK
                        ClassDescriptor cld = DescriptorRepository.getDefaultInstance().getDescriptorFor(obj.getClass());
                        FieldDescriptor fkFieldDesc = cld.getFieldDescriptorByIndex(fkId);
                        PersistentField fkField = fkFieldDesc.getPersistentField();
                        criteria.addEqualTo(fkField.getName(), keys[0]);

                    }

                }

            }
        }
        catch(Throwable ex) {

            logger.error(ex);
        }

    }

}
