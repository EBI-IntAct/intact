/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.application.commons.search.CriteriaBean;
import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.commons.search.SearchHelperI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditViewBeanFactory;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.ResultBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.business.BusinessConstants;
import uk.ac.ebi.intact.business.DuplicateLabelException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.IOException;
import java.io.ObjectInputStream;
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

    /**
     * Reference to the Intact Helper. This is transient as it is reconstructed
     * using the data source.
     */
    private transient IntactHelper myHelper;

    /**
     * The data source; need this to create a helper.
     */
    private DAOSource myDAOSource;

    /**
     * The session start time. This info is reset at deserialization.
     */
    private transient Date mySessionStartTime;

    /**
     * The session end time. This info is reset at deserialization.
     */
    private transient Date mySessionEndTime;

    /**
     * Maintains the edit state; it used when inserting the header dynamically.
     * For example, if in the edit state, then append the current object's class
     * type to the header or else display the standard header.
     */
    private boolean myEditState;

    /**
     * The topic selected by the user.
     */
    private String mySelectedTopic;

    /**
     * The current view of the user.
     */
    private AbstractEditViewBean myEditView;

    /**
     * Holds the last search results.
     */
    private Collection mySearchCache = new ArrayList();

    /**
     * The factory to create various views; e.g., CV, BioSource. The factory lasts
     * only for a session, hence it is transient. One factory per user.
     */
    private transient EditViewBeanFactory myViewFactory;

    /**
     * The institution; transient as it will be retrieved from the persistent
     * system if not set.
     */
    private transient Institution myInstitution;

    /**
     * Stores the last query result.
     */
    private String myLastQuery;

    /**
     * The search helper. This is recreated if necessary.
     */
    private transient SearchHelperI mySearchHelper;

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
     * A set of currently edited/added experiments.
     */
    private transient Set myCurrentExperiments = new HashSet();

    /**
     * A set of currently edited/added interactions.
     */
    private transient Set myCurrentInteractions = new HashSet();

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
        myDAOSource = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
        myDAOSource.setConfig(fileMap);

        // Save the user info in the DS for us to access them later.
        myDAOSource.setUser(user);
        myDAOSource.setPassword(password);

        // Initialize the object.
        initialize();
    }

    /**
     * This constructor for Seralization test class. The user and password is set
     * to null. This is equivalent to calling
     * {@link EditUser(String, String, String, String)} with null values for
     * user and password.
     * @param mapping the name of the mapping file.
     * @param dsClass the class name of the Data Source.
     * @throws DataSourceException for error in getting the data source; this
     * could be due to the errors in repository files.
     * @throws IntactException for errors in creating IntactHelper.
     *
     * @see uk.ac.ebi.intact.application.editor.test.SessionSerializationTest
     */
    public EditUser(String mapping, String dsClass) throws IntactException,
            DataSourceException {
        this(mapping, dsClass, null, null);
    }

    // Methods to handle special serialization issues.

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        try {
            initialize();
        }
        catch (IntactException ie) {
            throw new IOException(ie.getMessage());
        }
    }

    // Implements HttpSessionBindingListener

    /**
     * Will call this method when an object is bound to a session.
     * Starts the user view.
     */
    public void valueBound(HttpSessionBindingEvent event) {}

    /**
     * Will call this method when an object is unbound from a session. This
     * method sets the logout time.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        // Release any locks.
        releaseLock();
        try {
            logoff();
        }
        catch (IntactException ie) {
            // Just ignore this exception. Where to log this?
        }
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Delegates the task to
     * {@link
     * uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean#equals(Object)
     * }.
     * @param obj the object to compare.
     * @return true only if <code>obj</code> is an instance of this class
     * and its wrapped view equals to this object's view. For all
     * other instances, false is returned.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EditUser)) {
            return false;
        }
        // Can cast it safely.
        EditUser other = (EditUser) obj;
        if (myEditView != null) {
            return myEditView.equals(other.myEditView);
        }
        // Other's view must be null.
        return other.myEditView == null;
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

    public void setView(Class clazz) {
        // Start editing the object.
        this.startEditing();
        // Get the new view for the new edit object.
        myEditView = myViewFactory.factory(clazz);
        setSelectedTopic();
    }

    public void setView(AnnotatedObject annot) {
        // Start editing the object.
        this.startEditing();
        // Get the new view for the new edit object.
        myEditView = myViewFactory.factory(annot.getClass());
        myEditView.setAnnotatedObject(annot);
        setSelectedTopic();
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
        // Only delete if it is persistent.
        if (isPersistent(object)) {
            myHelper.delete(object);
        }
    }

    public void persist() throws IntactException, SearchException {
        myEditView.persist(this);
    }

    public void delete() throws IntactException {
        myEditView.clear();
        // Remove this from the experiment list.
//        if (myEditView.getClass().isAssignableFrom(ExperimentViewBean.class)) {
//            removeFromCurrentExperiment((Experiment) myEditView.getAnnotatedObject());
//        }
        delete(myEditView.getAnnotatedObject());
    }

    public void cancelEdit() {
        this.endEditing();
        myEditView.clearTransactions();
    }

    public boolean isPersistent(Object obj) {
        if (obj != null) {
            return myHelper.isPersistent(obj);
        }
        return false;
    }

    public boolean isPersistent() {
        return isPersistent(myEditView.getAnnotatedObject());
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

    public String getSearchQuery() {
        return myLastQuery;
    }

    public void addToSearchCache(Collection results) {
        // Clear previous results.
        mySearchCache.clear();

        // Wrap as ResultsBeans for tag library to display.
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            mySearchCache.add(new ResultBean((AnnotatedObject) iter.next()));
        }
    }

    public void updateSearchCache(ResultBean rb) {
        // Clear previous results.
        mySearchCache.clear();
        mySearchCache.add(rb);
    }

    public Collection lookup(String className, String value) throws SearchException {
        // The result to return.
        Collection results = new ArrayList();

        // The search helper.
        SearchHelperI helper = getSearchHelper();

        // The result to return.
        try {
            results = helper.doLookup(className, value, this);
        }
        catch (IntactException e) {
            String msg = "Failed to find any " + className + " records for " + value;
            throw new SearchException(msg);
        }
        CriteriaBean critera = (CriteriaBean)
                helper.getSearchCritera().iterator().next();
        myLastQuery = critera.getTarget() + "=" + critera.getQuery();
        return results;
    }

    public String getUniqueShortLabel(String shortlabel) throws SearchException {
        return this.getUniqueShortLabel(shortlabel, myEditView.getAc());
    }

    public String getUniqueShortLabel(String shortlabel, String extAc)
            throws SearchException {
        try {
            return GoTools.getUniqueShortLabel(myHelper, myEditView.getEditClass(),
                    myEditView.getAc(), shortlabel, extAc);
        }
        catch (IntactException ie) {
            String msg = "Failed to get a unique short label for " + extAc;
            throw new SearchException(msg);
        }
    }

    public boolean duplicateShortLabel(String label) throws SearchException {
        // The class of the object we are editing at the moment.
        Class clazz = myEditView.getEditClass();

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
            String currentAc = myEditView.getAc();
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
        // The current edit object's short label.
        String editLabel = myEditView.getShortLabel();
        // The class name of the current edit object.
        String className = myEditView.getClass().getName();

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

    public void addToCurrentInteraction(Interaction intact) {
        myCurrentInteractions.add(intact);
    }

    public void removeFromCurrentInteraction(Interaction intact) {
        myCurrentInteractions.remove(intact);
    }

    public Set getCurrentInteractions() {
        return myCurrentInteractions;
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

    public void releaseLock() {
        // Release any locks the user is holding.
        if (myEditView != null) {
            LockManager.getInstance().release(myEditView.getAc());
        }
    }

    // Helper methods.

    /**
     * Called by the constructors to initialize the object.
     * @throws IntactException for errors in accessing the persistent system.
     */
    private void initialize() throws IntactException {
        // Construct the the helper.
        myHelper = new IntactHelper(myDAOSource);

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

        // Create the factories.
        myViewFactory = new EditViewBeanFactory(myHelper);
    }

    /**
     * @return returns an instance of search helper. A new object is created
     * if the current search helper is null.
     */
    private SearchHelperI getSearchHelper() {
        if (mySearchHelper == null) {
            Logger logger = Logger.getLogger(EditorConstants.LOGGER);
            mySearchHelper = new SearchHelper(logger);
        }
        return mySearchHelper;
    }

    private void setSelectedTopic() {
        // The class name of the current edit object.
        String className = myEditView.getEditClass().getName();
        // Strip the package name from the class name.
        mySelectedTopic = className.substring(className.lastIndexOf('.') + 1);
    }

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
