/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.business;

import java.util.*;
import java.beans.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.*;

//as good a logging facility as any other....
import org.apache.ojb.broker.util.logging.Logger;

import uk.ac.ebi.intact.util.*;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.simpleGraph.*;


/**
 * <p>This class implements the business logic for intact. The requests to be
 * processed are usually obtained (in a webapp environment) from a struts
 * action class and then the business operations are carried out, via the DAO interface to
 * the data source. </p>
 *
 * @author Chris Lewington
 */

public class IntactHelper implements SearchI {

    //initialise variables used for persistence
    DAO dao = null;
    DAOSource dataSource = null;
    Map classInfo = null;

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

    /*
    public boolean isCachedClass(Class clazz) {
        return cachedClasses.contains(clazz);
    }
    */

    //a set containing intact properties we are NOT interested in when retirving relations
    //NB this is a result of making the design decision to use Java Bean Introspection
    Set notRequired = new HashSet();

    Logger pr = null;

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

        dataSource = source;

        if (source == null) {

            //couldn't get a mapping from the context, so can't search!!
            String msg = "intact helper: unable to search for any objects - data source required";
            throw new IntactException(msg);

        }

        //NB this needs to be a generic logger, from ObjectBridge?...
        pr = dataSource.getLogger();

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

    }

    /**
     *  This method provides a create operation for intact objects.
     *
     * @param types - a collection of intact objects to be created
     *
     * @exception IntactException - thrown if a problem arises during the creation process
     *
     */
    public void create(Collection objects) throws IntactException {

        try {

            dao = dataSource.getDAO();

            //just to be safe, restrict write access..
            synchronized (this) {

                dao.makePersistent(objects);

            }

        } catch (DataSourceException de) {

            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException(msg, de);

        } catch (CreateException ce) {

            String msg = "intact helper: object creation failed.. \n";
            throw new IntactException(msg, ce);

        } catch (TransactionException te) {

            String msg = "intact helper: transaction problem during object creation.. \n";
            throw new IntactException(msg, te);

        } finally {

            //make sure the connection gets closed - but this actually happens in the commit of org.apache.ojb....
            try {

                // dao.close();
            } catch (Exception de) {

                String msg = "intact helper: could not close data source connection properly";
                throw new IntactException(msg, de);

            }
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

               dao = dataSource.getDAO();

               //just to be safe, restrict write access..
               synchronized(this) {

                dao.remove(obj);

               }

           }

           catch(Exception de) {

                  String msg = "intact helper: could not close data source connection properly";
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

               dao = dataSource.getDAO();

               //just to be safe, restrict write access..
               synchronized(this) {

                dao.update(obj);

               }

           }

           catch(Exception de) {

                  String msg = "intact helper: could not close data source connection properly";
                  throw new IntactException(msg, de);

           }

       }


    /*------------------------ search facilities ---------------------------------------

    //methods implemented from the SearchI interface...
    /**
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
        try {

            if (null == dao) {
                dao = dataSource.getDAO();
            }

        } catch (DataSourceException de) {

            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException(msg, de);

        }

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

            //return to action servlet witha forward to error page command
            String msg = "intact helper: unable to perform search operation.. \n";
            throw new IntactException(msg, se);

        } catch (DataSourceException de) {

            String msg = "intact helper: problem with underlying persistence engine during search";
            throw new IntactException(msg, de);

        } catch (TransactionException te) {

            String msg1 = "intact helper: transaction problem during search - \n";
            String msg2 = "possible cause: transaction not started or not terminated";
            String msg = msg1 + msg2;
            throw new IntactException(msg, te);

        } finally {

            //done with the connection, so close it
            try {

                //debug
                pr.info("intact helper: doing final dao close in search...");
                //      dao.close();
                pr.info("intact helper: connection closed OK after search...");
            } catch (Exception de) {

                String msg = "intact helper: could not close data source connection properly";
                throw new IntactException(msg, de);

            }
        }

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
        try {

            if (null == dao) {
                dao = dataSource.getDAO();
            }

        } catch (DataSourceException de) {

            String msg = "intact helper: There was a problem accessing a data store";
            throw new IntactException(msg, de);

        }

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

        } catch (DataSourceException de) {

            String msg = "intact helper: problem with underlying persistence engine during search";
            throw new IntactException(msg, de);

        } catch (TransactionException te) {

            String msg1 = "intact helper: transaction problem during search - \n";
            String msg2 = "possible cause: transaction not started or not terminated";
            String msg = msg1 + msg2;
            throw new IntactException(msg, te);

        } finally {

            //done with the connection, so close it
            try {

                //debug
                pr.info("intact helper: doing final dao close in search...");
                //      dao.close();
                pr.info("intact helper: connection closed OK after search...");
            } catch (Exception de) {

                String msg = "intact helper: could not close data source connection properly";
                throw new IntactException(msg, de);

            }
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

        Method targetMethod = null;

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

            Class fieldType = fields[i].getType();

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

    /** Return an Object by classname and Xref.
     *  Currently this searches only by primaryId.
     *  Should search by database and primaryId.
     */
    public Object getObjectByXref(Class clazz,
                                  String aPrimaryId) throws IntactException {
        Collection resultList = null;

        // get the Xref from the database
        resultList = this.search(Xref.class.getName(), "primaryId", aPrimaryId);
        if (resultList.isEmpty()) {
            return null;
        } else {
            Iterator i = resultList.iterator();
            Object result = i.next();
            if (i.hasNext()) {
                IntactException ie = new IntactException("More than one object returned by search by primary id:\n"
                        + result + i.next());
                throw(ie);
            }
            // now get the object which belongs to the xref
            resultList = this.search(clazz.getName(), "ac", ((Xref) result).getParentAc());
            if (resultList.isEmpty()) {
                IntactException ie = new IntactException("Xref " + ((Xref) result).getAc() + " has invalid reference.");
                throw(ie);
            } else {
                Iterator j = resultList.iterator();
                Object parent = j.next();
                if (j.hasNext()) {
                    IntactException ie = new IntactException("More than one object of class "
                            + clazz.getName()
                            + (" and ac ")
                            + ((Xref) result).getAc()
                            + " found.");
                    throw(ie);
                } else {
                    return parent;
                }
            }
        }
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
                IntactException ie = new IntactException("More than one object returned by search by label.");
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
     *  This method is used for searching by scientific name (provided that name is unique).
     *  The BioSource object is the only intact object with this attribute.
     *
     * @param name the name of the BioSource required
     *
     * @return BioSource the result of the search, or null if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     * NB Not tested yet - requires BioSource data in DB
     *
     */
    public BioSource getBioSourceByName(String name) throws IntactException {

        BioSource result = null;

        Collection resultList = null;

        resultList = this.search("uk.ac.ebi.intact.model.BioSource", "scientificName", name);

        if (resultList.isEmpty()) {
            result = null;
        } else {
            Iterator i = resultList.iterator();
            result = (BioSource) i.next();
            if (i.hasNext()) {
                IntactException ie = new IntactException("More than one object returned by search by scientific name.");
                throw(ie);
            }
        }

        //this gets the BioSource instance - now need to use its taxId to get the object....
        //Not sure how this should work!!

        return result;
    }

    /**
     *  This method is used for obtaining an interactor given a specific BioSource and
     * the subclass of Interactor that is to be searched for. NB is it true that a match would
     * be unique??
     *
     * @param clazz the subclass of Interactor to search on
     * @param source the BioSource to search with - must be fully defined or at least AC set
     *
     * @return Interactor the result of the search, or null if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     * NB Not tested yet - BioSource data in DB required
     *
     */
    public Interactor getInteractorBySource(Class clazz, BioSource source) throws IntactException {

        Interactor result = null;

        Collection resultList = null;

        Interactor interactor = null;

        /*
        *  Basic idea:
        *  build an interactor (subclass) object, set its bioSource to the param given,
        *  then do a search based on ther "partial" object
        */
        if (Interactor.class.isAssignableFrom(clazz)) {

            try {

                interactor = (Interactor) clazz.newInstance();
                interactor.setBioSource(source);

                //NB created/updated are auto set inside BasicObject -
                //need to turn them OFF in an example for searching otherwise there will be no matches!!
                interactor.setCreated(null);
                interactor.setUpdated(null);
            } catch (Exception e) {

                //probably thrown from the newInstance() call - wrap and rethrow
                IntactException ie = new IntactException("problem creating isnatnce of interactor", e);
                throw ie;
            }

        } else {

            //parameter class is not a subclass of Interactor
            IntactException ie = new IntactException("search by source failed - class parameter must be a subclass of Interactor");
            throw(ie);
        }

        resultList = this.search(interactor);

        if (resultList.isEmpty()) {
            result = null;
        } else {
            Iterator i = resultList.iterator();
            result = (Interactor) i.next();
            if (i.hasNext()) {
                IntactException ie = new IntactException("More than one object returned by search by bioSource.");
                throw(ie);
            }
        }

        return result;
    }


    /**
     *  Search for an experiment given a specific BioSource. NB is it true that a match would
     * be unique?? Object model suggests a 1-1 match...
     *
     * @param source the BioSource to search with - must be fully defined or at least AC set
     *
     * @return Experiment the result of the search, or null if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     * NB not tested yet!! (requires data in the DB)
     *
     */
    public Experiment getExperimentBySource(BioSource source) throws IntactException {

        Experiment result = null;

        Collection resultList = null;

        Experiment exp = new Experiment();
        exp.setBioSource(source);

        //NB created/updated auto-set in BasicObject - turn OFF in search example
        exp.setCreated(null);
        exp.setUpdated(null);

        resultList = this.search(exp);

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

        Collection resultList = null;

        Component comp = new Component();
        comp.setCvComponentRole(role);

        //NB created/updated auto-set in BasicObject - turn OFF in search example
        comp.setCreated(null);
        comp.setUpdated(null);

        resultList = this.search(comp);

        return resultList;
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

        Collection resultList = null;

        Interaction interaction = new Interaction();
        interaction.setCvInteractionType(type);

        //NB created/updated auto-set in BasicObject - turn OFF in search example
        interaction.setCreated(null);
        interaction.setUpdated(null);

        resultList = this.search(interaction);

        return resultList;
    }


    /**
     *  Search for Experiments given an Institution. Assumed this is unlikely to be unique.
     * Usage restriction: the Institution must have, at least, its AC defined.
     *
     * @param institution the institution to search with
     *
     * @return Collection the result of the search, or an empty Collection if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     */
    public Collection getExperimentsByInstitution(Institution institution) throws IntactException {

        Collection resultList = null;

        Experiment exp = new Experiment();
        exp.setOwner(institution);

        //NB created/updated auto-set in BasicObject - turn OFF in search example
        exp.setCreated(null);
        exp.setUpdated(null);

        resultList = this.search(exp);

        return resultList;
    }

    /**
     *  Search for Protein given a cross reference. Assumed this is unique.
     *
     * @param xref the cross reference to search with - must be fully defnined, or
     * at least have its primary key set
     *
     * @return Collection the result of the search, or an empty Collection if not found
     *
     * @exception IntactException thrown if a search problem occurs
     *
     * NB not yet tested - requires object search over a Collection (in development)
     *
     */
    public Protein getProteinByXref(Xref xref) throws IntactException {

        Protein prot = new Protein();
        prot.addXref(xref);

        //NB created/updated auto-set in BasicObject - turn OFF in search example
        prot.setCreated(null);
        prot.setUpdated(null);

        Collection results = this.search(prot);

        //should be unique...
        if (results.size() > 1) {

            throw new IntactException("protein search error - more than one result returned with query by" + xref.toString());
        } else {

            if (results.isEmpty()) {

                return null;
            }
            Iterator it = results.iterator();
            return (Protein) it.next();
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

        System.out.println("subGraph called: " + startNode.getAc() + " Depth: " + graphDepth);

        graph = subGraphPartial(startNode, graphDepth, experiments, complexExpansion, graph);

        return graph;

    };

    private Graph subGraphPartial(Interactor startNode,
                                  int graphDepth,
                                  Collection experiments,
                                  int complexExpansion,
                                  Graph partialGraph) throws IntactException {

        /* This should not occur, but is ok.
        */
        if (null == startNode) {
            return partialGraph;
        }

        System.out.println("subGraphPartial (Interactor) called: " + startNode.getAc() + " Depth: " + graphDepth);

        /* If the Interaction has already been visited, return,
           else mark it.
        */
        if (partialGraph.isVisited(startNode)){
            return partialGraph;
        } else {
            partialGraph.addVisited(startNode);
        }

        /* End of recursion, return
        */
        if (0 == graphDepth) {
            return partialGraph;
        }

        Iterator i = startNode.getActiveInstance().iterator();

        while (i.hasNext()) {
            Component current = (Component) i.next();

            if (null == current) {
                continue;
            }

            /* Explore the next Interaction*/
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

        System.out.println("subGraphPartial (Interaction) called: " + current.getAc() + " Depth: " + graphDepth);

        /* If the Interaction has already been visited, return,
           else mark it.
        */
        if (partialGraph.isVisited(current)){
            return partialGraph;
        } else {
            partialGraph.addVisited(current);
        }

        /* Create list of baits */
        Vector baits = new Vector();

        switch (complexExpansion) {
            case Constants.EXPANSION_ALL:
                {
                    /* all components are considered as baits */
                    Iterator i = current.getComponent().iterator();
                    while (i.hasNext()) {
                        baits.add(i.next());
                    }
                }
                break;
            case Constants.EXPANSION_BAITPREY:
                {
                    /* only report bait-prey relations.
                    If there is no bait, select one arbitrarily. Choose the first.
                    */
                    Component bait = (Component) current.getBait();
                    if (null == bait) {
                        Iterator i = current.getComponent().iterator();
                        if (i.hasNext()) {
                            baits.add(i.next());
                        }
                    } else {
                        baits.add(bait);
                    }
                }
        }

        /* Create list of preys */
        Vector preys = new Vector();
        Iterator i = current.getComponent().iterator();
        while (i.hasNext()) {
            preys.add(i.next());
        }

        /* Generate all bait-prey pairs */
        for (int j = 0; j < baits.size(); j++) {
            System.out.println("Bait: " + ((Component) baits.elementAt(j)).getInteractor().getAc());
            for (int k = j; k < preys.size(); k++) {
                System.out.println("Prey: " + ((Component) preys.elementAt(k)).getInteractor().getAc());
                Edge edge = new Edge();
                Interactor baitInteractor = ((Component) baits.elementAt(j)).getInteractor();
                Interactor preyInteractor = ((Component) preys.elementAt(k)).getInteractor();

                if (baitInteractor != preyInteractor) {
                    Node node1 = partialGraph.addNode(baitInteractor);
                    Node node2 = partialGraph.addNode(preyInteractor);

                    edge.setNode1(node1);
                    edge.setNode2(node2);
                    partialGraph.addEdge(edge);
                    System.out.println("Adding: " + node1.getAc() + " -> " + node2.getAc());
                }
            }
        }

        /* recursively explore all Interactors linked to current Interaction */
        for (Iterator iterator = current.getComponent().iterator(); iterator.hasNext();) {
            Component component = (Component) iterator.next();
            partialGraph = subGraphPartial(component.getInteractor(),
                            graphDepth - 1,
                            experiments,
                            complexExpansion,
                            partialGraph);
            }

        return partialGraph;
    }


}


