package uk.ac.ebi.intact.application.hierarchView.struts;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUser;
import uk.ac.ebi.intact.application.hierarchView.business.graph.GraphHelper;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.business.image.GraphToSVG;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Implementation of <strong>Action</strong> that validates a centered submisson (from a link).
 *
 * @author Samuel Kerrien
 * @version $Id$
 */
public final class CenteredAction extends Action {

    // --------------------------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward perform (ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException, ServletException {

        // get the current session
        HttpSession session = request.getSession();

        // read form values from the bean
        ActionErrors errors = new ActionErrors();

        String AC = null;

        // look in the request ...
        AC = request.getParameter ("AC");

        if ((null == AC) || (AC.length() < 1)) {
            errors.add("AC", new ActionError("error.centeredAC.required"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.empty()) {
            saveErrors(request, errors);
            return (new ActionForward(mapping.getInput()));
        }

        String currentAC = (String)  session.getAttribute (Constants.ATTRIBUTE_AC);

        if (!AC.equals(currentAC)) {

            // Creation of the graph and the image
            InteractionNetwork in = null;

            int depthInt = 0;
            String depth = (String) session.getAttribute (Constants.ATTRIBUTE_DEPTH);

            try {
                // retreive datasource fron the session
                IntactUser user = (IntactUser) session.getAttribute (uk.ac.ebi.intact.application.hierarchView.business.Constants.USER_KEY);
                if (null == user) {
                    errors.add ("Datasource", new ActionError("error.datasource.notCreated"));
                    saveErrors(request, errors);
                    return (mapping.findForward("error"));
                }
                GraphHelper gh = new GraphHelper ( user.getIntactHelper() );

                depthInt = Integer.parseInt (depth);
                in = gh.getInteractionNetwork (AC, depthInt);
            } catch (Exception e) {
                errors.add ("InteractionNetwork", new ActionError("error.interactionNetwork.notCreated"));
                saveErrors(request, errors);
                return (mapping.findForward("error"));
            }

            if (null == in) {
                // warn the user that an error occur
                errors.add ("InteractionNetwork", new ActionError("error.interactionNetwork.notCreated"));
            } else {
                if (0 == in.sizeNodes()) {
                    errors.add ("InteractionNetwork", new ActionError("error.interactionNetwork.noProteinFound"));
                }
            }

            if (!errors.empty()) {
                // Report any errors we have discovered back to the original form
                saveErrors(request, errors);
                return (mapping.findForward("error"));
            }

            String dataTlp = in.exportTlp();
            in.importDataToImage(dataTlp);

            GraphToSVG te = new GraphToSVG(in);
            te.draw();
            ImageBean ib  = te.getImageBean();

            if (null == ib) {
                errors.add("ImageBean", new ActionError("error.ImageBean.build"));
                saveErrors(request, errors);
                return (mapping.findForward("error"));
            }

            // Save our data in the session
            session.setAttribute(Constants.ATTRIBUTE_AC, AC);

            // store the bean
            session.setAttribute (Constants.ATTRIBUTE_IMAGE_BEAN, ib);
            // store the graph
            session.setAttribute (Constants.ATTRIBUTE_GRAPH, in);
        }

        // Print debug in the log file
        if (servlet.getDebug() >= 1)
            servlet.log("CenteredAction: AC=" + AC +
                    "\nlogged on in session " + session.getId());

        // Remove the obsolete form bean
        if (mapping.getAttribute() != null) {
            if ("request".equals(mapping.getScope()))
                request.removeAttribute(mapping.getAttribute());
            else
                session.removeAttribute(mapping.getAttribute());
        }

        // Forward control to the specified success URI
        return (mapping.findForward("success"));

    }

} // CenteredAction
