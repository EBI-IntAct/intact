/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.persistence;

import java.util.*;
import java.lang.reflect.*;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.sql.*;

import org.apache.ojb.broker.*;
import org.apache.ojb.broker.util.logging.*;
import org.apache.ojb.broker.accesslayer.*;
import org.apache.ojb.broker.query.*;
import org.apache.ojb.broker.util.*;
import org.apache.ojb.broker.metadata.*;

import uk.ac.ebi.intact.*;
import uk.ac.ebi.intact.util.Key;

/**
 *  <p>This class provides an ObjectBridge-specific Data Access Object, which
 * effectively wraps up an ObjectBridge broker object. Note that this DAO only
 * provides a wrapped object from the ObjectBridge kernel, NOT the ODMG interface.</p>
 *
 * @author Chris Lewington
 *
 */

public class ObjectBridgeDAO implements DAO {


    /**
     *  holds the instance of the Database (ie a connection)
     */
    private PersistenceBroker broker;

    /**
     * A log utility, used if required
     */
    private Logger logger;

    /**
     * turns debugging on or off
     */
    private boolean debug;

    /** cache to be used for caching by unique search parameters. The parameter
     * is the key, and the value is the key used by the OJB cache.
     *
     */
    private HashMap cache = new HashMap();

    /** determines which classes are to be cached.
     *
     */
    private HashSet cachedClasses = new HashSet();


    public ObjectBridgeDAO(PersistenceBroker broker) {

        this.broker = broker;

        try {

            logger = LoggerFactory.getLogger(Class.forName("org.apache.ojb.broker.util.logging.Log4jLoggerImpl"));
        }
        catch(ClassNotFoundException c) {

            //try and set up a simple logger instead here....
        }

    }

    public void addCachedClass(Class clazz) {

        cachedClasses.add(clazz);
    }

    public boolean isCachedClass(Class clazz) {
        return cachedClasses.contains(clazz);
    }

    public void setLogger(org.apache.ojb.broker.util.logging.Logger p) {

        logger = p;
        debug = false;
    }

    /**
     *   Used to begin a transaction.
     *
     * @exception TransactionException - can be thrown if a transaction is already in progress
     *
     */
    public void begin() throws TransactionException {

        try {

            broker.beginTransaction();
        }
        catch(TransactionAbortedException ae) {

            //transaction already open - do something
            String msg = "failed begin: transaction has been aborted";
            throw new TransactionException(msg, ae);
        }
        catch(TransactionInProgressException pe) {

            //transaction already open - do something
            String msg = "failed begin: cannot begin a transaction as one is already open";
            throw new TransactionException(msg, pe);
        }

    }

    /**
     *   closes a DAO (connection).
     *
     * @exception DataSourceException - thrown if the DAO cannot be closed (details in specific errors)
     *
     */
    public void close() throws DataSourceException {

        //this releases the broker instance - assumes doing this also
        //releases the DB connection....
        PersistenceBrokerFactory.releaseInstance(broker);

    }

