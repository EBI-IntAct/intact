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

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
     * The pattern to parse the given short label to get the next available
     * short label.
     * pattern: any number of characters followed by -x.
     */
    private static final Pattern ourNextSLPattern = Pattern.compile("^(.+)?\\-(x)$");

    /**
     * The pattern to parse an existing cloned short label.
     * pattern: any number of characters followed by -, and digits.
     */
    private static final Pattern ourClonedSLPattern = Pattern.compile("^(.+)?\\-(\\d+)$");

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

    // ------------------------------------------------------------------------

    // Inner class to sort search result.
    private static class SearchResultComparator implements Comparator {

        private static SearchResultComparator ourInstance =
                new SearchResultComparator();

        // Compare on short labels.
        public int compare(Object obj1, Object obj2) {
            AnnotatedObject annobj1 = (AnnotatedObject) obj1;
            AnnotatedObject annobj2 = (AnnotatedObject) obj2;
            return annobj1.getShortLabel().compareTo(annobj2.getShortLabel());
        }
    }

    // ------------------------------------------------------------------------

    // Constructors.

    /**
     * Constructs an instance of this class with given data source class,
     * the name of the user and the password.
     *
     * @param dsClass the class name of the Data Source.
     * @param user the user
     * @param password the password of <code>user</code>.
     *
     * @exception uk.ac.ebi.intact.persistence.DataSourceException for error in getting the data source; this
     *  could be due to the errors in repository files.
     * @exception IntactException for errors in creating IntactHelper; possibly
     * due to an invalid user.
     */
    public EditUser(String dsClass, String user, String password)
            throws DataSourceException, IntactException {
        myDAOSource = DAOFactory.getDAOSource(dsClass);

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
     * @param dsClass the class name of the Data Source.
     * @throws DataSourceException for error in getting the data source; this
     * could be due to the errors in repository files.
     * @throws IntactException for errors in creating IntactHelper.
     *
     * @see uk.ac.ebi.intact.application.editor.test.SessionSerializationTest
     */
    public EditUser(String dsClass) throws IntactException,
            DataSourceException {
        this(dsClass, null, null);
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
        Logger.getLogger(EditorConstants.LOGGER).info("User is about to unbound");
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

    public void setView(AbstractEditViewBean view) {
        myEditView = view;
    }

    public void setView(Object obj) {
        // Start editing the object.
        startEditing();
        // Check for annotated object.
        if (AnnotatedObject.class.isAssignableFrom(obj.getClass())) {
            // View based on the class for given edit object.
            myEditView = myViewFactory.factory(IntactHelper.getRealClassName(obj));
        }
        else {
            // The new view based on the class type.
            myEditView = myViewFactory.factory((Class) obj);
        }
        // Resets the view.
        myEditView.reset(obj);
    }

    public void setClonedView(AnnotatedObject obj) {
        startEditing();
        myEditView = myViewFactory.factory(IntactHelper.getRealClassName(obj));
        myEditView.resetClonedObject(obj, this);
    }

    public String getSelectedTopic() {
        return mySelectedTopic;
    }

    public void setSelectedTopic(String topic) {
        mySelectedTopic = topic;
    }

    public Institution getInstitution() {
        return myInstitution;
    }

    public boolean isEditing() {
        return myEditState;
    }

    public void startEditing() {
        myEditState = true;
    }

    public void begin() throws IntactException {
        myHelper.startTransaction(BusinessConstants.OBJECT_TX);
    }

    public void commit() throws IntactException {
        myHelper.finishTransaction();
        endEditing();
        // Clear the cache; this will force the OJB to read from the database.
        myHelper.removeFromCache(myEditView.getAnnotatedObject());
    }

    public void rollback() throws IntactException {
        myHelper.undoTransaction();
        endEditing();
    }

    public void create(Object object) throws IntactException {
        myHelper.create(object);
    }

    public void update(Object object) throws IntactException {
        myHelper.update(object);
    }

    public void forceUpdate(Object object) throws IntactException {
        myHelper.forceUpdate(object);
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
        // Clear the view.
        myEditView.clear();
        AnnotatedObject annobj = myEditView.getAnnotatedObject();
        if (isPersistent(annobj)) {
            myHelper.delete(annobj);

            // Clear annotation and xref collections
            myHelper.deleteAllElements(annobj.getAnnotations());
            annobj.getAnnotations().clear();
            // Don't want xrefs; tied to an annotated object. Delete them explicitly
            myHelper.deleteAllElements(annobj.getXrefs());
            annobj.getXrefs().clear();
        }
    }

    public void cancelEdit() {
        endEditing();
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

    public Exception getProteinParseException() {
        // Map of exceptions.
        Map map = myProteinFactory.getParsingExceptions();
        // Only interested in the first entry as the parsing is limited to a
        // single entry. Guard against empty exceptions
        if (!map.values().isEmpty()) {
            return (Exception) map.values().iterator().next();
        }
        return null;
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
            mySearchCache.add(iter.next());
        }
    }

    public void updateSearchCache(AnnotatedObject annotobj) {
        // Clear previous results.
        mySearchCache.clear();
        mySearchCache.add(annotobj);
    }

    public Collection lookup(String className, String value) throws SearchException {
        // The result to return.
        Collection results = new ArrayList();

        // The search helper.
        SearchHelperI helper = getSearchHelper();

        // The result to return.
        try {
            results = helper.doLookupSimple(className, value, this);
        }
        catch (IntactException e) {
            String msg = "Failed to find any " + className + " records for " + value;
            throw new SearchException(msg);
        }
        // Search result in an array to pass to the sort method.
        AnnotatedObject[] items = (AnnotatedObject[]) results.toArray(
                new AnnotatedObject[0]);
        // Sort using the compartor.
        Arrays.sort(items, SearchResultComparator.ourInstance);

        // Cache the search query.
        CriteriaBean critera = (CriteriaBean)
                helper.getSearchCritera().iterator().next();
        myLastQuery = critera.getTarget() + "=" + critera.getQuery();

        // Convert back to the a list.
        return Arrays.asList(items);
    }

//    public String getUniqueShortLabel(String shortlabel) throws SearchException {
//        // Need to change to lower case as uppercases are not allowed for a name.
//        String ac = myEditView.getAc();
//        // If ac is null, just a template as the suggested value.
//        return this.getUniqueShortLabel(shortlabel, ac == null ? "xxx-xx"
//                : ac.toLowerCase());
//    }
//
//    public String getUniqueShortLabel(String shortlabel, String extAc)
//            throws SearchException {
//        try {
//            return GoTools.getUniqueShortLabel(myHelper, myEditView.getEditClass(),
//                    myEditView.getAc(), shortlabel, extAc);
//        }
//        catch (IntactException ie) {
//            String msg = "Failed to get a unique short label for " + extAc;
//            throw new SearchException(msg);
//        }
//    }

    public boolean shortLabelExists(String label) throws SearchException {
        return doesShortLabelExist(myEditView.getEditClass(), label, myEditView.getAc());
    }

    public boolean shortLabelExists(Class clazz, String label, String ac)
            throws SearchException {
        return doesShortLabelExist(clazz, label, ac);
    }

    /**
     * Returns the next available short label from the persistent system.
     * @param clazz the calss or the type for the search.
     * @param label the starting short label; this must contain the suffix '-x'
     * @return the next available short label from the persistent system. Basically,
     * this takes the form of <code>label</code> with -x substituted with the last
     * persistent number. For example, it could be abc-2 provided that <code>label</code>
     * is abc-x and abc-1 is the last persistent suffix. This could be as same
     * as <code>label</code> if there is an error in accessing the database to
     * access other similar objects. Null is returned if <code>label</code> has
     * invalid format.
     */
    public String getNextAvailableShortLabel(Class clazz, String label) {
        Matcher matcher = ourNextSLPattern.matcher(label);
        if (!matcher.matches()) {
            return null;
        }
        // Try the longest name first.
        String prefix = matcher.group(1) + "-";
        Collection results = null;
        try {
            results = search1(clazz.getName(), "shortLabel", prefix + "*");
        }
        catch (SearchException se) {
            return label;
        }
        if (results.isEmpty()) {
            // No matches found for the longes match. The first clone entry.
            return prefix + "1";
        }
        // Found at least one entry. Need to find out the largest number.
        int number = 1;
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            String shortLabel = ((AnnotatedObject) iter.next()).getShortLabel();
            // Need to split the short label to extract the number.
            Matcher matcher1 = ourClonedSLPattern.matcher(shortLabel);
            if (matcher1.matches()) {
                // Only consider a short label matching the prefix.
                if (matcher1.group(1).equals(matcher.group(1))) {
                    // They should match; the search returns only the matching one.
                    int digit = Integer.parseInt(matcher1.group(2));
                    if (digit >= number) {
                        number = digit + 1;
                    }
                }
            }
        }
        return prefix + number;
    }

    public void fillSearchResult(DynaBean dynaForm) {
        dynaForm.set("items", mySearchCache);
    }

    public List getSearchResult() {
        return (List) mySearchCache;
    }

    public void logoff() throws IntactException {
        mySessionEndTime = Calendar.getInstance().getTime();
        Logger.getLogger(EditorConstants.LOGGER).info("User is logging off at: "
                + mySessionEndTime);
        // Release all the locks held by this user.
        LockManager.getInstance().releaseAllLocks(getUserName());
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
        if (myEditView.getAc() != null) {
            LockManager.getInstance().release(myEditView.getAc());
        }
    }
    
	public BioSource getBioSourceByTaxId(String taxId) throws SearchException {
		try {
			return myHelper.getBioSourceByTaxId(taxId);
		}
		catch (IntactException ie) {
			throw new SearchException("Failed to get a BioSource for " + taxId);
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

        // Initialize the institution; this ensures that a connection is made
        // as a valid user.
        myInstitution = myHelper.getInstitution();

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

    /**
     * Finishes the editing session.
     */
    private void endEditing() {
        myEditState = false;
    }

    /**
     * Returns true if given label exists in the persistent system.
     * @param clazz the Class to search for (ie., scope)
     * @param label the label to search for.
     * @param ac the AC to exclude match for the retrieved record. This criteia
     * is taken into consideration only when a single record was found. If the
     * retieved object's AC matches this value then we conclude that we have
     * retrieved the the same entry.
     * @return true if <code>label</code> exists for <code>clazz</code>. False
     * is returned for all other instances.
     * @throws SearchException
     */
    private boolean doesShortLabelExist(Class clazz, String label, String ac)
            throws SearchException {
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
            String resultAc = ((AnnotatedObject) results.iterator().next()).getAc();
            if (resultAc.equals(ac)) {
                // We have retrieved the same record from the DB.
                return false;
            }
        }
        // There is another record exists with the same short label.
        return true;
    }
}
