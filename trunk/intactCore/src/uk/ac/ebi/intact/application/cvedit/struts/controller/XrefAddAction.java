/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.*;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action class is called when the user adds a new cross reference to
 * the current edit CV object.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefAddAction extends IntactBaseAction {

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
     * @return - represents a destination to which the controller servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Need the form to get data entered by the user.
        DynaActionForm theForm = (DynaActionForm) form;

        // Handler to the IntactUserIF.
        IntactUserIF user = super.getIntactUser(request);

        // The new xref to add to the current cv object.
        Xref xref = null;

        try {
            // The owner of the object we are editing.
            Institution owner = user.getInstitution();

            // The database the new xref belong to.
            CvDatabase db = (CvDatabase) user.getObjectByLabel(
                CvDatabase.class, (String) theForm.get("database"));

            xref = new Xref(owner, db, (String) theForm.get("primaryId"),
                (String) theForm.get("secondaryId"), (String) theForm.get("releaseNumber"));

            // Only set it if we have a non empty list for qualifiers.
            if (!user.isQualifierListEmpty()) {
                CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(
                    CvXrefQualifier.class, (String) theForm.get("qualifer"));
                xref.setCvXrefQualifier(xqual);
            }
        }
        catch (SearchException se) {
            // Error in accessing the database.
            super.addError("error.search", se.getNestedMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        catch (IntactException ie) {
            // This shoudn't be thrown at all; declared to throw from the
            // Xref constructor
            assert false;
        }
        // Add the annotation to the view.
        CvViewBean viewbean = user.getView();
        viewbean.addXref(xref);

        // Clear previous entries.
        theForm.reset(mapping, request);

        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }
}