    /**
     *   Commits (finishes) a transaction. Usual meaning.
     *
     * @exception TransactionException - thrown if, say, a transaction was not in progress for example
     *
     */
    public void commit() throws TransactionException {

        try {

            broker.commitTransaction();
        }
        catch(TransactionAbortedException tae) {

            //couldn't commit for some reason - do something
            String msg = "transaction commit failed";
            throw new TransactionException(msg, tae);
        }
        catch(TransactionNotInProgressException tne) {

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

        try {

            broker.store(obj);
        }
        catch(PersistenceBrokerException die) {

            String msg = "create failed for object of type " + obj.getClass().getName();
            throw new CreateException(msg, die);
        }

    }



    /**
     *   checks to see if a transaction is currently in progress
     *
     * @return boolean - true if a transaction isa ctive, false otherwise
     */
    public boolean isActive() {

        try {

            return broker.isInTransaction();
        }
        catch(PersistenceBrokerException pbe) {

            //this is never actually thrown in the body of the broker method,
            //but is declared to be!!
            return false;
        }
    }

    /**
     *   checks to see if object saving automatically is turned on
     *  ### This is not supported by the ObjectBridge broker API ###
     * - assume it does not turn JDBC autocommit off (needs to be checked..)
     *
     * @return boolean - true if auto saving is on, false otherwise
     */
    public boolean isAutoSave() {

        return true;
    }

    /**
     *   sets whether or not auto saving is turned on
     *  ### Not supported by ObjectBridge broker API ###
     *
     * @param val - true to turn on, false for off
     */
    public void setAutoSave(boolean val) {

        //do nothing
    }


    /**
     *   checks to see if a transaction is closed
     *   ### not supported by ObjectBridge broker API ###
     *
     * @return boolean - true if closed (default), false otherwise
     */
    public boolean isClosed() {

        return true;
    }

    /**
     *   checks to determine if a given object is persistent or not
     *   ### not supported by ObjectBridge broker API ###
     *
     * @param obj - the object to be checked
     *
     * @return boolean - true if the object is persistent (default), false otherwise
     */
    public boolean isPersistent(Object obj) {

        return true;
    }

    /**
     *   removes an object from persistent store.
     *
     * @param obj - the object to be removed
     *
     * @exception TransactionException - thrown usually if the operation is called outside a transaction
     * @exception DataSourceException - used to flag other errors in the persistence layer
     *
     */
    public void remove(Object obj) throws TransactionException, DataSourceException {

        try {

            broker.delete(obj);

        }
        catch(PersistenceBrokerException pbe) {

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

        try {

            broker.abortTransaction();
        }
        catch(PersistenceBrokerException pbe) {


            String msg = "rollback failed: see OBJ exception details";
            throw new TransactionException(msg, pbe);
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
     * @param value - the value of the search parameter
     *
     * @return a collection containing the results of the searches
     *
     * @exception SearchException -  thrown for errors during the search process, eg query problems
     * @exception TransactionException - thrown for transaction problems, eg called outside a transaction scope
     * @exception DataSourceException -  thrown for other problems usually related to the persistence engine
     *
     */
    public Collection find(String type, String col, String val) throws SearchException, TransactionException, DataSourceException {


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

            //check local cache first for the object ID, in case the col is not the PK
            if(isCachedClass(searchClass)) {

                logger.info("object already cached but not a PK search...");
                Identity id = (Identity)cache.get(searchClass + "-" + col);
                query = new QueryByExample(id);
                logger.info("query using object ID created OK");

            }
            else {

                //build a normal Criteria query
                crit = new Criteria();
                crit.addEqualTo(col, val);
                logger.info("criteria built OK");
                query = new QueryByCriteria(searchClass, crit);
                logger.info("query by criteria built OK: "
                        + type + " " + col + " " + val);

            }

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
            String msg = "search failed: problem executing query " + query.toString();
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

            //must have a unique result that could be queried other than by PK, so cache its ID
            Iterator it = results.iterator();
            Object obj = it.next();
            if(it.hasNext()) {

                //some problem - expecting a unique result...
                throw new SearchException("search error - expecting a unique result but got more than one");
            }
            cache.put(searchClass + "-" + col, obj);
        }

        return results;
    }

    /**
     *   @see uk.ac.ebi.intact.peresistence.DAO
     * Note: the local cache of non-PK fields is not currently used in this method (to do)
     */
    public Collection find(Object obj) throws SearchException, TransactionException, DataSourceException {

        //IMPORTANT!! OJB queryByExample only works on IDs again, ie it is not a "real"
        //search by object. If the example has no ID set it returns all for that class.
        //see private method buildQueryByExample for an attempt to do it properly.....

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



    /**
     * <p>updates a given object which was originally obtained in
     * another transaction. This allows, for example, objects to be read
     * from persistent store in one transaction, modified elsewhere (eg via
     * a user interface object) and then have the changes reflected in persistent
     * store within the context of a <bold>different</bold> transaction. This therefore
     * obviates the need to retrieve the object again in order to perform an update.
     * </p>
     *
     * @param obj - the object to be updated
     *
     * @exception CreateException  - thrown if the object could not me modified,
     * eg called outside a transaction scope
     *
     */
    public void update(Object obj) throws CreateException {

        ObjectModificationDefaultImpl mod = new ObjectModificationDefaultImpl();
        mod.setNeedsUpdate(true);
        try {

            broker.store(obj, mod);

        }
        catch(PersistenceBrokerException pe) {
            //problem doing DB begin/commit, or updating object - do something
            String msg = "object update failed: problem saving object of type " + obj.getClass().getName();
            throw new CreateException(msg, pe);
        }


    }


//-------------------------- private helper methods ----------------------------------------

    /**
     *  This method builds a query string based on the table information
     * provided. Currently only a simple SELECT...WHERE statement is built;
     * if more complex ones are needed in future then this method is the place
     * to modify the logic - note however that this is not a trivial exercise..
     */
    private String buildQuery(String className, String col) {

        StringBuffer buf = new StringBuffer("SELECT p FROM ");
        buf.append(className);
        buf.append(" p WHERE ");
        buf.append(col);
        buf.append("=$1");

        return buf.toString();
    }

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
		ClassDescriptor cld = DescriptorRepository.getInstance().getDescriptorFor(obj.getClass());
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
                    ClassDescriptor elemDesc = DescriptorRepository.getInstance().getDescriptorFor(element.getClass());
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
                ClassDescriptor elemDesc = DescriptorRepository.getInstance().getDescriptorFor(elem.getClass());
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

                ClassDescriptor valueCd = DescriptorRepository.getInstance().getDescriptorFor(value.getClass());
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
                        ClassDescriptor cld = DescriptorRepository.getInstance().getDescriptorFor(obj.getClass());
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
