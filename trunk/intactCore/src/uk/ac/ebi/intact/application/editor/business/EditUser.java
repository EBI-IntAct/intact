/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditViewBeanFactory;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.ResultBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.business.BusinessConstants;
import uk.ac.ebi.intact.business.DuplicateLabelException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.util.*;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.*;

/**
 * This class stores information about an Intact Web user session. Instead of
 * binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class also implements the <tt>HttpSessionBindingListener</tt> interface
 * for it can be notified of session time outs.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditUser implements EditUserI, HttpSessionBindingListener {

    // Beginning of Inner classes

    // ------------------------------------------------------------------------

    /**
     * Inner class to generate unique ids to use primary keys for CommentBean
     * class.
     */
    private static class UniqueID {

        /**
         * The initial value. All the unique ids are based on this value for any
         * (all) user(s).
         */
        private static long theirCurrentTime = System.currentTimeMillis();

        /**
         * Returns a unique id using the initial seed value.
         */
        private static synchronized long get() {
            return theirCurrentTime++;
        }
    }

    // ------------------------------------------------------------------------

    // End of Inner classes

    // Static data

    /**
     * Single result found with the last search.
     */
    private static final int theirSingleEntry = 0;

    /**
     * Multiple results found with the last search.
     */
    private static final int theirMultipleEntries = 1;

    // End of static data

    /**
     * Reference to the Intact Helper.
     */
    private IntactHelper myHelper;

    /**
     * The session start time.
     */
    private Date mySessionStartTime;

    /**
     * The session end time.
     */
    private Date mySessionEndTime;

    /**
     * Maintains the edit state; it used when inserting the header dynamically.
     * For example, if in the edit state, then append the current object's class
     * type to the header or else display the standard header.
     */
    private boolean myEditState;

    /**
     * The topic selected by the user; transient as it is valid for the
     * current session only.
     */
    private transient String mySelectedTopic;

    /**
     * The current view of the user. Not saving the state of the view yet.
     */
    private transient AbstractEditViewBean myEditView;

    /**
     * Stores the status of last search: multiple results or a single result.
     */
    private transient int mySearchResultStatus;

    /**
     * Holds the last search results. No need to save search results.
     */
    private transient Collection mySearchCache = new ArrayList();

    /**
     * The factory to create various views; e.g., CV, BioSource. The factory lasts
     * only for a session, hence it is transient.
     */
    private transient EditViewBeanFactory myViewFactory;

    /**
     * The factory to create various menus.
     */
    private transient EditorMenuFactory myMenuFactory;

    /**
     * The institution; transient as it is already persistent on the database.
     */
    private transient Institution myInstitution;

    /**
     * Stores the last query input.
     */
    private String myLastQueryInput;

    /**
     * Stores the last query result.
     */
    private String myLastQuery;

    /**
     * Stores the class name of the last search.
     */
