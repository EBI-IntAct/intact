/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.ojb.broker.query.Query;
import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.OJBQueryFactory;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ExperimentRowData;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.wrappers.ResultRowData;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Xref;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * The action class to search an Experiment (in the context of an Interaction).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 *
 * @struts.action
 *      path="/int/exp/search"
 *      name="intForm"
 *      input="edit.layout"
 *      scope="session"
 *      validate="false"
 *      parameter="dispatch"
 */
public class ExperimentDispatchAction extends AbstractEditorDispatchAction {

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("int.exp.button.recent", "recent");
        map.put("int.exp.button.search", "search");
        return map;
    }

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping - The <code>ActionMapping</code> used to select this instance
     * @param form - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the action servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward recent(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        Set recentExps = user.getCurrentExperiments();
        if (recentExps.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.exp.search",
                    new ActionError("error.int.exp.search.recent.empty"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        // We have edited/added experiments in the current session.
        view.addExperimentToHold(recentExps);

        return mapping.getInputForward();
    }

    public ActionForward search(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // Search value to search experiment.
        String searchValue = intform.getExpSearchValue();

        if (searchValue.length() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.exp.search",
                    new ActionError("error.int.exp.search.input"));
            saveErrors(request, errors);
            setAnchor(request, intform);
            return mapping.getInputForward();
        }
        // The array to store queries.
        Query[] queries = getSearchQueries(Experiment.class, searchValue);

        // The maximum interactions allowed.
        int max = getService().getInteger("exp.search.limit");

        // The results to display.
        List results = super.search(queries, max, request, "err.search");

        if (results.isEmpty()) {
            // Errors or empty or too large
            return mapping.getInputForward();
        }
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // The helper to make experiment rows.
        IntactHelper helper = user.getIntactHelper();

        // Add the search result to the holder.
        view.addExperimentToHold(makeExperimentRows(results, helper));

        return mapping.getInputForward();
    }

    private List makeExperimentRows(List rows, IntactHelper helper) throws IntactException {
        // The collection to return; it consists of exp row data.
        List expRows = new ArrayList();

        // The query factory to get a query.
        OJBQueryFactory qf = OJBQueryFactory.getInstance();

        // The primary reference AC.
        String ac = ((AnnotatedObject) helper.getObjectByLabel(
                CvXrefQualifier.class, "primary-reference")).getAc();

        for (Iterator iter = rows.iterator(); iter.hasNext(); ) {
            ResultRowData rrd = (ResultRowData) iter.next();
            ExperimentRowData expRow = new ExperimentRowData(rrd.getAc(),
                    rrd.getShortLabel(), rrd.getFullName());
            Query query = qf.getQualifierXrefQuery(ac, expRow.getAc());
            Xref xref = (Xref) helper.getObjectByQuery(query);
            // Xref is null if no primary reference found for the experiment.
            if (xref != null) {
                expRow.setPubMedLink(xref);
            }
            expRows.add(expRow);
        }
        return expRows;
    }
}