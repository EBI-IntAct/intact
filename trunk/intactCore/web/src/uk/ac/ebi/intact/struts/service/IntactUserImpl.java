/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.service;

import java.util.*;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.Assert;

/**
 * This class stores information about an Intact Web user session. Instead of
 * binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class implements the <tt>ttpSessionBindingListener</tt> interface for it
 * can be notified of session time outs.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
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
    private DAO myDAO;

    /**
     * The session started time.
     */
    private Date myStartTime;

    /**
     * The selected topic.
     */
    private String mySelectedTopic;

    /**
     * Maps list name -> list of items.
     */
    private Map myNameToItems = new HashMap();

    /**
     * The current Cv object we are editing.
     */
    private CvObject myEditCvObject;

    /**
     * Constructs an instance of this class with given mapping file and
     * the name of the data source class.
     *
     * @param mapping the name of the mapping file.
     * @param dsClass the class name of the Data Source.
     * @param user the user
     * @param password the password of <code>user</code>.
     *
     * @exception DataSourceException for error in getting the data source; this
     *  could be due to the errors in repository files or the underlying
     *  persistent mechanism rejected <code>user</code> and
     *  <code>password</code> combination.
     * @exception SearchException thrown for any error in creating lists such
     *  as topics, database names etc.
     */
    public IntactUserImpl(String mapping, String dsClass, String user,
                          String password)
        throws DataSourceException, SearchException {
        DAOSource ds = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
        ds.setConfig(fileMap);

        // Cache the DAO.
        // Need a method to get DAO object by passing user and password.
        // myDAO = ds.getDAO(user, password);
        myDAO = ds.getDAO();

        // Cache the list names.
        myNameToItems.put(IntactUserIF.TOPIC_NAMES, makeList(CvTopic.class));
        myNameToItems.put(IntactUserIF.DB_NAMES, makeList(CvDatabase.class));
        myNameToItems.put(IntactUserIF.QUALIFIER_NAMES,
            makeList(CvXrefQualifier.class));

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
    }

    public void setSelectedTopic(String topic) {
        mySelectedTopic = topic;
    }

    public String getSelectedTopic() {
        return mySelectedTopic;
    }

    public Institution getInstitution() throws SearchException {
        return (Institution) getObjectByLabel(
            Institution.class, "EBI");
    }

    public DAO getDAO() {
        return myDAO;
    }

    public Collection getList(String name) {
        return (Collection) myNameToItems.get(name);
    }

    public boolean isListEmpty(String name) {
        Collection list = getList(name);
        Iterator iter = list.iterator();
        return ((String) iter.next()).equals(theirEmptyListItem) && !iter.hasNext();
    }

    public void begin() throws TransactionException {
        // Only begin a transaction if it hasn't been started before.
        if (!myDAO.isActive()) {
            myDAO.begin();
        }
    }

    public void commit() throws TransactionException {
        myDAO.commit();
    }

    public boolean isActive() {
        return myDAO.isActive();
    }

    public void rollback() throws TransactionException {
        // Only rollback if there is an active transaction.
        if (myDAO.isActive()) {
            myDAO.rollback();
        }
    }

    public void create(Object object) throws CreateException {
        myDAO.create(object);
    }

    public void update(Object object) throws CreateException {
       myDAO.update(object);
    }

    public void delete(Object object) throws TransactionException {
        try {
            myDAO.remove(object);
        }
        catch (DataSourceException dse) {
            // We shouldn't get this exception as it is never thrown but declared
            // in the interface.
            Assert.fail(dse.getMessage());
        }
    }

    public void setCurrentEditObject(CvObject cvobj) {
        myEditCvObject = cvobj;
    }

    public CvObject getCurrentEditObject() {
        return myEditCvObject;
    }

    public Object getObjectByLabel(Class clazz, String label)
        throws SearchException {

        Object result = null;

        Collection resultList = search(clazz.getName(), "shortLabel", label);

        if (resultList.isEmpty()) {
            return null;
        }
        Iterator i = resultList.iterator();
        result = i.next();
        if (i.hasNext()) {
            throw new SearchException(
                "More than one object returned by search by label.");
        }
        return result;
    }

    public void removeFromCache(Object object) {
        myDAO.getBroker().removeFromCache(object);
    }

    public Collection search(String objectType, String searchParam,
                              String searchValue) throws SearchException {
        //set up variables used during searching..
        Collection resultList = new ArrayList();

        //now retrieve an object...
        try {
            resultList = myDAO.find(objectType, searchParam, searchValue);
        }
        // The following exceptions are never thrown from the implementation but
        // they are declared on the interface.
        catch (DataSourceException de) {
            Assert.fail(de);
        }
        catch (TransactionException te) {
            Assert.fail(te);
        }
        return resultList;
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
    private Collection makeList(Class clazz) throws SearchException {
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
