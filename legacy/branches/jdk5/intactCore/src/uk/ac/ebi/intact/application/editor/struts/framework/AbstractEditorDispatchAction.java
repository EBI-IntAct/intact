/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.LikeCriteria;
import org.apache.ojb.broker.query.Query;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.actions.LookupDispatchAction;
import uk.ac.ebi.intact.application.commons.search.ResultWrapper;
import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.commons.search.SearchHelperI;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.ForwardConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.OJBQueryFactory;
import uk.ac.ebi.intact.application.editor.struts.view.wrappers.ResultRowData;
import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * The super class for all the dispatch actions.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $id$
 */
public abstract class AbstractEditorDispatchAction extends LookupDispatchAction
        implements ForwardConstants {

    /**
     * The logger for Editor. Allow access from the subclasses.
     */
    protected static final Logger LOGGER = Logger.getLogger(EditorConstants.LOGGER);

    /**
     * Returns the only instance of Intact Service instance.
     * @return only instance of the <code>EditorService</code> class.
     */
    protected EditorService getService() {
        EditorService service = (EditorService)
                getApplicationObject(EditorConstants.EDITOR_SERVICE);
        return service;
    }

    /**
     * Returns the session from given request. No new session is created.
     * @param request the request to get the session from.
     * @return session associated with given request.
     * @exception uk.ac.ebi.intact.application.editor.exception.SessionExpiredException for an expired session.
     *
     * <pre>
     * post: return <> Undefined
     * </pre>
     */
    protected HttpSession getSession(HttpServletRequest request)
            throws SessionExpiredException {
        // Don't create a new session.
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new SessionExpiredException();
        }
        return session;
    }

    /**
     * Returns the Intact User instance saved in a session for given
     * Http request.
     *
     * @param request the Http request to access the Intact user object.
     * @return an instance of <code>EditUser</code> stored in a session.
     * No new session is created.
     * @exception uk.ac.ebi.intact.application.editor.exception.SessionExpiredException for an expired session.
     *
     * <pre>
     * post: return <> Undefined
     * </pre>
     */
    protected EditUserI getIntactUser(HttpServletRequest request)
            throws SessionExpiredException {
        EditUserI user = (EditUserI)
                getSession(request).getAttribute(EditorConstants.INTACT_USER);
        if (user == null) {
            throw new SessionExpiredException();
        }
        return user;
    }

    /**
     * @return the lock manager stored in the application context.
     */
    protected LockManager getLockManager() {
        return (LockManager) getApplicationObject(EditorConstants.LOCK_MGR);
    }

    /**
     * Returns true if errors in stored in the request
     * @param request Http request to search errors for.
     * @return true if strut's error is found in <code>request</code> and
     * it is not null. For all instances, false is returned.
     */
    protected boolean hasErrors(HttpServletRequest request) {
        ActionErrors errors =
                (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
        if (errors != null) {
            // Empty menas no errors.
            return !errors.isEmpty();
        }
        // No errors stored in the request.
        return false;
    }

    /**
     * Sets the anchor in the form if an anchor exists.
     *
     * @param request the HTTP request to get anchor.
     * @param form the form to set the anchor and also to extract the dispath
     * event.
     *
     * @see EditorService#getAnchor(java.util.Map, HttpServletRequest, String)
     */
    protected void setAnchor(HttpServletRequest request, EditorFormI form) {
        // The map containing anchors.
        Map map = (Map) getApplicationObject(EditorConstants.ANCHOR_MAP);

        // Any anchors to set?
        String anchor = getService().getAnchor(map, request, form.getDispatch());
        // Set the anchor only if it is set.
        if (anchor != null) {
            form.setAnchor(anchor);
        }
    }

    /**
     * Tries to acquire a lock for given id and owner.
     * @param ac the id or the accession number to lock.
     * @param owner the owner of the lock.
     * @return null if there are no errors in acquiring the lock or else
     * non null value is returned to indicate errors.
     */
    protected ActionErrors acquire(String ac, String owner) {
        return acquire(ac, owner, ActionErrors.GLOBAL_ERROR);
    }

    /**
     * Tries to acquire a lock for given id and owner.
     * @param ac the id or the accession number to lock.
     * @param owner the owner of the lock.
     * @param errGroup the error group for action errors (JSPs can display errors
     * under this name)
     * @return null if there are no errors in acquiring the lock or else
     * non null value is returned to indicate errors.
     */
    protected ActionErrors acquire(String ac, String owner, String errGroup) {
        // Try to acuire the lock.
        if (!getLockManager().acquire(ac, owner)) {
            ActionErrors errors = new ActionErrors();
            // The owner of the lock (not the current user).
            errors.add(errGroup, new ActionError("error.lock", ac,
                    getLockManager().getOwner(ac)));
            return errors;
        }
        return null;
    }

    /**
     * Performs the search using given query array.
     * @param queries an array of queries. The first query is to get a count and
     * the secodn query for the actual search.
     * @param max the max allowed records
     * @param request the request to store the ActionError
     * @return a list of search results or an empty list for any errors, too large
     * result set or search produces no output.
     */
    protected List search(Query[] queries, int max, HttpServletRequest request) {
        return search(queries, max, request, ActionErrors.GLOBAL_ERROR);
    }

    /**
     * Performs the search using given query array.
     * @param queries an array of queries. The first query is to get a count and
     * the secodn query for the actual search.
     * @param max the max allowed records
     * @param request the request to store the ActionError
     * @param errGroup the error group for action errors (JSPs can display errors
     * under this name)
     * @return a list of search results or an empty list for any errors, too large
     * result set or search produces no output.
     */
    protected List search(Query[] queries, int max, HttpServletRequest request,
                          String errGroup) {
        // The search helper to do the searching.
        SearchHelperI searchHelper = new SearchHelper(Logger.getLogger(
                EditorConstants.LOGGER));

        // The result wrapper returned from the search.
        ResultWrapper rw = null;
        try {
            rw = searchHelper.searchByQuery(queries, max);
        }
        catch (IntactException ie) {
            // This can only happen when problems with creating an internal helper
            // This error is already logged from the User class.
            ActionErrors errors = new ActionErrors();
            errors.add(errGroup, new ActionError("error.intact"));
            saveErrors(request, errors);
            return Collections.EMPTY_LIST;
        }

        // Too large result set?
        if (rw.isTooLarge()) {
            ActionErrors errors = new ActionErrors();
            errors.add(errGroup, new ActionError("error.search.large",
                            Integer.toString(rw.getPossibleResultSize())));
            saveErrors(request, errors);
            return Collections.EMPTY_LIST;
        }

        // Nothing found?
        if (rw.isEmpty()) {
            // The topic for errors.
            String topic = EditorService.getTopic(queries[1].getSearchClass());

            // Extract the search parameter.
            String searchParam = null;

            for (Enumeration enumer = queries[1].getCriteria().getElements();
                 enumer.hasMoreElements();) {
                Object nextCritera = enumer.nextElement();
                if (LikeCriteria.class.isAssignableFrom(nextCritera.getClass())) {
                    LikeCriteria crit = (LikeCriteria) nextCritera;
                    searchParam = (String) crit.getValue();
                    break;
                }
            }
            // No matches found - forward to a suitable page
            ActionErrors errors = new ActionErrors();
            errors.add(errGroup, new ActionError("error.search.nomatch",
                    searchParam, topic));
            saveErrors(request, errors);
            return Collections.EMPTY_LIST;
        }
        return makeRowData(rw.getResult().iterator(), queries[1].getSearchClass());
    }

    /**
     * Return an array of general search queries.
     * @param searchClass the search class
     * @param searchString the search value
     * @return an array of queries. The first element of the array contains
     * the query to get the count and the second part contains the search query.
     */
    protected Query[] getSearchQueries(Class searchClass, String searchString) {
        // The query factory to get a query.
        OJBQueryFactory qf = OJBQueryFactory.getInstance();

        // The array to store queries.
        Query[] queries = new Query[2];

        // The query to get a search result size.
        queries[0] = qf.getSearchCountQuery(searchClass, searchString);

        // The search query
        queries[1] = qf.getSearchQuery(searchClass, searchString);

        return queries;
    }

    /**
     * Returns an array of row data
     * @param iter the iterator to loop.
     * @param searchClass the search class to construct an instance of RowData
     * @return a list consists of RowData objects.
     */
    protected List makeRowData(Iterator iter, Class searchClass) {
        // The results to return.
        List results = new ArrayList();

        // Convert to result row data.
        while (iter.hasNext()) {
            results.add(new ResultRowData((Object[]) iter.next(), searchClass));
        }
        return results;
    }

    /**
     * A convenient method to retrieve an application object from a session.
     * @param attrName the attribute name.
     * @return an application object stored in a session under <tt>attrName</tt>.
     */
    private Object getApplicationObject(String attrName) {
        return super.servlet.getServletContext().getAttribute(attrName);
    }
}