//    private String myLastQueryClass;

    /**
     * Reference to Newt proxy server instance; transient as it is created
     * if required.
     */
    private transient NewtServerProxy myNewtServer;

    /**
     * Reference to Go proxy server instance; transient as it is created
     * if required.
     */
    private transient GoServerProxy myGoServer;

    /**
     * Reference to the Protein factory.
     */
    private transient UpdateProteinsI myProteinFactory;

    /**
     * A set of currently edited/added experiment short labels.
     */
    private transient Set myCurrentExperiments = new HashSet();

    // Static Methods.

    /**
     * Returns the unique id based on the current time; the ids are unique
     * for a session.
     */
    public static long getId() {
        return UniqueID.get();
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
     * @exception uk.ac.ebi.intact.persistence.DataSourceException for error in getting the data source; this
     *  could be due to the errors in repository files.
     * @exception IntactException for errors in creating IntactHelper; possibly
     * due to an invalid user.
     */
    public EditUser(String mapping, String dsClass, String user,
                    String password) throws DataSourceException, IntactException {
        DAOSource ds = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
        ds.setConfig(fileMap);

        // Initialize the helper.
        myHelper = new IntactHelper(ds, user, password);

        // A dummy read to ensure that a connection is made as a valid user.
        myHelper.search("uk.ac.ebi.intact.model.Institution", "ac", "*");

        // Initialize the Protein factory.
        try {
            myProteinFactory = new UpdateProteins(myHelper);
        }
        catch (UpdateProteinsI.UpdateException e) {
            throw new IntactException("Unable to create the Protein factory");
        }
        // Record the time started.
        mySessionStartTime = Calendar.getInstance().getTime();
    }

    // Implements HttpSessionBindingListener

    /**
     * Will call this method when an object is bound to a session.
     * Starts the user view.
     */
    public void valueBound(HttpSessionBindingEvent event) {
        // Create the factories.
        myViewFactory = new EditViewBeanFactory();
        myMenuFactory = new EditorMenuFactory(myHelper);
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

    // Implementation of IntactUserI interface.

    public String getUserName() {
        if (myHelper != null) {
            try {
                return myHelper.getDbUserName();
            }
            catch (LookupException e) {
            }
            catch (SQLException e) {
            }
        }
        return null;
    }

    public String getDatabaseName() {
        if (myHelper != null) {
            try {
                return myHelper.getDbName();
            }
            catch (LookupException e) {
            }
            catch (SQLException e) {
            }
        }
        return null;
    }

    public Collection search(String objectType, String searchParam,
                             String searchValue) throws IntactException {
        return myHelper.search(objectType, searchParam, searchValue);
    }

    // Implementation of EditUserI interface.

    public AbstractEditViewBean getView() {
        return myEditView;
    }

    public String getCurrentViewClass() {
        // The object we are editing at the moment.
        AnnotatedObject editObject = myEditView.getAnnotatedObject();
        // The class name of the current edit object.
        String className = editObject.getClass().getName();
        // Strip the package name from the class name.
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public void setSelectedTopic(String topic) {
        mySelectedTopic = topic;
    }

    public String getSelectedTopic() {
        return mySelectedTopic;
    }

    public Institution getInstitution() {
        if (myInstitution == null) {
            try {
                Collection result = myHelper.search(
                        "uk.ac.ebi.intact.model.Institution", "ac", "*");
                // Only one institute per site. Cache it.
                myInstitution = (Institution) result.iterator().next();
            }
            catch (IntactException ie) {
                Logger.getLogger(EditorConstants.LOGGER).info(ie);
            }
        }
        return myInstitution;
    }

    public boolean isEditing() {
        return myEditState;
    }

    public void begin() throws IntactException {
        myHelper.startTransaction(BusinessConstants.OBJECT_TX);
    }

    public void commit() throws IntactException {
        myHelper.finishTransaction();
        this.endEditing();
        // Clear the cache; this will force the OJB to read from the database.
        myHelper.removeFromCache(myEditView.getAnnotatedObject());
    }

    public void rollback() throws IntactException {
        myHelper.undoTransaction();
        this.endEditing();
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

    public void persist() throws IntactException, SearchException {
        myEditView.persist(this);
    }

    public void delete() throws IntactException {
        myEditView.clear();
        // Remove the current edit object from the cache.
//        removeFromSearchCache();
        // Remove this from the experiment list.
        if (myEditView.getClass().isAssignableFrom(ExperimentViewBean.class)) {
            removeFromCurrentExperiment((Experiment) myEditView.getAnnotatedObject());
        }
        delete(myEditView.getAnnotatedObject());
    }

    public void cancelEdit() {
        this.endEditing();
        myEditView.clearTransactions();
    }

    public boolean isPersistent(Object obj) {
        return myHelper.isPersistent(obj);
    }

    public boolean isPersistent() {
        return isPersistent(myEditView.getAnnotatedObject());
    }

    public void updateView(AnnotatedObject annot) {
        // Start editing the object.
        this.startEditing();
        // Get the new view for the new edit object.
        myEditView = myViewFactory.factory(annot);
        myEditView.setMenuFactory(myMenuFactory);
    }

    public Object getObjectByLabel(String className, String label)
            throws SearchException {
        try {
            return getObjectByLabel(Class.forName(className), label);
        }
        catch (ClassNotFoundException cnfe) {
            throw new SearchException("Class not found for " + className);
        }
    }

    public Object getObjectByLabel(Class clazz, String label)
            throws SearchException {
        try {
            return myHelper.getObjectByLabel(clazz, label);
        }
        catch (IntactException ie) {
            String msg;
            if (ie instanceof DuplicateLabelException) {
                msg = label + " already exists";
            }
            else {
                msg = "Failed to find a record for " + label;
            }
            throw new SearchException(msg);
        }
    }

    public Object getObjectByAc(Class clazz, String ac) throws SearchException {
        try {
            return myHelper.getObjectByAc(clazz, ac);
        }
        catch (IntactException ie) {
            String msg;
            if (ie instanceof DuplicateLabelException) {
                msg = ac + " already exists";
            }
            else {
                msg = "Failed to find a record for " + ac;
            }
            throw new SearchException(msg);
        }
    }

    public Collection getSPTRProteins(String pid) {
        return myProteinFactory.insertSPTrProteins(pid);
    }

    public Collection getProteinsByXref(String pid) throws SearchException {
        try {
            return myHelper.getObjectsByXref(Protein.class, pid);
        }
        catch (IntactException e) {
            throw new SearchException(e.getMessage());
        }
    }

    public Collection search1(String objectType, String searchParam,
                             String searchValue) throws SearchException {
        // Retrieve an object...
        try {
            return myHelper.search(objectType, searchParam, searchValue);
        }
        catch (IntactException ie) {
            String msg = "Failed to find any " + objectType + " records for "
                    + searchValue + " as " + searchParam;
            throw new SearchException(msg);
        }
    }

    public boolean hasSingleSearchResult() {
        return mySearchResultStatus == theirSingleEntry;
    }

    public String getSearchInput() {
        return myLastQueryInput;
    }

    public String getSearchQuery() {
        return myLastQuery;
    }

//    public String getSearchClass() {
//        return myLastQueryClass;
//    }

    public void addToSearchCache(Collection results) {
        // Clear previous results.
        mySearchCache.clear();

        // Wrap as ResultsBeans for tag library to display.
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            mySearchCache.add(new ResultBean((AnnotatedObject) iter.next()));
        }
    }

    public void updateSearchCache() {
        // Clear previous results.
        mySearchCache.clear();
        mySearchCache.add(new ResultBean(myEditView.getAnnotatedObject()));
//        AnnotatedObject annobj = myEditView.getAnnotatedObject();
        // Remove from the cache and add it again (this will update any
        // changes done in the editor).
//        removeFromSearchCache();
//        if (mySearchCache.isEmpty()) {
//            return;
//        }
        // Only add to the cache list if they are of same type; the check
        // is made against the first element (assumes that rest are of same type).
//        ResultBean rb = (ResultBean) mySearchCache.iterator().next();
//        if (rb.isSameType(annobj)) {
            // The same type; add it to the cache.
//            mySearchCache.add(new ResultBean(annobj));
//        }
    }

    public Collection lookup(String className, String value, boolean cache)
            throws SearchException {
        // The result to return.
        Collection results = new ArrayList();
        // The search parameter.
        String searchParam = "ac";

        //try search on AC first...
        results = search1(className, searchParam, value);
        if (results.isEmpty()) {
            // No matches found - try a search by label now...
//            super.log("now searching for class " + className + " with label " + value);
            searchParam = "shortLabel";
            results = search1(className, searchParam, value);
            if (results.isEmpty()) {
                //no match on label - try by xref....
                //super.log("no match on label - looking for: " + className + " with primary xref ID " + value);
                searchParam = "primaryId";
                Collection xrefs = search1(Xref.class.getName(), searchParam, value);

                //could get more than one xref, eg if the primary id is a wildcard search value -
                //then need to go through each xref found and accumulate the results...
                Iterator it = xrefs.iterator();
                Collection partialResults = new ArrayList();
                searchParam = "ac";
                while (it.hasNext()) {
                    partialResults = search1(className, searchParam,
                            ((Xref) it.next()).getParentAc());
                    results.addAll(partialResults);
                }
                if (results.isEmpty()) {
                    //no match by xref - try finally by name....
//                    super.log("trying fullname...last resort");
                    searchParam = "fullName";
                    results = search1(className, searchParam, value);
                }
            }
        }
        if (cache) {
            // Cache the search result statuses.
//            myLastQueryClass = className;
            myLastQueryInput = value;
            myLastQuery = searchParam + "=" + value;
            mySearchResultStatus = (results.size() == 1)
                    ? theirSingleEntry : theirMultipleEntries;
        }
        return results;
    }

    public String getUniqueShortLabel(String shortlabel) throws SearchException {
        AnnotatedObject annobj = myEditView.getAnnotatedObject();
        return this.getUniqueShortLabel(shortlabel, annobj.getAc());
    }

    public String getUniqueShortLabel(String shortlabel, String extAc)
            throws SearchException {
        AnnotatedObject annobj = myEditView.getAnnotatedObject();
        try {
            return GoTools.getUniqueShortLabel(myHelper, annobj.getClass(),
                    annobj, shortlabel, extAc);
        }
        catch (IntactException ie) {
            String msg = "Failed to get a unique short label for " + extAc;
            throw new SearchException(msg);
        }
    }

    public boolean duplicateShortLabel(String label) throws SearchException {
        // The object we are editing at the moment.
        AnnotatedObject annobj = myEditView.getAnnotatedObject();
        Class clazz = annobj.getClass();

        // Holds the result from the search.
        Collection results = search1(clazz.getName(), "shortLabel", label);
        if (results.isEmpty()) {
            // Don't have this short label on the database.
            return false;
        }
        // If we found a single record then it could be the current record.
        if (results.size() == 1) {
            // Found an object with similar short label; is it as same as the
            // current record?
            String currentAc = annobj.getAc();
            // ac is null until a record is persisted; current ac is null
            // for a new object.
            if (currentAc != null) {
                // Eediting an existing record.
                String resultAc = ((AnnotatedObject) results.iterator().next()).getAc();
                if (currentAc.equals(resultAc)) {
                    // We have retrieved the same record from the DB.
                    return false;
                }
            }
        }
        // There is another record exists with the same short label.
        return true;
    }

    public List getExistingShortLabels() throws SearchException {
        // The object we are editing at the moment.
        AnnotatedObject editObject = myEditView.getAnnotatedObject();
        // The current edit object's short label.
        String editLabel = editObject.getShortLabel();
        // The class name of the current edit object.
        String className = editObject.getClass().getName();

        // The list to return.
        List list = new ArrayList();

        // Flag to indicate processing of the first item.
        boolean first = true;
        // Search the database.
        Collection results = search1(className, "shortLabel", "*");
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            // Avoid this object's own short label.
            String label = ((AnnotatedObject) iter.next()).getShortLabel();
            if (label.equals(editLabel)) {
                continue;
            }
            if (first) {
                first = false;
            }
            else {
                list.add(label);
            }
        }
        return list;
    }

    public void fillSearchResult(DynaBean dynaForm) {
        dynaForm.set("items", mySearchCache);
    }

    public List getSearchResult() {
        return (List) mySearchCache;
    }

    public void logoff() throws IntactException {
        mySessionEndTime = Calendar.getInstance().getTime();
        myHelper.closeStore();
    }

    public Date loginTime() {
        return mySessionStartTime;
    }

    public Date logoffTime() {
        return mySessionEndTime;
    }

    public NewtServerProxy getNewtProxy() {
        if (myNewtServer == null) {
            try {
                myNewtServer = new NewtServerProxy();
            }
            catch (MalformedURLException murle) {
                // This should never happen because default constructor has a
                // valid URL.
                Logger.getLogger(EditorConstants.LOGGER).info(murle);
            }
        }
        return myNewtServer;
    }

    public GoServerProxy getGoProxy() {
        if (myGoServer == null) {
            myGoServer = new GoServerProxy();
        }
        return myGoServer;
    }

    public String getHelpTag() {
        return myEditView.getHelpTag();
    }

    public void addToCurrentExperiment(Experiment exp) {
        myCurrentExperiments.add(exp);
    }

    public void removeFromCurrentExperiment(Experiment exp) {
        myCurrentExperiments.remove(exp);
    }

    public Set getCurrentExperiments() {
        return myCurrentExperiments;
    }

    public Annotation getAnnotation(CommentBean cb) throws SearchException {
        // The topic for the new annotation.
        CvTopic cvtopic = (CvTopic) getObjectByLabel(CvTopic.class, cb.getTopic());

        // The new annotation to return.
        Annotation annot = new Annotation(getInstitution(), cvtopic);
        annot.setAnnotationText(cb.getDescription());
        return annot;
    }

    public Xref getXref(XreferenceBean xb) throws SearchException {
        // The owner of the object we are editing.
        Institution owner = getInstitution();

        // The database the new xref belong to.
        CvDatabase cvdb = (CvDatabase) getObjectByLabel(CvDatabase.class,
                xb.getDatabase());

        // The CV xref qualifier.
        CvXrefQualifier cvxref = (CvXrefQualifier) getObjectByLabel(
                CvXrefQualifier.class, xb.getQualifier());

        // The new xref to add to the current cv object.
        Xref xref = new Xref(owner, cvdb, xb.getPrimaryId(), xb.getSecondaryId(),
                xb.getReleaseNumber(), cvxref);
        return xref;
    }

    // Helper methods.

//    private void removeFromSearchCache() {
//        String ac = myEditView.getAcNoLink();
//        CollectionUtils.filter(mySearchCache, ResultBean.getPredicate(ac));
//    }

    /**
     * Starts the editing session.
     */
    private void startEditing() {
        myEditState = true;
    }

    /**
     * Finishes the editing session.
     */
    private void endEditing() {
        myEditState = false;
    }
}
