/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.business;

import java.util.*;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.ResultBean;
import org.apache.commons.collections.CollectionUtils;

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

    // Static data

    /**
     * The name of the topic list.
     */
    private static final String theirTopicNames = "TopicNames";

    /**
     * The name of the database list.
     */
    private static final String theirDBNames = "DatabaseNames";

    /**
     * The name of qualifier list.
     */
    private static final String theirQualifierNames = "QualifierNames";

    /**
     * An empty list only contains this item.
     */
    private static final String theirEmptyListItem = "-------------";

    /**
     * Maps: List Name -> List type. Common to all the users and it is immutable.
     */
    private static final Map theirNameToType = new HashMap();

    /**
     * Single result found with the last search.
     */
    private static final int theirSingleEntry = 0;

    /**
     * Multiple results found with the last search.
     */
    private static final int theirMultipleEntries = 1;

    /**
     * A single element array to be used by toArray() method.
     */
    private static final ResultBean[] RESULT_BEAN_ARRAY = new ResultBean[0];

    // End of static data

    /**
     * The user ID.
     */
    private String myUser;

    /**
     * Reference to the Intact Helper.
     */
    private IntactHelper myHelper;

    /**
     * The session start time.
     */
    private Date myStartTime;

    /**
     * The session end time.
     */
    private Date myEndTime;

    /**
     * Maps list name -> list of items. Made it transient
     */
    private transient Map myNameToItems = new HashMap();

    /**
     * The current Cv object we are editing.
     */
    private CvObject myEditCvObject;

    /**
     * The current view of the user. Not saving the state of the view yet.
     */
    private transient CvViewBean myView;

    /**
     * Stores the status of last search: multiple results or a single result.
     */
    private transient int mySearchResultStatus;

    /**
     * Holds the last search results. No need to save search results.
     */
    private transient Collection mySearchCache = new ArrayList();

    /**
     * Stores the last query result.
     */
    private String myLastQuery;

    /**
     * Stores the class name of the last search.
     */
    private String myLastQueryClass;

    // Static initializer.

    // Fill the maps with list names and their associated classes.
    static {
        theirNameToType.put(theirTopicNames, CvTopic.class);
        theirNameToType.put(theirDBNames, CvDatabase.class);
        theirNameToType.put(theirQualifierNames, CvXrefQualifier.class);
    }

    // Constructors.

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
     *  could be due to the errors in repository files.
     * @exception IntactException for errors in creating IntactHelper; possibly
     * due to an invalid user.
     * @exception SearchException for error in creating lists such as topics,
     *  database names etc.
     */
    public IntactUserImpl(String mapping, String dsClass, String user,
            String password) throws DataSourceException, IntactException,
            SearchException {
        myUser = user;
        DAOSource ds = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
        ds.setConfig(fileMap);

        // Initialize the helper.
        myHelper = new IntactHelper(ds, user, password);

        // Cache the list names.
        for (Iterator iter = theirNameToType.entrySet().iterator();
             iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            myNameToItems.put(entry.getKey(), makeList((Class) entry.getValue()));
        }
        // Record the time started.
        myStartTime = Calendar.getInstance().getTime();
    }

    // Implements HttpSessionBindingListener

    /**
     * Will call this method when an object is bound to a session.
     * Starts the user view.
     */
    public void valueBound(HttpSessionBindingEvent event) {
        // Create my initial view.
        myView = new CvViewBean();
    }

    /**
     * Will call this method when an object is unbound from a session. This
     * method sets the logout time.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        try {
            logoff();
        }
        catch (IntactException ie) {
            // Just ignore this exception. Where to log this?
        }
    }

    // Implementation of IntactUserIF interface.

    public CvViewBean getView() {
        return myView;
    }

    public String getUser() {
        return myUser;
    }

    public void setSelectedTopic(String topic) {
        myView.setTopic(topic);
    }

    public String getSelectedTopic() {
        return myView.getTopic();
    }

    public Institution getInstitution() throws SearchException {
        return (Institution) getObjectByLabel(Institution.class, "EBI");
    }

    public Collection getTopicList() {
        return getList(theirTopicNames);
    }

    public Collection getDatabaseList() {
        return getList(theirDBNames);
    }

    public Collection getQualifierList() {
        return getList(theirQualifierNames);
    }

    public boolean isQualifierListEmpty() {
        return isListEmpty(theirQualifierNames);
    }

    public void refreshList() throws SearchException {
        Class clazz = myEditCvObject.getClass();
        // Has the current edit type got a list?
        if (!theirNameToType.containsValue(clazz)) {
            return;
        }
        // Valid type; must update the list. First get the name of the list.
        for (Iterator iter = theirNameToType.entrySet().iterator();
             iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            if (clazz.equals(entry.getValue())) {
                // The list to update.
                String name = (String) entry.getKey();
                // Remove this from the existing map.
                myNameToItems.remove(name);
                myNameToItems.put(name, makeList((Class) entry.getValue()));
            }
        }
    }

    public void begin() throws IntactException {
        myHelper.startTransaction();
    }

    public void commit() throws IntactException {
        myHelper.finishTransaction();
    }

    public void rollback() throws IntactException {
        myHelper.undoTransaction();
    }

    public void create(Object object) throws IntactException {
        myHelper.create(object);
    }

    public void update(Object object) throws IntactException {
        myHelper.update(object);
    }

    public void delete(Object object) throws IntactException {
        myHelper.delete(object);
    }

    public void setCurrentEditObject(CvObject cvobj) {
        myEditCvObject = cvobj;
        // Update the view using the current CV edit object.
        myView.initialise(cvobj);
    }

    public CvObject getCurrentEditObject() {
        return myEditCvObject;
    }

    public Object getObjectByLabel(Class clazz, String label)
        throws SearchException {
        Collection resultList = search(clazz.getName(), "shortLabel", label);

        if (resultList.isEmpty()) {
            return null;
        }
        Iterator i = resultList.iterator();
        Object result = i.next();
        if (i.hasNext()) {
            throw new SearchException(
                "More than one object returned by search by label.");
        }
        return result;
    }

    public Collection search(String objectType, String searchParam,
                              String searchValue) throws SearchException {
        // Cache the last search details.
        this.myLastQuery = searchParam + "=" + searchValue;
        this.myLastQueryClass = objectType;

        //now retrieve an object...
        try {
            return myHelper.search(objectType, searchParam, searchValue);
        }
        catch (IntactException ie) {
            throw new SearchException("Search failed: " + ie.getNestedMessage());
        }
    }

    public void setSearchResultStatus(int size) {
        mySearchResultStatus = (size == 1) ? theirSingleEntry : theirMultipleEntries;
    }

    public boolean hasSingleSearchResult() {
        return  mySearchResultStatus == theirSingleEntry;
    }

    public String getLastSearchQuery() {
        return myLastQuery;
    }

    public String getLastSearchClass() {
        return myLastQueryClass;
    }

    public void addToSearchCache(Collection results) {
        // Clear previous results.
        mySearchCache.clear();

        // Wrap as ResultsBeans for tag library to display.
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            mySearchCache.add(new ResultBean((CvObject) iter.next()));
        }
    }

    public void addToSearchCache(CvObject cvobj) {
        mySearchCache.add(new ResultBean(cvobj));
    }

    public ResultBean[] getSearchCache() {
        return (ResultBean[]) mySearchCache.toArray(RESULT_BEAN_ARRAY);
    }

    public void removeFromSearchCache(String ac) {
        CollectionUtils.filter(mySearchCache, ResultBean.getPredicate(ac));
    }

    public void logoff() throws IntactException {
        myEndTime = Calendar.getInstance().getTime();
        myHelper.closeStore();
    }

    public Date loginTime() {
        return myStartTime;
    }

    public Date logoffTime() {
        return myEndTime;
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

        Vector v = null;
        try {
            v = CvObject.getMenuList(clazz, myHelper, true);
        }
        catch (IntactException ie) {
            throw new SearchException("Search failed: " + ie.getNestedMessage());
        }
        // Guard against the null pointer.
        if ((v == null) || v.isEmpty()) {
            // Special list when we don't have any names.
            list.add(theirEmptyListItem);
            return list;
        }
        for (Iterator iter = v.iterator(); iter.hasNext();) {
            list.add(iter.next());
        }
        return list;
    }

    /**
     * Returns <code>true</code> only if the list for given name contains
     * a single item and that item equals to the empty list item identifier.
     * @param name the name of the list.
     */
    private boolean isListEmpty(String name) {
        Collection list = (Collection) myNameToItems.get(name);
        Iterator iter = list.iterator();
        return (iter.next()).equals(theirEmptyListItem) && !iter.hasNext();
    }

    /**
     * Returns the collection for given list name.
     * @param name the name of the list; e.g., topic, database etc.
     * @return the list for <code>name</code>. If the current editable object is
     * as same as <code>name/code>'s class, then the cuurent editable's name (short
     * label) wouldn't be included. For example, if the short label for a CvTopic is
     * Function, then the list wouldn't have 'Function' if the current editable object
     * is of CvTopic and its short label is 'Function'.
     */
    private Collection getList(String name) {
        Collection list = (Collection) myNameToItems.get(name);
        Class clazz = (Class) theirNameToType.get(name);

        // Remove the short label only when the current editable object's
        // class and the given class match.
        if (myEditCvObject.getClass().equals(clazz)) {
            // The short label of the CV object we are editing at the moment.
            String label = myEditCvObject.getShortLabel();

            // New collection because we are modifying the list.
            Collection topics = new ArrayList(list);

            // Remove the short label from the drop down list.
            topics.remove(label);
            return topics;
        }
        // No modifcations to the list; just return the cache list.
        return list;
    }
}
