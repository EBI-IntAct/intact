package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.AbstractViewBean;
import uk.ac.ebi.intact.application.search3.struts.view.beans.SingleResultViewBean;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * This class provides the actions to handle the case if the retriving resultset from the
 * searchaction is too big. The Action creates a basic statistic which gives an overview over the
 * resultsset. this statistics will be shown in the web-interface.
 *
 * @author Michael Kleen (mkleen@ebi.ac.uk)
 */

public class TooLargeAction extends AbstractResultAction {

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        logger.info("tooLarge action: the result contains to many objects");

        // create help link
        String relativeHelpLink = getServlet().getServletContext().getInitParameter("helpLink");
        String ctxtPath = request.getContextPath();
        String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));
        String helpLink = relativePath.concat(relativeHelpLink);

        // get the resultinfo from the initial request from the search action
        Map resultInfo = ((Map) request.getAttribute(SearchConstants.RESULT_INFO));

        // count the complete result information in 4 different categories
        // this is necessary because all controlled vocabulary terms should
        // count in the same category

        Collection someKeys = resultInfo.keySet();

        int cvCount = 0;
        int proteinCount = 0;
        int experimentCount = 0;
        int interactionCount = 0;

        for (Iterator iterator = someKeys.iterator(); iterator.hasNext();) {
            String className = (String) iterator.next();
            logger.info("tooLarge action: searching for class" + className);
            Class clazz = null;
            try {
                clazz = Class.forName(className);
                if (Protein.class.isAssignableFrom(clazz)) {
                    proteinCount += ((Integer) resultInfo.get(className)).intValue();
                } else {
                    if (Experiment.class.isAssignableFrom(clazz)) {
                        experimentCount += ((Integer) resultInfo.get(className)).intValue();
                    } else {
                        if (Interaction.class.isAssignableFrom(clazz)) {
                            interactionCount += ((Integer) resultInfo.get(className)).intValue();
                        } else {
                            if (CvObject.class.isAssignableFrom(clazz)) {
                                cvCount += ((Integer) resultInfo.get(className)).intValue();
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException e) {

                logger.info(
                        "tooLarge action: the resultset contains to an object which is no assignable from an intactType");
                logger.info("tooLarge action: forward to an errorpage");
                return mapping.findForward(SearchConstants.FORWARD_FAILURE);

            }
        } // for
                 
        // now create a couple of viewbeans with the results for the jsp
        Collection result = new ArrayList(4);
        if (experimentCount > 0) {
            result.add(
                    new SingleResultViewBean("Experiment", new Integer(experimentCount).toString(),
                            helpLink));
        }
        if (interactionCount > 0) {
            result.add(
                    new SingleResultViewBean("Interaction",
                            new Integer(interactionCount).toString(), helpLink));
        }
        if (proteinCount > 0) {
            result.add(
                    new SingleResultViewBean("Protein", new Integer(proteinCount).toString(),
                            helpLink));
        }
        if (cvCount > 0) {
            result.add(
                    new SingleResultViewBean("Controlled vocabulary term",
                            new Integer(cvCount).toString(), helpLink));
        }

        // add the viewbean to the request and forward to the jsp
        request.setAttribute(SearchConstants.VIEW_BEAN, result);
        return mapping.findForward("results");
    }

    /*
    * only here to satify the interface
    */
    protected AbstractViewBean getAbstractViewBean(Object result, IntactUserIF user,
                                                   String contextPath) {
        return null;
    }

}
