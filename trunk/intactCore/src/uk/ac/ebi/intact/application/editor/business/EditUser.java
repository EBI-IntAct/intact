/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.business;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.commons.search.CriteriaBean;
import uk.ac.ebi.intact.application.commons.search.ResultWrapper;
import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.commons.search.SearchHelperI;
import uk.ac.ebi.intact.application.editor.event.EventListener;
import uk.ac.ebi.intact.application.editor.event.LogoutEvent;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditViewBeanFactory;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.business.BusinessConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.util.GoServerProxy;
import uk.ac.ebi.intact.util.NewtServerProxy;
import uk.ac.ebi.intact.util.UpdateProteins;
import uk.ac.ebi.intact.util.UpdateProteinsI;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // -------------------------------------------------------------------------

    /**
     * The formatter for a short label.
     */
    public static class ShortLabelFormatter {

        /**
         * The pattern to split the short label.
         */
        private static final Pattern ourSLSplitPattern = Pattern.compile("-");

        /**
         * An array of strings after applying the split pattern.
         */
        private String[] myLabelComps;

        /**
         * Constructs an instance with given short label.
         * @param shortLabel the short label to do the formatting.
         */
        public ShortLabelFormatter(String shortLabel) {
            myLabelComps = ourSLSplitPattern.split(shortLabel);
        }

        /**
         * @return true if this short label has a branch (i.e., ends with a digit).
         */
        public boolean hasBranch() {
            return myLabelComps[getBranchPos()].matches("\\d+");
        }

        /**
         * @return true if this short label ends with x (e.g., abc-x).
         */
        public boolean hasClonedSuffix() {
            return myLabelComps[myLabelComps.length - 1].equals("x");
        }

        /**
         * @return true only if the short label hasn't got a branch or cloned suffix.
         */
        public boolean isRootOnly() {
            return !hasBranch() && !hasClonedSuffix();
        }

        /**
         * @return the branch number as an int or -1 if there is no branch.
         */
        public int getBranchNumber() {
            if (!hasBranch()) {
                // No branch.
                return -1;
            }
            return Integer.parseInt(getBranch());
        }

        /**
         * @return the root element without the branch (if it exists).
         */
        public String getRoot() {
            // Initialize with the first item.
            StringBuffer sb = new StringBuffer(myLabelComps[0]);

            // Decide where to stop.
            int stop = myLabelComps.length;
            if (hasClonedSuffix()) {
                --stop;
            }
            if (hasBranch()) {
                --stop;
            }
            // Loop from the second item.
            for (int i = 1; i < stop; i++) {
                sb.append("-" + myLabelComps[i]);
            }
            return sb.toString();
        }

        /**
         * @return the branch component ot null if none exists.
         */
        public String getBranch() {
            if (!hasBranch()) {
                return null;
            }
            return myLabelComps[getBranchPos()];
        }

        private int getBranchPos() {
            int pos = myLabelComps.length - 1;
            if (hasClonedSuffix()) {
                --pos;
            }
            return pos;
        }
    }

    // -- End of Inner class --------------------------------------------------

    /**
     * The pattern to parse an existing cloned short label.
     * pattern: any number of characters followed by -, and digits.
     */
    private static final Pattern ourClonedSLPattern = Pattern.compile("^(.+)?\\-(\\d+)$");

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
     * The name of the current user.
     */
    private String myUserName;

    /**
     * The password for the current user.
     */
    private String myPassword;

    /**
     * The name of the current database.
     */
    private String myDatabaseName;

    /**
     * Stores the current query result.
     */
    private CriteriaBean mySearchCriteria;

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

    // Constructors.

    /**
     * The default constructor. This constructor uses the default values for
     * user and password (mainly for testing).
     */
    public EditUser() {
    }

    /**
     * Constructs an instance of this class with given user and the password.
     *
     * @param user the user
     * @param password the password of <code>user</code>.
     * @exception IntactException for errors in creating IntactHelper; possibly
     * due to an invalid user.
     */
    public EditUser(String user, String password) throws IntactException {
        myUserName = user;
        myPassword = password;
        IntactHelper helper = new IntactHelper();
        try {
            // Initialize the object.
            initialize(helper);
        }
        finally {
            helper.closeStore();
            myProteinFactory.setIntactHelper(null);
        }
    }

    // Methods to handle special serialization issues.

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            initialize(helper);
        }
        catch (IntactException ie) {
            throw new IOException(ie.getMessage());
        }
        finally {
            if (helper != null) {
                try {
                    helper.closeStore();
                }
                catch (IntactException e) {}
            }
            myProteinFactory.setIntactHelper(null);
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
        getLogger().info("User is about to unbound");
        ServletContext ctx = event.getSession().getServletContext();
        LockManager lm = (LockManager) ctx.getAttribute(EditorConstants.LOCK_MGR);

        // Notify the event listener.
        EventListener listener = (EventListener) ctx.getAttribute(
                EditorConstants.EVENT_LISTENER);
        listener.notifyObservers(new LogoutEvent(getUserName()));

        // Not an error, just a logging statemet to see values are unbound or not
        getLogger().error("User unbound: " + getUserName());

        // Release all the locks held by this user.
        lm.releaseAllLocks(getUserName());
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract.
     * @param obj the object to compare.
     * @return true only if <code>obj</code> is an instance of this class
     * and the user name is same. For all other instances, false is returned.
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
        // User name must equal
        return other.myUserName.equals(other.myUserName);
    }

    // Implementation of IntactUserI interface.

    public String getUserName() {
        return myUserName;
    }

    public String getDatabaseName() {
        return myDatabaseName;
    }

    public Collection search(String objectType, String searchParam,
                             String searchValue) throws IntactException {
        throw new IntactException("Not implemented");
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

    public boolean isEditing() {
        return myEditState;
    }

    public IntactHelper getIntactHelper() throws IntactException {
        // Construct the the helper.
        return new IntactHelper(myUserName, myPassword);
    }
    
    public void startEditing() {
        myEditState = true;
    }

    public void startTransaction(IntactHelper helper) throws IntactException {
        helper.startTransaction(BusinessConstants.OBJECT_TX);
    }

    public void commit(IntactHelper helper) throws IntactException {
        helper.finishTransaction();
        endEditing();
        // Clear the cache; this will force the OJB to read from the database.
        helper.removeFromCache(myEditView.getAnnotatedObject());
    }

    public void rollback(IntactHelper helper) throws IntactException {
        helper.undoTransaction();
        endEditing();
    }

    public void delete(IntactHelper helper) throws IntactException {
        // Clear the view.
        myEditView.clear();
        AnnotatedObject annobj = myEditView.getAnnotatedObject();
        if (!helper.isPersistent(annobj)) {
            return;
        }
        helper.delete(annobj);

        // Clear annotation and xref collections
        helper.deleteAllElements(annobj.getAnnotations());
        annobj.getAnnotations().clear();
        // Don't want xrefs; tied to an annotated object. Delete them explicitly
        helper.deleteAllElements(annobj.getXrefs());
        annobj.getXrefs().clear();
    }

    public void cancelEdit() {
        endEditing();
        myEditView.clearTransactions();
    }

    public ResultWrapper getSPTRProteins(String pid, int max) throws IntactException {
        // The helper to insert proteins.
        IntactHelper helper = getIntactHelper();
        // The result wrapper to return.
        ResultWrapper rw = null;

        // Set the helper as it has already been closed.
        myProteinFactory.setIntactHelper(helper);
        Collection prots;
        try {
            prots = myProteinFactory.insertSPTrProteins(pid);
        }
        finally {
            helper.closeStore();
            myProteinFactory.setIntactHelper(null);
        }
        if (prots.size() > max) {
            // Exceeds the maximum size.
            rw = new ResultWrapper(prots.size(), max);
        }
        else if (prots.isEmpty()) {
            rw = new ResultWrapper(0, max);
        }
        else {
            // Within allowed range
            rw = new ResultWrapper(prots, max);
        }
        return rw;
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

    public CriteriaBean getSearchCriteria() {
        return mySearchCriteria;
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

    public ResultWrapper lookup(Class clazz, String param, String value, int max)
            throws IntactException {
        // The search helper.
        SearchHelperI helper = getSearchHelper();

        // The result to return.
        try {
            return helper.searchByQuery(clazz, param, value, max);
        }
        catch (IntactException e) {
            // This is an internal error. Log it.
            getLogger().error("", e);
            // Rethrow it again for the presentaion layer to handle it
            throw e;
        }
    }

    public ResultWrapper lookup(String className, String value, int max)
            throws IntactException {
        // The search helper.
        SearchHelperI helper = getSearchHelper();

        // The result to return.
        try {
            Class clazz = Class.forName(className);
            ResultWrapper rw = helper.doLookupSimple(clazz, value, max);
            // Cache the search query.
            mySearchCriteria = (CriteriaBean) helper.getSearchCritera().iterator().next();
            return rw;
        }
        catch (ClassNotFoundException e) {
            // This is an internal error. Log it.
            getLogger().error("", e);
            throw new IntactException(e.getMessage());
        }
        catch (IntactException e) {
            // This is an internal error. Log it.
            getLogger().error("", e);
            // Rethrow it again for the presentaion layer to handle it
            throw e;
        }
    }

    public boolean shortLabelExists(String label) throws IntactException {
        return doesShortLabelExist(myEditView.getEditClass(), label, myEditView.getAc());
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
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            return doGetNextAvailableShortLabel(helper, clazz, label);
        }
        catch (IntactException ie) {
            // Error in searching, just return the original name for user to
            // decide.
        }
        finally {
            if (helper != null) {
                try {
                    helper.closeStore();
                }
                catch (IntactException ie) {}
            }
        }
        return label;
    }

    public List getSearchResult() {
        return (List) mySearchCache;
    }

    public NewtServerProxy getNewtProxy() {
        if (myNewtServer == null) {
            try {
                myNewtServer = new NewtServerProxy();
            }
            catch (MalformedURLException murle) {
                // This should never happen because default constructor has a
                // valid URL.
                getLogger().info(murle);
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

    // Helper methods.

    /**
     * Called by the constructors to initialize the object.
     * @param helper the Intact helper to access the persistent system.
     * @throws IntactException for errors in accessing the persistent system.
     */
    private void initialize(IntactHelper helper) throws IntactException {
        myDatabaseName = helper.getDbName();

        // Initialize the Protein factory.
        try {
            myProteinFactory = new UpdateProteins(helper);
        }
        catch (UpdateProteinsI.UpdateException e) {
            throw new IntactException("Unable to create the Protein factory");
        }
        // Create the factories.
        myViewFactory = new EditViewBeanFactory();
    }

    /**
     * @return returns an instance of search helper. A new object is created
     * if the current search helper is null.
     */
    private SearchHelperI getSearchHelper() {
        if (mySearchHelper == null) {
            mySearchHelper = new SearchHelper(getLogger());
        }
        return mySearchHelper;
    }

    private Logger getLogger() {
        return Logger.getLogger(EditorConstants.LOGGER);
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
     * @throws IntactException unable to create an Intact helper or error in
     * searching the persistent system.
     */
    private boolean doesShortLabelExist(Class clazz, String label, String ac)
            throws IntactException {
        IntactHelper helper = new IntactHelper();
        // Holds the result from the search.
        Collection results;
        try {
           results = helper.search(clazz.getName(), "shortLabel", label);
        }
        finally {
            helper.closeStore();
        }
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

    private String doGetNextAvailableShortLabel(IntactHelper helper, Class clazz,
                                                String label) throws IntactException {
        // The formatter to analyse the short label.
        ShortLabelFormatter formatter = new ShortLabelFormatter(label);

        // Holds the result from the search.
        Collection results = null;

        // The prefix for the proposed short label;
        String prefix = null;

        // Try to guess the next new label.
        String nextLabel;

        if (formatter.isRootOnly()) {
            // case: abc
            if (helper.getObjectByLabel(clazz, label) == null) {
                // Label is not found in the DB.
                return label;
            }
            // Label already exists.
            prefix = label;
            nextLabel = prefix + "-1";
        }
        else {
            // case: abc-x or abc-1
            prefix = formatter.getRoot();
            // Trying to guess the new label by increment the branch number by
            // one. This avoids loading all the cloned names with prefix-*.
            nextLabel = formatter.hasBranch()
                    ? prefix + "-" + (formatter.getBranchNumber() + 1)
                    : prefix + "-1";
        }
        if (helper.getObjectByLabel(clazz, nextLabel) == null) {
            return nextLabel;
        }
        // AT this point: no success with incrementing the branch number.

        // Get all the short labels with the prefix.
        results = helper.search(clazz.getName(), "shortLabel", prefix + "-*");
        if (results.isEmpty()) {
            // No matches found for the longest match. The first clone entry.
            return prefix + "-1";
        }
        // Found at least one entry. Need to find out the largest number.
        int number = 2;
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            String shortLabel = ((AnnotatedObject) iter.next()).getShortLabel();
            // Need to split the short label to extract the number.
            Matcher matcher1 = ourClonedSLPattern.matcher(shortLabel);
            if (matcher1.matches()) {
                // Only consider a short label matching the prefix.
                if (matcher1.group(1).equals(prefix)) {
                    // They should match; the search returns only the matching one.
                    int digit = Integer.parseInt(matcher1.group(2));
                    if (digit >= number) {
                        number = digit + 1;
                    }
                }
            }
        }
        return prefix + "-" + number;
    }
}
