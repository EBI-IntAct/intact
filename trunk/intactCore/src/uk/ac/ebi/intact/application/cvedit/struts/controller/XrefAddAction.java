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
import java.util.Collection;

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
    public ActionForward perform (ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        // Handler to the IntactUserIF.
        IntactUserIF user = super.getIntactUser(request);

        // Need the form to get data entered by the user.
        XrefAddForm theForm = (XrefAddForm) form;

        // The new xref to add to the current cv object.
        Xref xref = null;

        try {
            // The owner of the object we are editing.
            Institution owner = user.getInstitution();

            // The database the new xref belong to.
            CvDatabase db = (CvDatabase) user.getObjectByLabel(
                CvDatabase.class, theForm.getDatabase());

            xref = new Xref(owner, db, theForm.getPrimaryId(),
                theForm.getSecondaryId(), theForm.getReleaseNumber());

            // Only set it if we have a non empty list for qualifiers.
            if (!user.isListEmpty(IntactUserIF.QUALIFIER_NAMES)) {
                CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(
                    CvXrefQualifier.class, theForm.getQualifer());
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
        // The current CV object we are editing.
        CvObject cvobj = user.getCurrentEditObject();

        // Add the xref to the current cv object.
        cvobj.addXref(xref);

        // Create the xref on the database.
        try {
            user.create(xref);
        }
        catch (IntactException ie) {
            // Unable to create an annotation; no nested message provided.
            super.addError("error.create", ie.getMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        // We need to update on the screen as well.
        XreferenceBean bean = new XreferenceBean(xref, user.isActive());

        // The annotation collection.
        Collection beans = (Collection) super.getSessionObject(
            request, WebIntactConstants.XREFS);
        beans.add(bean);

        // Reset the form
        theForm.reset();

        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }
}
