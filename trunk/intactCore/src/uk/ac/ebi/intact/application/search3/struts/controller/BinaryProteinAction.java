package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.SimpleViewBean;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * /** This class provides the actions required  for an url based search query for 2 proteins. The
 * action recieve 2 proteins from the Search Action
 *
 * @author Michael Kleen
 * @version BinaryProteinAction.java Date: Jan 14, 2005 Time: 12:53:45 PM
 */
public class BinaryProteinAction extends IntactBaseAction {


    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        logger.info("binary action");
        // Session to access various session objects. This will create
        // a new session if one does not exist.
        HttpSession session = super.getSession(request);

        // Handle to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if (user == null) {
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        // get the search results from the request
        // and calculate the the particapating interactions
        Collection someProteins = (Collection) request.getAttribute(SearchConstants.SEARCH_RESULTS);

        // forward to error page if we got lesser than 1 protein
        if (someProteins.size() < 1) {
            return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
        }

        Collection participatingInteractions = this.processtParticipatedInteractions(someProteins);
        logger.info("interactions : " + participatingInteractions);


        // forward to error page if we found no interaction
        if (participatingInteractions.isEmpty()) {
            return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
        }


        // build the URL for searches and pass to the view beans
        String contextPath = request.getContextPath();
        String appPath = getServlet().getServletContext().getInitParameter("searchLink");
        String searchURL = contextPath.concat(appPath);

        // now create an chunk of of wrapped Interactions for the jsp
        List interactionList = new ArrayList();

        for (Iterator it = participatingInteractions.iterator(); it.hasNext();) {

            Interaction anInteraction = (Interaction) it.next();
            logger.info("Interaction  = " + anInteraction);
            interactionList.add(
                    new SimpleViewBean(anInteraction, user.getHelpLink(), searchURL, contextPath));
        }

        // now create an list which holds the results and forward to the jsp
        Collection viewBeanList = new ArrayList();
        viewBeanList.add(interactionList);
        // put the viewbeans in the request and send on to the view...
        request.setAttribute(SearchConstants.VIEW_BEAN_LIST, viewBeanList);

        //get the maximum size beans from the context for later use
        Map sizeMap = (Map) session.getServletContext().getAttribute(SearchConstants.MAX_ITEMS_MAP);

        return mapping.findForward("results");

    }


    private Collection processtParticipatedInteractions(Collection someProteins) {

        logger.info("process ParticaptedInteractions");

        Collection result = new HashSet();
        Iterator iterator = someProteins.iterator();


        // get all interactions from protein1

        Protein protein1 = (Protein) iterator.next();
        Set componentSet1 = new HashSet(protein1.getActiveInstances());
        Set component1Interactions = new HashSet();

        for (Iterator iterator1 = componentSet1.iterator(); iterator1.hasNext();) {
            Component component = (Component) iterator1.next();
            component1Interactions.add(component.getInteraction());
        }

        // if we got another protein, get also all interactions from protein2
        if (someProteins.size() == 2) {
            Protein protein2 = (Protein) iterator.next();
            Set componentSet2 = new HashSet(protein2.getActiveInstances());

            for (Iterator iterator2 = componentSet2.iterator(); iterator2.hasNext();) {
                Component component2 = (Component) iterator2.next();
                Interaction component2Interaction = component2.getInteraction();

                if (component1Interactions.contains(component2Interaction)) {
                    result.add(component2Interaction);
                }
            }
        } else {

            // check now for self Interactions from protein1
            for (Iterator iterator3 = component1Interactions.iterator();
                 iterator3.hasNext();) {
                Interaction interaction = (Interaction) iterator3.next();
                Collection interactionComponents = interaction.getComponents();

                if (interactionComponents.size() == 2) {
                    Iterator iterator4 = interactionComponents.iterator();
                    Component component1 = (Component) iterator4.next();
                    Component component2 = (Component) iterator4.next();
                    if (component1.getStoichiometry() == 1 &&
                            component2.getStoichiometry() == 1) {
                        result.add(interaction);
                    }
                } else if (interactionComponents.size() == 1) {
                    Iterator iterator5 = interactionComponents.iterator();
                    Component component1 = (Component) iterator5.next();
                    if (component1.getStoichiometry() == 2) {
                        result.add(interaction);
                    }
                }
            }
        }
        return result;
    }

}



