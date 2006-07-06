/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.business;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.VirtualProxy;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.proxy.IntactObjectProxy;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.simpleGraph.Edge;
import uk.ac.ebi.intact.simpleGraph.Graph;
import uk.ac.ebi.intact.simpleGraph.Node;
import uk.ac.ebi.intact.util.Key;
import uk.ac.ebi.intact.util.PropertyLoader;

import java.beans.PropertyDescriptor;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


/**
 * <p>This class implements the business logic for intact. The requests to be
 * processed are usually obtained (in a webapp environment) from a struts
 * action class and then the business operations are carried out, via the DAO
 * interface to the data source. </p>
 *
 * @author Chris Lewington
 */
public class IntactHelper implements SearchI, Externalizable {

    /**
     * Path of the configuration file which allow to retrieve the
     * inforamtion related to the IntAct node we are running on.
     */
    private static final String INSTITUTION_CONFIG_FILE = "/config/Institution.properties";

    //initialise variables used for persistence
    private DAO dao = null;
    private DAOSource dataSource = null;
    private Map classInfo = null;

    //used to cache user details if supplied
    private String user;
    private String password;

    /**
     * used internally to manage transaction creation
     */
//    private boolean isInTransaction;

    /** cache to be used for caching by shortLabel.
     *  shortLabel is not a primary key, therefore the caching
     *  by OJB does not work.
     */
    //private HashMap cache = new HashMap();

    /* determines which classes are to be cached.
     *

    private HashSet cachedClasses = new HashSet();
    */

    public void addCachedClass(Class clazz) {

        if (dao != null) {

            dao.addCachedClass(clazz);
        }
    }

    /**
     * Wipe the whole cache.
     */
    public void clearCache() {
        if (dao != null) dao.clearCache();
    }

    /**
     * Removes given object from the cache.
     * @param obj the object to clear from the OJB cache.
     */
    public void removeFromCache(Object obj) {
        dao.removeFromCache(obj);
    }

    /*
    public boolean isCachedClass(Class clazz) {
        return cachedClasses.contains(clazz);
    }
    */

    //a set containing intact properties we are NOT interested in when retirving relations
    //NB this is a result of making the design decision to use Java Bean Introspection
    private Set notRequired = new HashSet();

    //Logger is not serializable - but transient on its own doesn't work!
    private transient Logger pr = null;

    /**
     * Method required for writing to disk via Serializable. Needed because
     * basic serialization does not work due to the use of the Log4J logger.
     * @param out The object output stream.
     * @throws IOException thrown if there were problems writing to disk.
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        pr = null;
        out.writeObject(this);
    }

    /**
     * Used for serialization via Externalizable. Needed because basic serialization
     * does not work with the Log4J logger.
     * @param in The object input stream.
     * @throws IOException Thrwon if there were problems reading from disk.
     * @throws ClassNotFoundException thrown if the class definition needed to create
     * an instance is not available.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        in.readObject();
        if (dataSource != null) pr = dataSource.getLogger();

    }

    public Logger getLogger() {
        return pr;
    }

    /**
     *  Constructor - requires a datasource to be provided.
     *
     * @param source - a datasource object from which a connection could be obtained. Note
     * that use of this constructor prevents optimisation of reflection and may result
     * in slower response times for eg initial searches.
     *
     * @exception IntactException - thrown if either the type map or source are invalid
     *
     */
    public IntactHelper(DAOSource source) throws IntactException {
        this(source, source.getUser(), source.getPassword());
//
//        dataSource = source;
//
//        if (source == null) {
//
//            //couldn't get a mapping from the context, so can't search!!
//            String msg = "intact helper: unable to search for any objects - data source required";
//            throw new IntactException(msg);
//
//        }
//
//        //set up a logger
//        pr = dataSource.getLogger();
//
//        //get a DAO so some work can be done!!
//        try {
//
//            dao = dataSource.getDAO();
//        }
//        catch (DataSourceException de) {
//
//            String msg = "intact helper: There was a problem accessing a data store";
//            throw new IntactException(msg, de);
//
//        }
    }

    /**
     *  Shortcut constructor
     *
     * @exception IntactException - thrown if either the type map or source are invalid
     *
     */
    public IntactHelper() throws IntactException {

        try {
            DAOSource dataSource = DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

            //set the config details, ie repository file for OJB in this case
//            Map config = new HashMap();
//            config.put("mappingfile", "config/repository.xml");
//            dataSource.setConfig(config);

            //set up a logger
            pr = dataSource.getLogger();

            //get a DAO so some work can be done!!
            dao = dataSource.getDAO();
        } catch (DataSourceException de) {

            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException(msg, de);

        }
    }


    /**
     * Constructor allowing a helper instance to be created with a given
     * username and password.
     *
     * @param source - the data source to be connected to
     * @param user - the username to make a connection with
     * @param password - the user's password (null values allowed)
     */
    public IntactHelper(DAOSource source, String user, String password) throws IntactException {

        dataSource = source;

        if (source == null) {

            //couldn't get a mapping from the context, so can't search!!
            String msg = "intact helper: unable to search for any objects - data source required";
            throw new IntactException(msg);

        }

        //set up a logger
        pr = dataSource.getLogger();

        //cache the user details in case they are needed later, eg for reconnection
        this.user = user;
        this.password = password;

        //get a DAO using the supplied user details
        try {

            dao = dataSource.getDAO(user, password);
        } catch (DataSourceException de) {

            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException(msg, de);

        }
    }

    /**
     * <p>
     *  This constructor allows creation of an <code>IntactHelper</code> with a
     * <code>Map</code> of java classnames to <code>PropertyDescriptor</code> information.
     *  Supplying the <code>Map</code> allows for performance improvements since it
     * reduces the reflection operations needed. The format of the <code>Map</code> should
     * be as follows:
     * </p>
     * <p>
     * <ul>
     * <li>key - a full java classname (eg java.lang.String)
     * <li>value - an array of <code>PropertyDescriptor</code> objects
     * </ul>
     *
     * @param source the data source to be used
     * @param classInfo the <code>Map</code> containing reflection information for classes
     *
     * @exception IntactException thrown if no data source is supplied
     *
     */
    public IntactHelper(Map classInfo, DAOSource source) throws IntactException {

        this(source);
        this.classInfo = classInfo;

        //debug info...
        pr.info("intact helper: created, data source is " + source.getClass().getName());
        pr.info("intact helper: classInfo specified, classes are:");
        Set keys = classInfo.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext()) {

            Key k = (Key) iter.next();
            pr.info(k.getKey());
        }

        //as class info is specified, need to set the property names we don't want
        notRequired.add("ac");
        notRequired.add("name");
        notRequired.add("class");

