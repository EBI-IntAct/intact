/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

package uk.ac.ebi.intact.application.mine.struts.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import uk.ac.ebi.intact.application.mine.business.Constants;
import uk.ac.ebi.intact.application.mine.business.IntactUserI;
import uk.ac.ebi.intact.application.mine.business.graph.MineHelper;
import uk.ac.ebi.intact.application.mine.business.graph.model.MineData;
import uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey;
import uk.ac.ebi.intact.application.mine.struts.view.ErrorForm;

/**
 * This class provides the action to start the Dijkstra algorithm to find the
 * shortest path between all search nodes.
 * 
 * @author Andreas Groscurth (groscurt@ebi.ac.uk)
 */
public class DisplayAction extends Action {
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an ActionForward instance describing where and how control should
     * be forwarded, or null if the response has already been completed.
     * 
     * @param mapping - The <code>ActionMapping</code> used to select this
     *            instance
     * @param form - The optional <code>ActionForm</code> bean for this
     *            request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     * @return - represents a destination to which the controller servlet,
     *         <code>ActionServlet</code>, might be directed to perform a
     *         RequestDispatcher.forward() or HttpServletResponse.sendRedirect()
     *         to, as a result of processing activities of an
     *         <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        IntactUserI user = (IntactUserI) session.getAttribute(Constants.USER);

        try {
            Collection searchFor = (Collection) request
                    .getAttribute(Constants.SEARCH);

            // if no user is in the current session an excepion is thrown
            // because up to now a user should have been created and e.g.
            // the search ac nr. should have been set.
            if (user == null || searchFor == null) {
                throw new NullPointerException("No user could be found in the "
                        + "current session");
            }
            // if less than two search Ac are given
            else if (searchFor.size() < 2) {
                request.setAttribute(Constants.ERROR, new ErrorForm(
                        "At least two interactors have to be given !"
                                + " No network can be displayed !"));
                return mapping.findForward(Constants.ERROR);
            }

            Constants.LOGGER.info("start minehelper");

            MineHelper helper = new MineHelper(user);
            // the network map maps the wrapper class containing
            // the biosource taxid and the graphid to a collection
            // of search acnr.
            Map networks = helper.getNetworkMap(searchFor);

            NetworkKey key;
            MineData md;
            Collection search = null;
            for (Iterator iter = networks.keySet().iterator(); iter.hasNext();) {
                // the key stores the taxid and graphid for the current search
                key = (NetworkKey) iter.next();
                search = (Collection) networks.get(key);

                // if the current search ac are in a graph in the database
                if (key.getGraphID() != 0) {
                    // the wrapper class which stores the graph and the
                    // searchac->vertex map is fetched from the user
                    md = user.getMineData(key);
                    // if there is no data, the graph is build and add to the
                    // user
                    if (md == null) {
                        md = helper.buildGraph(key);
                        user.addToGraphMap(key, md);
                    }

                    // the shortest path is computed
                    Collection path = helper.mine(md, search);
                    // if no path could be found the search values are
                    // added to the singleton collection of the user
                    if (path.size() == 0) {
                        user.addToSingletons(helper.getShortLabels(search));
                    }
                    else {
                        user.addToPath(path);
                    }
                }
                else {
                    user.addToSingletons(helper.getShortLabels(search));
                }
            }

            // if no paths could been found the application is forwarded
            // to the error page
            if (user.getPaths().size() == 0) {
                String singletons = user.getSingletons().toString();
                singletons = singletons.substring(1, singletons.length() - 1);

                request.setAttribute(Constants.ERROR, new ErrorForm(
                        "The proteins <i>" + singletons
                                + "</i> are not in a connecting network !<br>"
                                + "Therfore no display is available !"));
                return mapping.findForward(Constants.ERROR);
            }

            return mapping.findForward(Constants.SUCCESS);
        }
        catch (Exception e) {
            request.setAttribute(Constants.ERROR, new ErrorForm(e
                    .getLocalizedMessage()));
            return mapping.findForward(Constants.ERROR);
        }
    }
}