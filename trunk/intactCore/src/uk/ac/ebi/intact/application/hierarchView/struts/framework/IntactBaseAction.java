/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.framework;

import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUser;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserIF;
import uk.ac.ebi.intact.application.hierarchView.business.image.GraphToSVG;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.business.graph.GraphHelper;
import uk.ac.ebi.intact.application.hierarchView.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.hierarchView.exception.ProteinNotFoundException;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.business.IntactException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Super class for all hierarchView related action classes.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
public abstract class IntactBaseAction extends Action {

    public static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /** The global Intact error key. */
    public static final String INTACT_ERROR = "IntactError";

    /** Error container */
    private ActionErrors myErrors = new ActionErrors();

    /**
     * Returns the Intact User instance saved in a session.
     *
     * @param session the session to access the Intact user object.
     * @return an instance of <code>IntactUserImpl</code> stored in
     * <code>session</code>
     */
    protected IntactUserIF getIntactUser(HttpSession session)
            throws SessionExpiredException {
        IntactUserIF user = (IntactUserIF) session.getAttribute(Constants.USER_KEY);

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
    protected void saveErrors(HttpServletRequest request)
            throws SessionExpiredException {
        super.saveErrors(request, myErrors);

        // As an error occured, remove the image data stored in the session
        HttpSession session = this.getSession(request);
        IntactUserIF user = getIntactUser(session);
        user.setImageBean (null);
    }

    /**
     * Specify if an the error set is empty.
     *
     * @return boolean false is there are any error registered, else true
     */
    protected boolean isErrorsEmpty () {
        return myErrors.isEmpty();
    }

    // Helper methods.

    /**
     * A convenient method to retrieve an application object from a session.
     * @param attrName the attribute name.
     * @return an application object stored in a session under <tt>attrName</tt>.
     */
//    private Object getApplicationObject(String attrName) {
//        return super.servlet.getServletContext().getAttribute(attrName);
//    }




    /**
     * Produces and updates the user session with :
     * <blockquote>
     *      the InteractionNetwork corresponding to the given AC and depth. <br>
     *      the image data corresponding to the produced InteractionNetwork
     * </blockquote>
     * Any errors are stored in the <i>ActionErrors</i> object. A test need to be done
     * afterward to check if any errors have occured.
     *
     * @param AC the protein identifier on which is centered the interaction network.
     * @param depth the search depth around the cetered protein
     * @param user where are saved produced data
     */
    public void produceInteractionNetworkImage (String AC, String depth, IntactUserIF user) {

        int depthInt = 0;
        InteractionNetwork in = null;

        try {
            GraphHelper gh = new GraphHelper(user);
            depthInt = Integer.parseInt(depth);
            in = gh.getInteractionNetwork(AC, depthInt);
        } catch (ProteinNotFoundException e) {
            addError ("error.protein.notFound", AC);
            return;

        } catch (SearchException e) {
            addError ("error.search.process", e.getMessage());
            return;

        } catch (IntactException e) {
            addError ("error.interactionNetwork.notCreated", e.getMessage());
            return;
        }

        if (0 == in.sizeNodes()) {
            addError ("error.interactionNetwork.noProteinFound");
            return;
        }

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
        } catch (Exception e) {
            addError ("error.webService", e.getMessage());
            logger.error (e.getMessage(), e);
            return;
        }

        GraphToSVG te = new GraphToSVG (in);
        te.draw ();
        ImageBean ib = te.getImageBean ();

        if (null == ib) {
            addError ("error.ImageBean.build");
            return;
        }

        // store the image data and the graph
        user.setImageBean (ib);
        user.setInteractionNetwork (in);
    } // produceInteractionNetworkImage


} // IntactBaseAction