        //get a DAO so some work can be done!!
        try {

            dao = dataSource.getDAO();
        } catch (DataSourceException de) {

            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException(msg, de);

        }
    }

    /**
     * validates a user's credentials against the data store.
     *
     * @param user the username
     * @param password the user password (may be null)
     *
     * @return boolean true if valid details, false if not (including a null username).
     */
    public boolean isUserVerified(String user, String password) {

        if (dao != null) return dao.isUserValid(user, password);
        return false;
    }

    /**
     * close the data source. NOTE: performing this operation will
     * invalidate any outstanding transactions, and a call to open()
     * must be made before further store access can be made. Do not use this method
     * if you wish to continue data store operations in the same connection!
     * Note that any previous data may be lost, and a subsequent call to open()
     * will result in a new connection being established to the persitent store.
     *
     * @exception IntactException if the store was unable to be closed.
     */
    public void closeStore() throws IntactException {

        try {

            dao.close();
        } catch (Exception de) {

            throw new IntactException("failed to close data source!", de);
        }
    }

    /**
     * @return boolean true if a client-initiated transaction is in progress, false if not
     * or if there is no valid connection to the datastore.
     */
    public boolean isInTransaction() {

        if (dao != null) {
            return dao.isActive();
        }
        return false;
    }

    /**
     * Wrapper for uk.ac.ebi.intact.persistence#DAO(Object)
     * @param obj the object to check for.
     * @return <code>true</code> if <code>obj</code> is persisted or it has non
     * null primary key value (shouldn't do it).
     */
    public boolean isPersistent(Object obj) {
        return dao.isPersistent(obj);
    }

    /**
     * starts a business level transaction. This allows finer grained
     * transaction management of business operations (eg for performing
     * a number of creates/deletes within one unit of work). You can choose, by
     * specifying the appropriate parameter, which level of transaction you want:
     * either object level or JDBC (ie relational) level. If an unkown type is supplied
     * then the transaction type defaults to JDBC.
     * @param transactionType The type of transaction you want (relational or object).
     * Use either BusinessConstants.OBJECT_TX or BusinessConstants.JDBC_TX.
     *
     * @exception IntactException thrown usually if a transaction is already running
     */
    public void startTransaction(int transactionType) throws IntactException {

        // The default transaction type is JDBC.
        int txType = BusinessConstants.JDBC_TX;
        if (transactionType == BusinessConstants.OBJECT_TX) {
            txType = BusinessConstants.OBJECT_TX;
        }
        try {
            dao.begin(txType);
        } catch (Exception e) {
            throw new IntactException("unable to start an intact transaction!", e);
        }
    }

    /**
     * Locks the given object for <b>write</b> access.
     * <b>{@link #startTransaction(int)} must be called to prior to this method.</b>
     * @param cvobj the object to lock for <b>write</b> access.
     */
    public void lock(CvObject cvobj) {
        dao.lock(cvobj);
    }

    /**
     * ends a busines transaction. This method should be used if it is required
     * to manage business transactionas across multiple operations (eg for
     * performing a number of creates/deletes in one unit of work).
     *
     * @exception IntactException thrown usually if there is no transaction in progress
     */
    public void finishTransaction() throws IntactException {

        try {
            dao.commit();
        } catch (Exception e) {
            throw new IntactException("unable to complete an intact transaction!", e);
        }
    }

    /**
     * unwraps a transaction. This method is of use when a "business unit of work"
     * fails for some reason and the operations within it should not affect the
     * persistent store.
     *
     * @exception IntactException thrown usually if a transaction is not in progress
     */
    public void undoTransaction() throws IntactException {

        try {
            dao.rollback();
        } catch (Exception e) {
            throw new IntactException("unable to undo an intact transaction!", e);
        }
    }

    /**
     * Provides the database name that is being connected to.
     *
     * @return String the database name, or an empty String if the query fails
     *
     * @exception org.apache.ojb.broker.accesslayer.LookupException thrown on error
     *            getting the Connection
     * @exception SQLException thrown if the metatdata can't be obtained
     */
    public String getDbName() throws LookupException, SQLException {
        return dao.getDbName();
    }

    /**
     * Provides the user name that is connecting to the DB.
     * @return String the user name, or an empty String if the query fails
     * @exception org.apache.ojb.broker.accesslayer.LookupException thrown on error
     * getting the Connection
     * @exception SQLException thrown if the metatdata can't be obtained
     */
    public String getDbUserName() throws LookupException, SQLException {
        return dao.getDbUserName();

    }


    /**
     *  This method provides a create operation for intact objects.
     *
     * @param objects - a collection of intact objects to be created
     *
     * @exception IntactException - thrown if a problem arises during the creation process
     *
     */
    public void create(Collection objects) throws IntactException {

        try {

            if (dao == null) connect();

            //just to be safe, restrict write access..
            synchronized (this) {

                dao.makePersistent(objects);

            }

        } catch (CreateException ce) {

            String msg = "intact helper: object creation failed.. \n";
            throw new IntactException(msg, ce);

        } catch (TransactionException te) {

            String msg = "intact helper: transaction problem during object creation.. \n";
            throw new IntactException(msg, te);

//        } finally {
//
//            //make sure the connection gets closed - but this actually happens in the commit of org.apache.ojb....
//            try {
//
//                // dao.close();
//            } catch (Exception de) {
//
//                String msg = "intact helper: could not close data source connection properly";
//                throw new IntactException(msg, de);
//
//            }
        }
    }

    /**
     *  This method provides a delete operation.
     *
     * @param obj -obj to be deleted
     *
     * @exception IntactException - thrown if a problem arises during the deletion
     *
     */
    public void delete(Object obj) throws IntactException {

        try {

            if (dao == null) connect();

            //just to be safe, restrict write access..
            synchronized (this) {

                dao.remove(obj);

            }

        } catch (Exception de) {

            String msg = "intact helper: failed to delete object of type " + obj.getClass().getName();
            throw new IntactException(msg, de);

        }

    }

    /**
     * Convenience method to create a single object in persistent store.
     *
     * @param obj The object to be created
     *
     * @exception IntactException thrown if the creation failed
     */
    public void create(Object obj) throws IntactException {

        try {

            if (dao == null) connect();

            //just to be safe, restrict write access..
            synchronized (this) {

                dao.create(obj);

            }
        } catch (Exception de) {
            de.printStackTrace();
            String msg = "intact helper: single object creation failed for class " + obj.getClass().getName();
            throw new IntactException(msg, de);

        }

    }

    /**
     *  This method provides an update operation.
     *
     * @param obj -obj to be updated
     *
     * @exception IntactException - thrown if a problem arises during the update
     *
     */
    public void update(Object obj) throws IntactException {

        try {

            if (dao == null) connect();

            //just to be safe, restrict write access..
            synchronized (this) {

                dao.update(obj);

            }
        } catch (Exception de) {
            de.printStackTrace();
            String msg = "intact helper: failed to perform update on class " + obj.getClass().getName();
            throw new IntactException(msg, de);

        }

    }

    /**
     * Cancels the update for the given object.
     * @param obj the object to cancel the update for.
     */
    public void cancelUpdate(Object obj) {
        dao.removeFromCache(obj);
    }

    /*------------------------ search facilities ---------------------------------------

    //methods implemented from the SearchI interface...
    /**
    *  Not Yet Fully Implemented.
     *  @see SearchI
     */
    public Collection paramSearch(String objectType, String searchParam, String searchValue,
                                  boolean includeSubClass,
                                  boolean matchSubString) throws IntactException {
        Collection result = null;

        //implementation TBD

        return result;
    }


    /**
     * Not Yet Fully Implemented.
     * @see SearchI
     */
    public List stringSearch(String objectType, String searchString,
                             boolean includeSubClass,
                             boolean matchSubString) throws IntactException {

        List result = null;
        Class classToSearch = null;
        Field[] fieldsToSearch = null;
        Collection searchResult = null;

        //implementation under development.......
        /*
        * sketch: get the field names from the objectType, and search those
        fields either a) directly (matchSubString=false) or b)using a "like"
        search (matchSubString=true). If includeSubclass is true, do this recursively
        and collect the results until there are no more subclasses.
        Before return, order results in "descending relevance" (!)
        NB the "like" search is possible in OJB, but the DAO interface may need modifying
        to indicate that a "partial" type of search is required..
        */

        try {

            //NB assumes objectType is fully qualified java classname...
            classToSearch = Class.forName(objectType);
            fieldsToSearch = classToSearch.getFields();

            if (matchSubString) {
            } else {

                //standard search - need to check all fields...
                for (int i = 0; i < fieldsToSearch.length; i++) {

                    Field fld = fieldsToSearch[i];
                    searchResult = this.search(objectType, fld.getName(), searchString);
                    if (!searchResult.isEmpty()) {

                        //done
                        break;
                    }
                }

                //to get the list to return, must order the collection by "relevance"...
            }
        } catch (Exception e) {

            //probablky a ClassNotFoundException
            throw new IntactException("error - possible class not found for " + objectType, e);
        }

        return result;
    }

    //other various search methods......

    /**
     * Search which provides an Iterator rather than a Collection. This is particularly
     * useful for applications which may need to close result data for DB resource
     * reasons (the iterator from this method can be passed to <code>closeResults</code>).
     * Note that this will NOT close oracle cursors on the oracle server, as it seems the
     * only guranteed way to do that is to 'cose' the connection (preferably via an oracle
     * connection pool supplied with the driver).
     * @param objectType - the object type to be searched
     * @param searchParam - the parameter to search on (eg field)
     * @param searchValue - the search value to match with the parameter
     *
     * @return Iterator - a "DB aware" Iterator over the results - null if no match found
     *
     * @exception IntactException - thrown if problems are encountered during the search process
     */
    public Iterator iterSearch(String objectType, String searchParam, String searchValue)
            throws IntactException {
        Iterator result = null;
        try {

            result = dao.iteratorFind(objectType, searchParam, searchValue);

        } catch (SearchException se) {

            //return to action servlet witha forward to error page command
            String msg = "intact helper: query by Iterator failed.. \n";
            throw new IntactException(msg + "reason: " + se.getNestedMessage(), se.getRootCause());

        }
        return result;

    }

    /**
     * Allows access to column data rather than whole objects. This is more of a convenience
     * method if you want to get at some data without retrieving complete objects.
     * @param type The name of the object you want data from (fully qualified java name) - must be specified
     * @param cols The columns (NOT attribute names) that you are interested in - null returns all
     * @return Iterator an Iterator over all column values, returned as Objects (null if nothing found),
     * NB if you do not exhaust this Iterator you should make sure datastore resources are
     * cleaned up by passing it to the <code>closeData</code> method.
     * @throws IntactException
     */
    public Iterator getColumnData(String type, String[] cols) throws IntactException {

        Iterator result = null;

        try {
            result = dao.findColumnValues(type, cols);
            return result;
        } catch (SearchException se) {
            throw new IntactException("failed to get column data!", se);
        }

    }

    /**
     * Allows search by raw SQL String. This should only be used if you are confident
     * with SQL syntaxt and you know what you are doing - no gurantees are made on the
     * results (garbage in, garbage out).
     * @param type The object you are interested in - must be specified
     * @param sqlString The SQL string you wish to search with
     * @return Collection The search results, empty if none found
     * @throws IntactException
     */
    public Collection searchBySQL(String type, String sqlString) throws IntactException {

        Collection result = new ArrayList();

        try {
            result = dao.findBySQL(type, sqlString);
            return result;
        } catch (SearchException se) {
            throw new IntactException("failed to execute SQL string " + sqlString, se);
        }
    }

    /**
     * Closes result data.
     * @param items The Iterator used to retrieve the data originally.
     * @exception IllegalArgumentException thrown if the Iterator is an invalid type
     */
    public void closeData(Iterator items) {
        dao.closeResults(items);
    }


    /**
     *  This method provides a means of searching intact objects, within the constraints
     * provided by the parameters to the method. NB this will probably become private, and replaced
     * for public access by paramSearch...
     *
     * @param objectType - the object type to be searched
     * @param searchParam - the parameter to search on (eg field)
     * @param searchValue - the search value to match with the parameter
     *
     * @return Collection - the results of the search (empty Collection if no matches found)
     *
     * @exception IntactException - thrown if problems are encountered during the search process
     */
    public Collection search(String objectType, String searchParam, String searchValue) throws IntactException {

        //set up variables used during searching..
        Collection resultList = new ArrayList();

        /* get a Data Access Object (ie a connection) for the data source
        * NB assumed pooling is managed within the persistence layer..
        */

        if (null == dao) connect();

        //now retrieve an object...
        try {

            long timer = System.currentTimeMillis();

            resultList = dao.find(objectType, searchParam, searchValue);

            long tmp = System.currentTimeMillis();
            timer = tmp - timer;
            pr.info("**************************************************");
            pr.info("intact helper: time spent in DAO find (ms): " + timer);
            pr.info("**************************************************");

            if (resultList.isEmpty()) {

                return new ArrayList();
            }
        } catch (SearchException se) {
            se.printStackTrace();
            //return to action servlet witha forward to error page command
            String msg = "intact helper: unable to perform search operation.. \n";
            throw new IntactException(msg + "reason: " + se.getNestedMessage(), se.getRootCause());

        }
//        finally {
//
//            //done with the connection, so close it
//            try {
//
//                //debug
//                pr.info("intact helper: doing final dao close in search...");
//                //      dao.close();
//                pr.info("intact helper: connection closed OK after search...");
//            } catch (Exception de) {
//
//                String msg = "intact helper: could not close data source connection properly";
//                throw new IntactException(msg, de);
//
//            }
//        }


        return resultList;
    }

    /**
     *  Enables searching by example. This means it is possible to search for matches
     * to a "partial" object where a subset of its fields are complete. Note that unless the
     * field(s) completed in the example are unique, it is possible that there may be more than one
     * match.
     * <p>
     * NOTES ON USAGE:
     * <ul>
     * <li> if a partial object contains any reference objects, at least one of those references must be "complete" (ie have its primary key field set)
     * <li> if a partial object contains a non-empty Collection, at least one Collection element must be "complete"
     * <li> when creating an example search object, subclasses of BasicObject MUST have their
     * created/updated fields set to null. This is because those fields must be non-null in DB tables,
     * and the BasicObject constructor currently sets those fields to the current Date by default. This
     * therefore means that any search example constructed in such a way will not match any DB data as these
     * fields will not match.
     * </ul>
     * </p>
     *
     * @deprecated This should not be used with the new model, as applications should
     * not use no-arg constructors.
     * @param obj - the partial object to search on
     *
     * @return Collection - the results of the search (empty Collection if no matches found)
     *
     * @exception IntactException - thrown if problems are encountered during the search process
     */
    public Collection search(Object obj) throws IntactException {

        //set up variables used during searching..
        Collection resultList = new ArrayList();

        /* get a Data Access Object (ie a connection) for the data source
        * NB assumed pooling is managed within the persistence layer..
        */
        if (null == dao) connect();

        //now do the search...
        try {

            long timer = System.currentTimeMillis();

            resultList = dao.find(obj);

            long tmp = System.currentTimeMillis();
            timer = tmp - timer;
            pr.info("**************************************************");
            pr.info("intact helper: time spent in DAO find (ms): " + timer);
            pr.info("**************************************************");

            if (resultList.isEmpty()) {

                return new ArrayList();
            }

        } catch (SearchException se) {

            //return to action servlet witha forward to error page command
            String msg = "intact helper: unable to perform search operation.. \n";
            throw new IntactException(msg, se);

        }
        return resultList;
    }


    /**
     *  This method obtains the nearest neighbours (ie the next level of related
     * objects) to a given intact object. Usage of this method will primarily be
     * within a lazy loading environment, since its function is to provide a level
     * of optimisation in that respect.
     *
     * @param base - the object whose relations we are interested in (as an intact Base object)
     *
     * @return Collection - a collection of the nearest neighbours to the given object
     *
     */
    public Collection getRelations(BasicObject base) throws IntactException {

        Collection relations = new ArrayList();

        if (base == null) {

            //no parent - throw exception
            String msg = "error - no parent object specified, so can't retrieve relations!";
            throw new IntactException(msg);
        }

        pr.info("intact helper: in getRelations, non-null root object..");

//        Method targetMethod = null;

        //holds return value of invoked method
        Object methodResult = null;
        Method getterMethod = null;


        if (classInfo != null) {

            pr.info("intact helper: got pre-loaded reflection info...");

            String className = base.getClass().getName();
            Key infoKey = new Key(className);
            PropertyDescriptor[] propsInfo = (PropertyDescriptor[]) classInfo.get(infoKey);

            pr.info("intact helper: got pre-loaded property descriptors - processing...");

            //for every property descriptor, run its get method
            for (int i = 0; i < propsInfo.length; i++) {

                String propName = propsInfo[i].getName();

                if (notRequired.contains(propName)) {

                    //don't want this property (ie not declared in the class, but a parent)
                    continue;
                }
                pr.info("intact helper: property descriptor is " + propName);

                try {

                    getterMethod = propsInfo[i].getReadMethod();
                    methodResult = getterMethod.invoke(base, null);

                } catch (Exception e) {

                    String msg = "error- unable to access getter methof for property" + propName;
                    throw new IntactException(msg, e);
                }

                if ((getterMethod == null) || (methodResult == null)) {

                    //can't get the property for some reason - skip
                    continue;
                }

                pr.info("intact helper: result of get method:");
                pr.info(methodResult.toString());

                //NB if a Collection is the method result, get all its members
                //(any lazy loading is hidden here with OJB!!
                if (Collection.class.isAssignableFrom(methodResult.getClass())) {

                    Iterator it = ((Collection) methodResult).iterator();
                    while (it.hasNext()) {

                        relations.add(it.next());
                    }
                } else {

                    relations.add(methodResult);
                }

                pr.info("intact helper: done iteration " + i + " of prop descriptor processing...");

            }

            pr.info("intact helper: done prop processing for object " + base.getClass().getName());

        } else {

            pr.info("intact helper: no pre-loaded reflection info - starting reflection...");
            //no reflection data - have to use reflection directly
            try {

                relations = reflect(base);
            } catch (Exception e) {

                //something went wrong during reflection...
                String msg = "intact helper: unable to get relations - failed retrieval";
                throw new IntactException(msg, e);

            }
        }

        return relations;

    }


    /**
     *  this private method uses reflection to retrieve the next level
     * down of related objects to a root object.
     *
     * @param rootObj - the root object to check
     *
     * @return Collection - a collection of the objects related to the root, or null
     * if the return type of the executed method is void
     *
     * @exception Exception - thrown if errors occur during the refelction process
     *
     */
    private Collection reflect(Object rootObj) throws Exception {

        if (rootObj == null) {

            //no parent - throw exception
            String msg = "error - no parent object specified, so can't retrieve relations!";
            throw new Exception(msg);
        }

        pr.info("intact helper: in reflect method, non-null root object..");

        Method targetMethod = null;
        Collection relatedObjects = new ArrayList();

        //holds return value of invoked method
        Object methodResult = null;

        pr.info("intact helper: no pre-loaded reflection info - starting reflection...");

        Field[] fields = null;
        fields = rootObj.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {

//            Class fieldType = fields[i].getType();

            //NB can't use the Field 'get' method as the field is declared private...

            //some tedious case conversion, needed due to the above to find a valid method..
            StringBuffer buf = new StringBuffer(fields[i].getName());
            char ch = Character.toUpperCase(buf.charAt(0));
            buf.setCharAt(0, ch);

            String methodName = "get" + buf.toString();

            try {

                //should generalise this later if more ops required...
                //zero-param method to be called - returns null if method return type is void
                targetMethod = rootObj.getClass().getMethod(methodName, null);
                methodResult = targetMethod.invoke(rootObj, null);
                relatedObjects.add(methodResult);

                //need to handle collection result here too....TBD

            } catch (NoSuchMethodException nme) {

                //method does not exist in this object - do something
                String msg1 = "Failed search: java NoSuchMethod exception thrown during reflection... \n";
                String msg2 = "Object Type: " + rootObj.getClass().getName() + "\n";
                String msg3 = "method Name: " + methodName;
                String msg = msg1 + msg2 + msg3;
                throw new Exception(msg);
            } catch (SecurityException se) {

                //not allowed to invoke it - do something
                String msg1 = "Failed search: java Security exception thrown during refelction \n";
                String msg2 = "Object Type: " + rootObj.getClass().getName() + "\n";
                String msg3 = "method Name: " + methodName;
                String msg = msg1 + msg2 + msg3;
                throw new Exception(msg);
            } catch (IllegalArgumentException iae) {

                //class does not specify such a method - do something
                String msg1 = "Failed search: java IllegalArgument exception thrown during reflection \n";
                String msg2 = "Object Type: " + rootObj.getClass().getName() + "\n";
                String msg3 = "method Name: " + methodName;
                String msg = msg1 + msg2 + msg3;
                throw new Exception(msg);
            } catch (IllegalAccessException ae) {

                //method is not accessible (eg for security reasons) - do something
                String msg1 = "Failed search: java IllegalAccess exception thrown during reflection \n";
                String msg2 = "Object Type: " + rootObj.getClass().getName() + "\n";
                String msg3 = "method Name: " + methodName;
                String msg = msg1 + msg2 + msg3;
                throw new Exception(msg);
            } catch (InvocationTargetException te) {

                //method threw an exception - do something
                String msg1 = "Failed search: java InvocationTarget exception thronw during reflection \n";
                String msg2 = "Object Type: " + rootObj.getClass().getName() + "\n";
                String msg3 = "method Name: " + methodName;
                String msg = msg1 + msg2 + msg3;
                throw new Exception(msg);
            }

        }

        pr.info("intact helper: leaving doRelations...");
        return relatedObjects;
    }

    /** Searches for objects by classname and Xref.
     *  Currently this searches only by primaryId.
     *  Should search by database and primaryId.
     */
    public Collection getObjectsByXref(Class clazz,
                                       String aPrimaryId) throws IntactException {

        // get the Xref from the database
        Collection xrefs = this.search(Xref.class.getName(), "primaryId", aPrimaryId);
        Collection results = new ArrayList();

        // add all referenced objects of the searched class
        for (Iterator iterator = xrefs.iterator(); iterator.hasNext();) {
            Xref xref = (Xref) iterator.next();
            results.addAll(this.search(clazz.getName(), "ac", xref.getParentAc()));
        }
        return results;
    }

    /**
     * Searches for splice objects by primaryId.
     * @param primaryId Primary Id to search for proteins.
     * @return a collection of splice proteins for <code>primaryId</code> or an
     * empty collection if none are found.
     * @exception IntactException if there is more than single protein returned from
     * the search or an error ocurred in searching.
     *
     * <b>This method is a temporary method added to get the splice proteins.
     * This will be removed shortly once the necessary changes are performed to
     * get all the splice proteins. Only the editor uses this method</b>
     */
    public Collection getSpliceProteinsByXref(String primaryId)
            throws IntactException {
        // The results to return.
        Collection results = new ArrayList();

        // Proteins retrieved via xrefs.
        Collection proteins = getObjectsByXref(Protein.class, primaryId);

        // Empty if no protein is found for the primary id.
        if (proteins.isEmpty()) {
            // An empty collection if no proteins found.
            return results;
        }
        if (proteins.size() > 1) {
            return getObjectsByXref(Protein.class, primaryId);
        }
        // The primary protein.
        Protein protein = (Protein) proteins.iterator().next();

        // Add the 'primary' protein.
        results.add(protein);

        // All splice proteins have 'this' protein as the primary id.
        Collection spProteins = search(Xref.class.getName(), "primaryId",
                protein.getAc());

        // The iso-form to check for splice or not.
        CvXrefQualifier isoForm = (CvXrefQualifier) getObjectByLabel(
                CvXrefQualifier.class, "isoform-parent");

        // Loop through proteins collection; only add the splice proteins.
        for (Iterator iterator = spProteins.iterator(); iterator.hasNext();) {
            Xref xref = (Xref) iterator.next();
            // Filter out proteins which aren't iso-forms.
            if (xref.getCvXrefQualifier().equals(isoForm)) {
                results.addAll(search(Protein.class.getName(), "ac",
                        xref.getParentAc()));
            }
        }
        return results;
    }

    /** Searches for a unique Object by classname and Xref.
     *  Currently this searches only by primaryId.
     *  Should search by database and primaryId.
     */
    public Object getObjectByXref(Class clazz,
                                  String aPrimaryId) throws IntactException {

        Collection results = getObjectsByXref(clazz, aPrimaryId);

        //should be unique...
        if (results.size() > 1) {
            throw new IntactException("error - more than one result returned with query by "
                    + aPrimaryId + " ");
        } else {
            if (results.isEmpty()) {
                return null;
            }
            Iterator it = results.iterator();
            return it.next();
        }
    }


    /**
     * Search for objects by any alias with the specified name.
     *
     * @param clazz the class filter.
     *              All objects in the returned collection will be instance of that class.
     * @param aliasName the name of the alias to look for.
     * @return the collection of objects of type <code>clazz</code> who have an alias
     *         named <code>aliasName</code>
     * @throws IntactException when an error occurs when searching.
     * @throws IllegalArgumentException if the name of the Alias is not specified or empty.
     * @see uk.ac.ebi.intact.model.Alias
     */
    public Collection getObjectsByAlias(Class clazz,
                                        String aliasName) throws IntactException {

        if (aliasName == null || "".equals(aliasName))
            throw new IllegalArgumentException("You have requested a search by alias, but the specified alias name is null.");

        // get the Alias from the database
        Collection aliases = search(Alias.class.getName(), "name", aliasName);
        Collection results = new ArrayList();

        // add all referenced objects of the searched class
        for (Iterator iterator = aliases.iterator(); iterator.hasNext();) {
            Alias alias = (Alias) iterator.next();
            results.addAll(search(clazz.getName(), "ac", alias.getParentAc()));
        }
        return results;
    }

    /**
     * Search for objects by any alias with the specified name.
     *
     * @param clazz the class filter.
     *              All objects in the returned collection will be instance of that class.
     * @param aliasName the name of the alias to look for.
     * @param aliasTypeShortLabel the shortLabel of the alias type we are interrested in.
     * @return the collection of objects of type <code>clazz</code> who have an alias
     *         named <code>aliasName</code>
     * @throws IntactException When the requested aliasType is not found or an error occurs when searching.
     * @throws IllegalArgumentException if the name of the Alias is not specified or empty.
     * @see uk.ac.ebi.intact.model.Alias
     */
    public Collection getObjectsByAlias(Class clazz,
                                        String aliasName,
                                        String aliasTypeShortLabel) throws IntactException {

        if (aliasName == null || "".equals(aliasName))
            throw new IllegalArgumentException("You have requested a search by alias, but the specified alias name is null.");

        if (aliasTypeShortLabel == null)
            throw new IllegalArgumentException("You have requested a search by alias (" + aliasName +
                    ") but the specified alias type is null.");

        CvAliasType cvAliasType = (CvAliasType) getObjectByLabel(CvAliasType.class, aliasTypeShortLabel);
        if (cvAliasType == null)
            throw new IntactException("The requested CvAliasType (" + aliasTypeShortLabel + ") could not be found in the database.");

        return getObjectsByAlias(clazz, aliasName, cvAliasType);
    }

    /**
     * Search for objects by any alias with the specified name.
     *
     * @param clazz the class filter.
     *              All objects in the returned collection will be instance of that class.
     * @param aliasName the name of the alias to look for.
     * @param cvAliasType the type of the alias we are interrested in.
     * @return the collection of objects of type <code>clazz</code> who have an alias
     *         named <code>aliasName</code>
     * @throws IntactException When the requested aliasType is not found or an error occurs when searching.
     * @throws IllegalArgumentException if the name of the Alias is not specified or empty.
     * @see uk.ac.ebi.intact.model.Alias
     */
    public Collection getObjectsByAlias(Class clazz,
                                        String aliasName,
                                        CvAliasType cvAliasType) throws IntactException {

        // get the Xref from the database
        Collection aliases = this.search(Alias.class.getName(), "name", aliasName);
        Collection results = new ArrayList();

        // add all referenced objects of the searched class
        for (Iterator iterator = aliases.iterator(); iterator.hasNext();) {
            Alias alias = (Alias) iterator.next();
            if (!alias.getCvAliasType().equals(cvAliasType))
                continue; // we use only aliases mathing with the requested CvAliasType
            results.addAll(this.search(clazz.getName(), "ac", alias.getParentAc()));
        }
        return results;
    }


    /** Return an Object by classname and shortLabel.
     *  For efficiency, classes which are subclasses of CvObject are cached
     *  if the label is unique.
     *
     */
    public Object getObjectByLabel(Class clazz,
                                   String label) throws IntactException {

        /** Algorithm sketch:
         *  if (clazz is a controlled vocabulary class){
         *     if (not (clazz is cached)){
         *        load clazz into cache
         *     }
         *     return element from cache;
         *  return element from search;
         */

        Object result = null;

        /*
        if (isCachedClass(clazz)){
            result = cache.get(clazz + "-" + label);
             if (null != result){
                 return result;
             }
         }
         */

        Collection resultList = null;

        resultList = this.search(clazz.getName(), "shortLabel", label);

        if (resultList.isEmpty()) {
            result = null;
        } else {
            Iterator i = resultList.iterator();
            result = i.next();
            if (i.hasNext()) {
                IntactException ie = new DuplicateLabelException(label, clazz.getName());
                throw(ie);
            }
        }

        /*
        if (isCachedClass(clazz)){
            cache.put(clazz + "-" + label,result);
        }
        */

        return result;
    }

    /** Return an Object by classname and ac.
     *
     */
    public Object getObjectByAc(Class clazz,
                                String ac) throws IntactException {

        Object result = null;

        Collection resultList = null;

        resultList = this.search(clazz.getName(), "ac", ac);

        if (resultList.isEmpty()) {
            result = null;
        } else {
            Iterator i = resultList.iterator();
            result = i.next();
            if (i.hasNext()) {
                IntactException ie = new DuplicateLabelException(ac, clazz.getName());
                throw(ie);
            }
        }

        return result;
    }

    /**
     *  This method retrieves an intact object, given a class and the object's full
     * name (provided that name is unique).
     *
     * @param clazz the class to search on
     * @param name the name of the object required
     *
     * @return Object the result of the search, or null if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     */
    public Object getObjectByName(Class clazz,
                                  String name) throws IntactException {

        Object result = null;

        Collection resultList = null;

        resultList = this.search(clazz.getName(), "fullName", name);

        if (resultList.isEmpty()) {
            result = null;
        } else {
            Iterator i = resultList.iterator();
            result = i.next();
            if (i.hasNext()) {
                IntactException ie = new IntactException("More than one object returned by search by name.");
                throw(ie);
            }
        }

        return result;
    }

    /**
     *  This method is used for searching by short label (provided that name is unique).
     *
     * @param name the name of the BioSource required
     *
     * @return BioSource the result of the search, or null if not found
     *
     * @exception IntactException thrown if a search problem occurs, or more than
     * one BioSource found
     *
     */
    public BioSource getBioSourceByName(String name) throws IntactException {

        Collection resultList = null;
        resultList = this.search("uk.ac.ebi.intact.model.BioSource", "shortLabel", name);

        if (resultList.isEmpty()) return null;
        if (resultList.size() > 1) throw new IntactException("More than one BioSource returned by name search");
        return (BioSource) resultList.iterator().next();
    }

    /**
     *  Searches for a BioSource given a tax ID. Only a single BioSource is found
     * for given tax id and null values for cell type and tissue.
     * @param taxId The tax ID to search on - should be unique
     * @return BioSource The matching BioSource object, or null if none found (for the
     * combination of tax id, cell and tissue)
     * @exception IntactException thrown if there was a search problem.
     *
     */
    public BioSource getBioSourceByTaxId(String taxId) throws IntactException {

        //List of biosurce objects for given tax id
        Collection results = search(BioSource.class.getName(), "taxId", taxId);

        // Get the biosource with null values for cell type and tisse
        //  (there is only one of them exists).
        for (Iterator iter = results.iterator(); iter.hasNext(); )  {
            BioSource biosrc = (BioSource) iter.next();
            if ((biosrc.getCvCellType() == null) && (biosrc.getCvTissue() == null)) {
                return biosrc;
            }
        }
        // None found.
        return null;
    }


    /**
     *  This method is used for obtaining an interactor given a specific BioSource and
     * the subclass of Interactor that is to be searched for. NB is it true that a match would
     * be unique??
     *
     * @param clazz the subclass of Interactor to search on
     * @param source the BioSource to search with - must be fully defined or at least AC set
     *
     * @return Collection the list of Interactors that have the given BioSource, or empty if none found
     *
     * @exception IntactException thrown if a search problem occurs
     * @exception NullPointerException if source or class is null
     * @exception IllegalArgumentException if the class parameter is not assignable from Interactor
     *
     * NB Not tested yet - BioSource data in DB required
     *
     */
    public Collection getInteractorBySource(Class clazz, BioSource source) throws IntactException {

        if (source == null) throw new NullPointerException("Need a BioSource to search by BioSource!");
        if (clazz == null) throw new NullPointerException("Class is null for Interactor/BioSource search!");
        if (!Interactor.class.isAssignableFrom(clazz))
            throw new IllegalArgumentException("Cannot do Interactor search - Class "
                    + clazz.getName() + "is not a subclass of Interactor");

        return (this.search(Interactor.class.getName(), "bioSource_ac", source.getAc()));

    }


    /**
     *  Search for an experiment given a specific BioSource. NB is it true that a match would
     * be unique?? Object model suggests a 1-1 match...
     *
     * @param source the BioSource to search with - must be fully defined or at least AC set
     *
     * @return Experiment the result of the search, or empty if none found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     * NB not tested yet!! (requires data in the DB)
     *
     */
    public Experiment getExperimentBySource(BioSource source) throws IntactException {

        Experiment result = null;
        Collection resultList = null;
        String bioAc = source.getAc();
        resultList = this.search(Experiment.class.getName(), "bioSource_ac", bioAc);
        if (resultList.isEmpty()) {
            result = null;
        } else {
            Iterator i = resultList.iterator();
            result = (Experiment) i.next();
            if (i.hasNext()) {
                IntactException ie = new IntactException("More than one object returned by search by bioSource.");
                throw(ie);
            }
        }

        return result;
    }

    /**
     *  Search for components given a role. Assumed this is unlikely to be unique.
     *
     * @param role the role to search with - must be fully defined or at least AC set
     *
     * @return Collection the result of the search, or an empty Collection if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     * NB Not tested yet!!
     *
     */
    public Collection getComponentsByRole(CvComponentRole role) throws IntactException {

        return (this.search(Component.class.getName(), "role", role.getAc()));

    }

    /**
     *  Search for interactions given a type. Assumed this is unlikely to be unique.
     *
     * @param type the type to search with - must be fully defined or at least AC set
     *
     * @return Collection the result of the search, or an empty Collection if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     * NB Not tested yet!!
     *
     */
    public Collection getInteractionsByType(CvInteractionType type) throws IntactException {

        return (this.search(Interaction.class.getName(), "interactionType_ac", type.getAc()));
    }


    /**
     *  Search for Experiments given an Institution. Assumed this is unlikely to be unique.
     * Note: If the Institution AC is not set, a search will first be carried out for it
     * using its shortLabel - thus only Institutions that are persistent will provide
     * a non-empty search result. If more than one Institution is returned,
     * the first one's AC will be used.
     *
     * @param institution the institution to search with
     *
     * @return Collection the result of the search, or an empty Collection if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     */
    public Collection getExperimentsByInstitution(Institution institution) throws IntactException {

        String ownerAc = institution.getAc();
        if (ownerAc == null) {
            //get it first via shortLabel
            Collection tmpResults = this.search(Institution.class.getName(), "shortLabel", institution.getShortLabel());
            if (!tmpResults.isEmpty()) {
                //take the first one
                Institution inst = (Institution) tmpResults.iterator().next();
                ownerAc = inst.getAc();
            }
        }
        return (this.search(Experiment.class.getName(), "owner_ac", ownerAc));
    }

    /**
     *  Search for Protein given a cross reference. Assumed this is unique.
     *
     * @param xref the cross reference to search with - must be fully defined, or
     * at least have its parent AC field set
     *
     * @return Protein the result of the search, or null if not found
     *
     * @exception IntactException thrown if a search problem occurs, the Xref does
     * not match a Protein, or we get more than one Protein back
     *
     * NB not yet tested....
     *
     */
    public Protein getProteinByXref(Xref xref) throws IntactException {

        Collection resultList = null;
        resultList = this.search(Protein.class.getName(), "ac", xref.getParentAc());
        if (resultList.isEmpty()) return null;
        if (resultList.size() > 1) throw new IntactException("Got more than one Protein with Xref " + xref.getAc());

        Object obj = resultList.iterator().next();
        if (!(obj instanceof Protein))
            throw new IntactException("Xref refers to an instance of "
                    + obj.getClass().getName() + ", NOT a Protein!!");

        return (Protein) obj;
    }

    /**
     * Delete all elements in a collection.
     */
    public void deleteAllElements(Collection aCollection) throws IntactException {
        try {

            if (dao == null) connect();

            //just to be safe, restrict write access..
            synchronized (this) {
                for (Iterator i = aCollection.iterator(); i.hasNext();) {
                    dao.remove(i.next());
                }
            }

        } catch (Exception de) {

            String msg = "intact helper: error deleting collection elements";
            throw new IntactException(msg, de);

        }
    }

    /**
     * Returns a subgraph centered on startNode.
     * The subgraph will contain all nodes which are up to graphDepth interactions away from startNode.
     * Only Interactions which belong to one of the Experiments in experiments will be taken into account.
     * If experiments is empty, all Interactions are taken into account.
     *
     * Graph depth:
     * This parameter limits the size of the returned interaction graph. All baits are shown with all
     * the interacting preys, even if they would normally be on the "rim" of the graph.
     * Therefore the actual diameter of the graph may be 2*(graphDepth+1).
     *
     * Expansion:
     * If an Interaction has more than two interactors, it has to be defined how pairwise interactions
     * are generated from the complex data. The possible values are defined in the beginning of this file.
     *
     * @param startNode - the start node of the subgraph.
     * @param graphDepth - depth of the graph
     * @param experiments - Experiments which should be taken into account
     * @param complexExpansion - Mode of expansion of complexes into pairwise interactions
     * @param graph - the graph we have to fill with interaction data
     *
     * @return a GraphI object.
     *
     * @exception IntactException - thrown if problems are encountered
     */
    public Graph subGraph(Interactor startNode,
                          int graphDepth,
                          Collection experiments,
                          int complexExpansion,
                          Graph graph) throws IntactException {

        //System.out.println("subGraph called: " + startNode.getAc() + " Depth: " + graphDepth);

        if (startNode instanceof Interaction) {
            graph = subGraphPartial((Interaction) startNode, graphDepth, experiments, complexExpansion, graph);
        } else if (startNode instanceof Interactor) {
            graph = subGraphPartial(startNode, graphDepth, experiments, complexExpansion, graph);
        }
        return graph;

    }

    private Graph subGraphPartial(Interactor startNode,
                                  int graphDepth,
                                  Collection experiments,
                                  int complexExpansion,
                                  Graph partialGraph) throws IntactException {

        /* This should not occur, but is ok. */
        if (null == startNode) {
            return partialGraph;
        }

        //System.out.println("subGraphPartial (Interactor) called: " + startNode.getAc() + " Depth: " + graphDepth);

        /* If the Interaction has already been visited, return,
           else mark it.
        */
        if (partialGraph.isVisited(startNode)) {
            return partialGraph;
        } else {
            partialGraph.addVisited(startNode);
        }

        /* End of recursion, return */
        if (0 == graphDepth) {
            return partialGraph;
        }

        Iterator i = startNode.getActiveInstances().iterator();

        Component current = null;
        while (i.hasNext()) {
            current = (Component) i.next();

            if (null == current) {
                continue;
            }

            /* Explore the next Interaction */
            partialGraph = subGraphPartial(current.getInteraction(),
                    graphDepth,
                    experiments,
                    complexExpansion,
                    partialGraph);
        }

        return partialGraph;
    }

    private Graph subGraphPartial(Interaction current,
                                  int graphDepth,
                                  Collection experiments,
                                  int complexExpansion,
                                  Graph partialGraph) throws IntactException {


        /* This should not occur, but is ok.
        */
        if (null == current) {
            return partialGraph;
        }

        //System.out.println("subGraphPartial (Interaction) called: " + current.getAc() + " Depth: " + graphDepth);

        /* If the Interaction has already been visited, return,
           else mark it.
        */
        if (partialGraph.isVisited(current)) {
            return partialGraph;
        } else {
            partialGraph.addVisited(current);
        }

        /* Create list of baits - the size is set later according to what we have to store */
        ArrayList baits = null;

        switch (complexExpansion) {
            case Constants.EXPANSION_ALL:
                {
                    baits = new ArrayList(current.getComponents().size());

                    /* all components are considered as baits */
                    Iterator i = current.getComponents().iterator();
                    while (i.hasNext()) {
                        baits.add(i.next());
                    }
                }
                break;
            case Constants.EXPANSION_BAITPREY:
                {
                    /* only report bait-prey relations.
                     * If there is no bait, select one arbitrarily. Choose the first.
                     */
                    Component bait = current.getBait();
                    if (null == bait) {
                        baits = new ArrayList(current.getComponents().size());
                        Iterator i = current.getComponents().iterator();
                        if (i.hasNext()) {
                            baits.add(i.next());
                        }
                    } else {
                        baits = new ArrayList(1);
                        baits.add(bait);
                    }
                }
        }

        /* Create list of preys */
        ArrayList preys = new ArrayList(current.getComponents().size());
        Iterator i = current.getComponents().iterator();
        while (i.hasNext()) {
            preys.add(i.next());
        }

        /* Generate all bait-prey pairs */
        int countBaits = baits.size();
        int countPreys = preys.size();

        for (int j = 0; j < countBaits; j++) {
            //System.out.println("Bait: " + ((Component) baits.get(j)).getInteractor().getAc());
            for (int k = j; k < countPreys; k++) {
                //System.out.println("Prey: " + ((Component) preys.get(k)).getInteractor().getAc());
                Edge edge = new Edge();
                Component baitComponent = (Component) baits.get(j);
                Interactor baitInteractor = baitComponent.getInteractor();
                Component preyComponent = (Component) preys.get(k);
                Interactor preyInteractor = preyComponent.getInteractor();

                if (baitInteractor != preyInteractor) {
                    Node node1 = partialGraph.addNode(baitInteractor);
                    Node node2 = partialGraph.addNode(preyInteractor);

                    edge.setNode1(node1);
                    edge.setComponent1(baitComponent);
                    edge.setNode2(node2);
                    edge.setComponent2(preyComponent);
                    partialGraph.addEdge(edge);
                    //System.out.println("Adding: " + node1.getAc() + " -> " + node2.getAc());
                }
            }
        }

        /* recursively explore all Interactors linked to current Interaction */
        for (Iterator iterator = current.getComponents().iterator(); iterator.hasNext();) {
            Component component = (Component) iterator.next();
            partialGraph = subGraphPartial(component.getInteractor(),
                    graphDepth - 1,
                    experiments,
                    complexExpansion,
                    partialGraph);
        }

        return partialGraph;
    }

    /**
     *  Used to obtain the Experiments related to a search object. For example, if the object is a
     * Protein then we are interested in the Interactions and also the Experiments that the Protein
     * is related to. In practice this therefore means that we only need the Experiments, since
     * the Interactions can be obtained from those.
     *
     * @param item A search result item (though anything can be tried!)
     * @return Collection The Experiments related to the parameter item
     * (empty if none found)
     */
    public Collection getRelatedExperiments(Object item) {

        Collection results = new ArrayList();
        if (item instanceof Protein) {
            //collect all the related experiments (interactions can be displayed from them)
            Protein protein = (Protein) item;
            Collection components = protein.getActiveInstances();
            Iterator iter1 = components.iterator();
            while (iter1.hasNext()) {
                Component component = (Component) iter1.next();
                Interaction interaction = component.getInteraction();
                Collection experiments = interaction.getExperiments();
                Iterator iter2 = experiments.iterator();
                while (iter2.hasNext()) {
                    results.add(iter2.next());
                }
            }
        }

        if (item instanceof Interaction) {
            //collect all experiments....
            Collection experiments = ((Interaction) item).getExperiments();
            Iterator iter3 = experiments.iterator();
            while (iter3.hasNext()) {
                results.add(iter3.next());
            }
        }

        return results;
    }

    /**
     * Gets the underlying JDBC connection. This is a 'useful method' rather than
     * a good practice one as it returns the underlying DB connection (and assumes there is one).
     * No guarantees - if you screw up the Connection you are in trouble!.
     *
     * @return Connection a JDBC Connection, or null if the DAO you are using is not
     * an OJB one.
     * @throws IntactException thrown if there was a problem getting the connection
     */
    public Connection getJDBCConnection() throws IntactException {

        if (dao instanceof ObjectBridgeDAO) {
            try {
                return ((ObjectBridgeDAO) dao).getJDBCConnection();
            } catch (LookupException le) {
                throw new IntactException("Failed to get JDBC Connection!", le);
            }
        }
        return null;
    }


    /**
     * Allow the user not to know about the it's Institution, it has to be configured once
     * in the properties file: ${INTACTCORE_HOME}/config/Institution.properties and then
     * when calling that method, the Institution is either retreived or created according
     * to its shortlabel.
     *
     * @return the Institution to which all created object will be linked.
     */
    public Institution getInstitution() throws IntactException {
        Institution institution = null;

        Properties props = PropertyLoader.load(INSTITUTION_CONFIG_FILE);
        if (props != null) {
            String shortlabel = props.getProperty("Institution.shortLabel");
            if (shortlabel == null || shortlabel.trim().equals(""))
                throw new IntactException("Your institution is not properly configured, check out the configuration file:" +
                        INSTITUTION_CONFIG_FILE + " and set 'Institution.shortLabel' correctly");

            // search for it (force it for LC as short labels must be in LC).
            shortlabel = shortlabel.trim();
            Collection result = search(Institution.class.getName(), "shortLabel", shortlabel);

            if (result.size() == 0) {
                // doesn't exist, create it
                institution = new Institution(shortlabel);

                String fullname = props.getProperty("Institution.fullName");
                if (fullname != null) {
                    fullname = fullname.trim();
                    if (!fullname.equals(""))
                        institution.setFullName(fullname);
                }


                String lineBreak = System.getProperty("line.separator");
                StringBuffer address = new StringBuffer(128);
                String line = props.getProperty("Institution.postalAddress.line1");
                if (line != null) {
                    line = line.trim();
                    if (!line.equals("")) {
                        address.append(line).append(lineBreak);
                    }
                }

                line = props.getProperty("Institution.postalAddress.line2");
                if (line != null) {
                    line = line.trim();
                    if (!line.equals(""))
                        address.append(line).append(lineBreak);
                }

                line = props.getProperty("Institution.postalAddress.line3");
                if (line != null) {
                    line = line.trim();
                    if (!line.equals(""))
                        address.append(line).append(lineBreak);
                }

                line = props.getProperty("Institution.postalAddress.line4");
                if (line != null) {
                    line = line.trim();
                    if (!line.equals(""))
                        address.append(line).append(lineBreak);
                }

                line = props.getProperty("Institution.postalAddress.line5");
                if (line != null) {
                    line = line.trim();
                    if (!line.equals(""))
                        address.append(line).append(lineBreak);
                }

                if (address.length() > 0) {
                    address.deleteCharAt(address.length() - 1); // delete the last line break;
                    institution.setPostalAddress(address.toString());
                }

                String url = props.getProperty("Institution.url");
                if (url != null) {
                    url = url.trim();
                    if (!url.equals(""))
                        institution.setUrl(url);
                }

                this.create(institution);

            } else {
                // return the object found
                institution = (Institution) result.iterator().next();
            }

        } else {
            throw new IntactException("Unable to read the properties from " + INSTITUTION_CONFIG_FILE);
        }

        return institution;
    }

    /**
     * Returns the real object wrapped around the proxy for given object of
     * IntactObjectProxy type.
     * @param obj the object to return the real object from.
     * @return the real object wrapped around the proxy for given object of
     * IntactObjectProxy type; otherwise, <code>obj</code> is returned.
     */
    public static IntactObject getRealIntactObject(IntactObject obj) {
        if (IntactObjectProxy.class.isAssignableFrom(obj.getClass())) {
            return (IntactObject) ((IntactObjectProxy) obj).getRealSubject();
        }
        return obj;
    }

    /**
     * Gives the Object classname, give the real object class name if this is a VirtualProxy class
     *
     * @param obj the object for which we request the real class name.
     * @return the real class name.
     *
     * @see org.apache.ojb.broker.VirtualProxy
     */
    public static Class getRealClassName(Object obj) {
        Class name = null;

        if (obj instanceof VirtualProxy) {
            name = ((IntactObjectProxy) obj).getRealClassName();
        } else {
            name = obj.getClass();
        }

        return name;
    }

    /**
     * From the real className of an object, gets a displayable name.
     *
     * @param obj the object for which we want the class name to display - the object must not be null
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName(Object obj) {

        return getDisplayableClassName(getRealClassName(obj));
    }

    /**
     * From the real className of className, gets a displayable name.
     *
     * @param clazz the class for which we want the class name to display - the class must not be null
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName(Class clazz) {

        return getDisplayableClassName(clazz.getName());
    }

    /**
     * From the real className of className, gets a displayable name.
     * <br>
     * 1. get the real class name.
     * 2. Removes the package name
     * 3. try to remove an eventual Impl suffix
     *
     * @param name the class name for which we want the class name to display - the class must not be null
     * @return the classname to display in the view.
     */
    public static String getDisplayableClassName(String name) {

        int indexDot = name.lastIndexOf(".");
        int indexImpl = name.lastIndexOf("Impl");
        if (indexImpl != -1)
            name = name.substring(indexDot + 1, indexImpl);
        else
            name = name.substring(indexDot + 1);

        return name;
    }

    //---------------- private helper methods ------------------------------------

    /**
     * tries to get a connection if a DAO has (somehow!) not been
     * set.
     *
     * @exception IntactException thrown if obtaining a DAO failed
     */
    private void connect() throws IntactException {

        try {
            if (user != null) {

                //try to create using given user details
                dao = dataSource.getDAO(user, password);
            } else {

                //create as default user
                dao = dataSource.getDAO();
            }
        } catch (DataSourceException de) {
            String msg = "failed to create a DAO when it was (somehow!) originally null";
            throw new IntactException(msg, de);
        }
    }
}


