/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.framework;

import uk.ac.ebi.intact.application.hierarchView.business.*;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchView.business.image.DrawGraph;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.business.graph.GraphHelper;
import uk.ac.ebi.intact.application.hierarchView.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.hierarchView.exception.ProteinNotFoundException;
import uk.ac.ebi.intact.application.hierarchView.exception.MultipleResultException;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.Chrono;


import org.apache.struts.action.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import java.rmi.RemoteException;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * Super class for all hierarchView related action classes.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public abstract class IntactBaseAction extends Action {

    public static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /** The global Intact error key. */
    public static final String INTACT_ERROR = "IntactError";

    /** Error container */
    private ActionErrors myErrors = new ActionErrors();


    /** The global Intact message key. */
    public static final String INTACT_MESSAGE = "IntactMessage";

    /** Message container */
    private ActionMessages myMessages = new ActionMessages();

    /**
     * Says if an IntactUser object is currently available in the session.
     *
     * @param session the session to look into.
     * @return true is the IntactUser exists, else false.
     */
    protected boolean intactUserExists (HttpSession session) {
        IntactUserI user = (IntactUserI) session.getAttribute(Constants.USER_KEY);
        return (null != user);
    }

    /**
     * Returns the Intact User instance saved in a session.
     *
     * @param session the session to access the Intact user object.
     * @return an instance of <code>IntactUserImpl</code> stored in
     * <code>session</code>
     */
    protected IntactUserI getIntactUser(HttpSession session)
            throws SessionExpiredException {
        IntactUserI user = (IntactUserI) session.getAttribute(Constants.USER_KEY);

        if (null == user) {
            logger.warn ("Session expired ... forward to error page.");
            throw new SessionExpiredException();
        }

        return user;
    }

    /**
     * Returns the session from given request. No new session is created.
     * @param request the request to get the session from.
     * @return session associated with given request. Null is returned if there
     * is no session associated with <code>request</code>.
     */
    protected HttpSession getSession(HttpServletRequest request)
            throws SessionExpiredException {
        // Don't create a new session.
        HttpSession session = request.getSession(false);

        if (null == session) {
            logger.warn ("Session expired ... forward to error page.");
            throw new SessionExpiredException();
        }

        return session;
    }

    /**
     * Returns a session and create a new one if necessary.
     * @param request the request to get the session from.
     * @return session associated with given request.
     */
    protected HttpSession getNewSession(HttpServletRequest request) {
        return request.getSession(true);
    }



     /////////////////////
    // Error management

    /**
     * Clear error container.
     */
    protected void clearErrors() {
        if (!myErrors.isEmpty()) {
            myErrors.clear();
        }
    }

    /**
     * Adds an error with given key.
     *
     * @param key the error key. This value is looked up in the
     * IntactResources.properties bundle.
     */
    protected void addError(String key) {
        myErrors.add(INTACT_ERROR, new ActionError(key));
    }

    /**
     * Adds an error with given key and value.
     *
     * @param key the error key. This value is looked up in the
     * IntactResources.properties bundle.
     * @param value the value to substitute for the first place holder in the
     * IntactResources.properties bundle.
     */
    protected void addError(String key, String value) {
        myErrors.add(INTACT_ERROR, new ActionError(key, value));
    }

    /**
     * Saves the errors in given request for <struts:errors> tag.
     *
     * @param request the request to save errors.
     */
    protected void saveErrors(HttpServletRequest request) {
        super.saveErrors(request, myErrors);

        // As an error occured, remove the image data stored in the session
        try {
            HttpSession session = this.getSession(request);
            IntactUserI user = getIntactUser(session);
            user.setImageBean (null);
        } catch (SessionExpiredException see) {}
    }

    /**
     * Specify if an the error set is empty.
     *
     * @return boolean false is there are any error registered, else true
     */
    protected boolean isErrorsEmpty () {
        return myErrors.isEmpty();
    }





     //////////////////////
    // Message management

    /**
     * Clear Message container.
     */
    protected void clearMessages() {
        if (!myMessages.isEmpty()) {
            myMessages.clear();
        }
    }

    /**
     * Adds an Message with given key.
     *
     * @param key the Message key. This value is looked up in the
     * Struts.properties bundle.
     */
    protected void addMessage (String key) {
        myMessages.add(INTACT_MESSAGE, new ActionMessage (key));
//        myMessages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage (key));
    }

    /**
     * Adds an Message with given key and value.
     *
     * @param key the Message key. This value is looked up in the
     * Struts.properties bundle.
     * @param value the value to substitute for the first place holder in the
     * Struts.properties bundle.
     */
    protected void addMessage (String key, String value) {
        myMessages.add(INTACT_MESSAGE, new ActionMessage (key, value));
//        myMessages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage (key, value));
    }

    /**
     * Saves the Messages in given request for <struts:messages> tag.
     *
     * @param request the request to save errors.
     */
    protected void saveMessages(HttpServletRequest request) {
        super.saveMessages(request, myMessages);
        logger.info ("################################ MESSAGES SAVED");
    }

    /**
     * Specify if an the Message set is empty.
     *
     * @return boolean false is there are any Message registered, else true
     */
    protected boolean isMessagesEmpty () {
        return myMessages.isEmpty();
    }



     /////////////////////
    // Helper methods.

    /**
     * Create a new IntactUser and store it in the session.<br>
     * A datasource is also initialised inside the IntactUser.
     * @param session the user session where to store the IntactUser
     * @return a brand new IntactUser or null is something goes wrong.
     *
     * <post>
     *      check is the errorsAction is empty, any errors are reported in there.
     * </post>
     */
    protected IntactUser createIntactUser (HttpSession session, HttpServletRequest aRequest) {
        IntactUser user = null;
        ServletContext servletContext = getServlet().getServletContext();

        // Name of the mapping file and data source class.
        String repositoryfile  = servletContext.getInitParameter (Constants.MAPPING_FILE);
        String datasourceClass = servletContext.getInitParameter (Constants.DATA_SOURCE);

        // Create an instance of IntactUser which we'll store in the Session
        try {
            String applicationPath = aRequest.getContextPath();

            user = new IntactUser (repositoryfile, datasourceClass, applicationPath);
            session.setAttribute (Constants.USER_KEY, user);
        }
        catch (DataSourceException de) {
            // Unable to get a data source...can't proceed
            logger.error (de.getMessage(), de);
            String applicationPath = aRequest.getContextPath();
            addError ("error.datasource.notCreated", applicationPath);
            return null;
        }
        catch (IntactException ie) {
            logger.error ("Could not initialize user's settings", ie);
            String applicationPath = aRequest.getContextPath();
            addError ("error.datasource.notCreated", applicationPath);
            return null;
        }

        return user;
    }

    /**
     * Produces image accordingly to the interaction network stored in the user :
     * Any errors are stored in the <i>ActionErrors</i> object. A test need to be done
     * afterward to check if any errors have occured.<br>
     * nothing is done if there is no existing interaction network.
     *
     * @param user where are saved produced data
     */
    protected void produceImage (IntactUserI user) {

        InteractionNetwork in = user.getInteractionNetwork();
        String applicationPath = user.getApplicationPath();

        if (in == null) return;

        // TODO : If depth desacrease we don't have to access IntAct, we have to reduce the current graph.

        String dataTlp  = in.exportTlp();

        try {
            String[] errorMessages;
            errorMessages = in.importDataToImage(dataTlp);

            if ((null != errorMessages) && (errorMessages.length > 0)) {
                for (int i = 0; i<errorMessages.length; i++) {
                    addError("error.webService", errorMessages[i]);
                    logger.error (errorMessages[i]);
                }
                return;
            }
        } catch (RemoteException e) {
            addError ("error.webService", e.getMessage());
            logger.error (e.getMessage(), e);
            return;
        }

        Chrono chrono = new Chrono ();
        chrono.start();

        DrawGraph te = new DrawGraph (in, applicationPath);
        te.draw ();

        chrono.stop();
        String msg = "Time for rendering the interaction network " + chrono;
        logger.info(msg);

        ImageBean ib = te.getImageBean ();

        if (null == ib) {
            addError ("error.ImageBean.build");
            return;
        }

        // store the image data and the graph
        user.setImageBean (ib);
    }


    /**
     * Update the interaction network according to the specified action type:
     * <blockquote>
     *      <li> <code>StrutsConstants.CREATE_INTERACTION_NETWORK</code> : create a new interaction network </li>
     *      <li> <code>StrutsConstants.ADD_INTERACTION_NETWORK</code> : add a new interaction network to the existing's one</li>
     *      <li> <code>StrutsConstants.UPDATE_INTERACTION_NETWORK</code> : rebuild the current interaction network by taking into account any depth change </li>
     * </blockquote>
     * Any errors are stored in the <i>ActionErrors</i> object. A test need to be done
     * afterward to check if any errors have occured.
     *
     * @param user where are saved produced data
     * @param action to perform
     * @see StrutsConstants
     *
     * @throws MultipleResultException in case your query gives multiple results
     */
    public void updateInteractionNetwork (IntactUserI user, int action)
              throws MultipleResultException {

        InteractionNetwork in = null;
        String queryString = user.getQueryString();
        int depth = user.getCurrentDepth();

        StringTokenizer st = new StringTokenizer (queryString, ",");
        ArrayList queries = new ArrayList(30);
        String aQuery;
        while (st.hasMoreElements()) {
            aQuery = st.nextToken().trim(); // remove front and back blank space
            if (aQuery.length() > 0)
               queries.add (aQuery);
        }
        int max = queries.size();

        try {
            GraphHelper gh = new GraphHelper(user);
            String query = null;
            Chrono chrono = new Chrono ();
            chrono.start();

            switch (action) {
                case StrutsConstants.CREATE_INTERACTION_NETWORK:
                    for (int i = 0; i < max; i++) {
                        query = (String) queries.get(i);
                        try {
                            in = gh.addInteractionNetwork (in, query, depth);
                        } catch (ProteinNotFoundException e) {
                            addMessage ("warning.protein.notFound", query);
                            addError   ("error.protein.notFound", query);
                        }
                    } // for

                    // if no network built after processing all sub query, display any errors.
                    // Else any messages.
                    if (in == null) {
                        clearMessages(); // display errors
                    } else {
                        clearErrors();   // display messages
                    }
                    break;


                case StrutsConstants.ADD_INTERACTION_NETWORK:
                    in = user.getInteractionNetwork();
                    for (int i = 0; i < max; i++) {
                        query = (String) queries.get(i);
                        try {
                            in = gh.addInteractionNetwork (in, query, depth);
                        } catch (ProteinNotFoundException e) {
                            addMessage ("warning.protein.notFound", query);
                        }
                    }
                    break;

                case StrutsConstants.UPDATE_INTERACTION_NETWORK:
                    in = user.getInteractionNetwork();
                    try {
                        in = gh.updateInteractionNetwork (in, depth);
                    } catch (ProteinNotFoundException e) {
                        addError ("error.protein.notFound", queryString);
                    }
                    break;

                default:
                    return;
            }

            chrono.stop();
            String msg = null;
            if (in == null) {
                msg = new StringBuffer().append("No interaction network retreived, took ").append (chrono).toString();
            } else {
                msg = new StringBuffer().append("Time for retreiving the interaction network ( ").append (
                        in.sizeNodes()).append(" proteins, ").append(in.sizeEdges()).append(" edges) :").append(
                        chrono).toString();
            }
            logger.info(msg);

            if (in == null) {
                // no protein found
                return;
            }


        }  catch (SearchException e) {
            addError ("error.search.process", e.getMessage());
            return;

        } catch (IntactException e) {
            addError ("error.interactionNetwork.notCreated", e.getMessage());
            return;

        } catch (MultipleResultException mre) {
             throw mre;
        }

        if (0 == in.sizeNodes()) {
            addError ("error.interactionNetwork.noProteinFound");
            return;
        }

        user.setInteractionNetwork (in);
    }

}
