/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.business;

import java.util.*;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;

/**
 * This class stores information about an Intact Web user session. Instead of
 * binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class implements the <tt>ttpSessionBindingListener</tt> interface for it
 * can be notified of session time outs.
 *
 * @author Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactUserImpl implements IntactUserIF, HttpSessionBindingListener {

    /**
     * An empty list only contains this item.
     */
    private static final String theirEmptyListItem = "-------------";

    /**
     * Reference to the DAO.
     */
    private IntactHelper helper;

    /**
     * The session started time.
     */
    private Date myStartTime;

    /**
     * Maps list name -> list of items.
     */
    private Map myNameToItems = new HashMap();


    /**
     * Constructs an instance of this class with given mapping file and
     * the name of the data source class.
     *
     * @param mapping the name of the mapping file.
     * @param dsClass the class name of the Data Source.
     *
     * @exception DataSourceException for error in getting the data source; this
     *  could be due to the errors in repository files or the underlying
     *  persistent mechanism rejected <code>user</code> and
     *  <code>password</code> combination.
     * @exception SearchException thrown for any error in creating lists such
     *  as topics, database names etc.
     */
    public IntactUserImpl(String mapping, String dsClass)
        throws DataSourceException, IntactException {
        DAOSource ds = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
        ds.setConfig(fileMap);

        // build a helper for use throughout a session
        //helper = new IntactHelper(ds, user, password);
        helper = new IntactHelper(ds);

        // Cache the list names.
        /*myNameToItems.put(IntactUserIF.TOPIC_NAMES, makeList(CvTopic.class));
        myNameToItems.put(IntactUserIF.DB_NAMES, makeList(CvDatabase.class));
        myNameToItems.put(IntactUserIF.QUALIFIER_NAMES,
            makeList(CvXrefQualifier.class));*/

        // Record the time started.
        myStartTime = Calendar.getInstance().getTime();
    }

    // Implements HttpSessionBindingListener

    /**
     * Will call this method when an object is bound to a session.
     * Not doing anything.
     */
    public void valueBound(HttpSessionBindingEvent event) {
    }

    /**
     * Will call this method when an object is unbound from a session.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {

        try {
            helper.closeStore();
        }
        catch(IntactException ie) {

            //failed to close the store - not sure what to do here yet....
        }
    }

    //not used for search, only CV - dummy to implement IF
    public void setSelectedTopic(String topic) {

    }

    //not used for search, only CV - dummy to implement IF
    public String getSelectedTopic() {
        return null;
    }

    public Institution getInstitution() throws IntactException {
        return (Institution) getObjectByLabel(
            Institution.class, "EBI");
    }

    //not used for search, only CV - dummy to implement IF
    public DAO getDAO() {
        return null;
    }

    public Collection getList(String name) {
        return (Collection) myNameToItems.get(name);
    }

    public boolean isListEmpty(String name) {
        Collection list = getList(name);
        Iterator iter = list.iterator();
        return ((String) iter.next()).equals(theirEmptyListItem) && !iter.hasNext();
    }

    public void begin() throws IntactException {
        // Only begin a transaction if it hasn't been started before.
        if (!helper.isInTransaction()) {
            helper.startTransaction();
        }
    }

    public void commit() throws IntactException {
        helper.finishTransaction();
    }

    public boolean isActive() {
        return helper.isInTransaction();
    }

    public void rollback() throws IntactException {
        // Only rollback if there is an active transaction.
        if (helper.isInTransaction()) {
            helper.undoTransaction();
        }
    }

    public void create(Object object) throws IntactException {
        helper.create(object);
    }

    public void update(Object object) throws IntactException {
       helper.update(object);
    }

    public void delete(Object object) throws IntactException {
        helper.delete(object);
    }

    //not used for search, only CV - dummy to implement IF
    public void setCurrentEditObject(CvObject cvobj) {
    }

    //not used for search, only CV - dummy to implement IF
    public CvObject getCurrentEditObject() {
        return null;
    }

    public Object getObjectByLabel(Class clazz, String label)
        throws IntactException {

        Object result = null;

        Collection resultList = search(clazz.getName(), "shortLabel", label);

        if (resultList.isEmpty()) {
            return null;
        }
        Iterator i = resultList.iterator();
        result = i.next();
        if (i.hasNext()) {
            throw new IntactException(
                "More than one object returned by search by label.");
        }
        return result;
    }
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
        throws IntactException {

        Object result = null;

        result = helper.getObjectByXref(clazz, id);
        return result;
    }

    public void removeFromCache(Object object) {
        // IMPORTANT: THIS IS removed to make the build successful.
        // This could be unnecessary with the new ODMG stuff????
        //myDAO.getBroker().removeFromCache(object);
    }

    public Collection search(String objectType, String searchParam,
                              String searchValue) throws IntactException {
        //set up variables used during searching..
        Collection resultList = new ArrayList();

        //now retrieve an object...
        return helper.search(objectType, searchParam, searchValue);
    }

    // Helper methods.

    /**
     * This method creates a list for given class.
     *
     * @param clazz the class type to create the list.
     *
     * @return list made of short labels for given class type. A special
     * list with <code>theirEmptyListItem</code> is returned if there
     * are no items found for <code>clazz</code>.
     */
    private Collection makeList(Class clazz) throws IntactException {
        // The collection to return.
        Collection list = new ArrayList();

        // Interested in all the records for 'clazz'.
        Collection results = search(clazz.getName(), "ac", "*");

        if (results.isEmpty()) {
            // Special list when we don't have any names.
            list.add(theirEmptyListItem);
            return list;
        }
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            list.add(((CvObject) iter.next()).getShortLabel());
        }
        return list;
    }
}
