/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The action class to search a Protein.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinSearchAction extends AbstractEditorAction {

    /**
     * SP AC. 10 characters allowed.
     */
    private static final Pattern ourSpAcPat = Pattern.compile("\\w{1,10}$");

    /**
     * Intact AC. Must start with either uppercase characters, followed by
     * '-' and a number.
     */
    private static final Pattern ourIntactAcPat = Pattern.compile("[A-Z]+\\-[0-9]+$");

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
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        String ac = ((String) dynaform.get("protSearchAC")).trim();
        String spAc = ((String) dynaform.get("protSearchSpAC")).trim();
        String shortLabel = ((String) dynaform.get("protSearchLabel")).trim();

        // Cache string lengths.
        int acLen = ac.length();
        int spAcLen = spAc.length();
        int shortLabelLen = shortLabel.length();

        // Error if all three fields are empty.
        if ((acLen == 0) && (spAcLen == 0) && (shortLabelLen == 0)) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.protein.search.input"));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }
        // The default values for search.
        String value = shortLabel;
        String param = "shortLabel";

        if (acLen != 0) {
            Matcher matcher = ourIntactAcPat.matcher(ac);
            if (!matcher.matches()) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR,
                        new ActionError("error.int.protein.search.ac"));
                saveErrors(request, errors);
                return mapping.findForward(FAILURE);
            }
            value = ac;
            param = "ac";
        }
        else if (spAcLen != 0) {
            Matcher matcher = ourSpAcPat.matcher(spAc);
            if (!matcher.matches()) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR,
                        new ActionError("error.int.protein.search.sp"));
                saveErrors(request, errors);
                return mapping.findForward(FAILURE);
            }
            value = spAc;
            param = "spAc";
        }
        // Handler to the current user.
        EditUserI user = getIntactUser(request);

        // The collection to hold proteins.
        Collection proteins;

        if (param.equals("spAc")) {
            proteins = user.getSPTRProteins(value);
        }
        else {
            proteins = user.search1(Protein.class.getName(), param, value);
        }
        // Search found any results?
        if (proteins.isEmpty()) {
            // Log the error if we have one.
            Exception exp = user.getProteinParseException();
            if (exp != null) {
                LOGGER.info(exp);
            }
            // The error to display on the web page.
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.protein.search.empty", param));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }
        // The number of Proteins retrieved from the search.
        int psize = proteins.size();

        // The protein search limit.
        String protlimit = getService().getResource("protein.search.limit");
        if (psize > Integer.parseInt(protlimit)) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.protein.search.many",
                            Integer.toString(psize), param, protlimit));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }
        // Can safely cast it as we have the correct editor view bean.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        for (Iterator iter = proteins.iterator(); iter.hasNext();) {
            view.addProtein((Protein) iter.next());
        }
        return mapping.findForward(SUCCESS);
    }
}