/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.biosrc;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.action.SubmitFormAction;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.struts.view.biosrc.BioSourceViewBean;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.NewtServerProxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The action to handle tax id for a bio source.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceAction extends SubmitFormAction {

    /**
     * The tax id database.
     */
    private static final String TAX_DB = "newt";

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
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // To report errors.
        ActionErrors errors;

        // Extract the tax id from the form.
        DynaActionForm theForm = (DynaActionForm) form;

        // The tax id to search the Newt database.
        String taxid = (String) theForm.get("taxId");

        // Handler to the user.
        EditUserI user = getIntactUser(request);

        // Must be an integer.
        try {
            Integer.parseInt(taxid);
        }
        catch (NumberFormatException nfe) {
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.taxid.mask", taxid));
            saveErrors(request, errors);
            // Non integer value for taxid. Display the error in the input page.
            return mapping.getInputForward();
        }
        // This shouldn't cause a class cast exception as we had
        // already created the correct editor view bean.
        BioSourceViewBean bioview = (BioSourceViewBean) user.getView();

        // The response from the Newt server.
        NewtServerProxy.NewtResponse newtResponse =
                getNewtResponse(user, taxid, request);

        // Any errors?
        if (hasErrors(request)) {
            // Errors are displayed in the input page.
            return mapping.getInputForward();
        }
        // Values from newt.
        String newtLabel = newtResponse.getShortLabel();
        String newtName = newtResponse.getFullName();

        // Validate the short label; compute the new name using
        // scientific name and tax id for an empty short label.
        newtLabel = newtResponse.hasShortLabel()
                ? user.getUniqueShortLabel(newtLabel, taxid)
                : this.getUniqueShortLabel(newtName, taxid, user);

        // Covert to lowercase as we only allow LC characters for a short label.
        newtLabel = newtLabel.toLowerCase();

        // Validate the scientific name.
        if (newtName.length() == 0) {
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.newt.name", taxid));
            // Display the error and continue on.
            saveErrors(request, errors);
        }
        // Retrieve the xref bean for previous tax id.
        XreferenceBean taxXref = findXref(TAX_DB, bioview);

        // Have we got an existing Xref?
        if (taxXref != null) {
            // Delete the existing xref as we are replacing it with a new one.
            bioview.delXref(taxXref);
        }
        // Add the nex Xref.
        Xref xref = createTaxXref(user, taxid, newtLabel);
        bioview.addXref(new XreferenceBean(xref));

        // Set the view with the new inputs.
        bioview.setShortLabel(newtLabel);
        bioview.setFullName(newtName);
        bioview.setTaxId(taxid);

        return mapping.findForward(SUCCESS);
    }

    private NewtServerProxy.NewtResponse getNewtResponse(EditUserI user,
                                                         String taxid,
                                                         HttpServletRequest request) {
        // To report errors.
        ActionErrors errors;

        // Handler to the Newt server.
        NewtServerProxy newtServer = user.getNewtProxy();

        // Query the server.
        NewtServerProxy.NewtResponse newtResponse = null;
        try {
            newtResponse = newtServer.query(Integer.parseInt(taxid));
        }
        catch (IOException ioe) {
            // Error in communcating with the server.
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.newt.connection", ioe.getMessage()));
            saveErrors(request, errors);
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.newt.search", taxid));
            saveErrors(request, errors);
        }
        return newtResponse;
    }

    /**
     * Returns a unique short label.
     * @param name the scientific nsme returned by the Newt server.
     * @param taxid the tax id to use as the extrnal ac when short label is
     * not unique
     * @param user handler to the user to access the method to get a
     * unique short label.
     * @return tax id if the computed short label is empty; otherwise
     * it could be either new computed label or the tax id.
     * @throws SearchException for errors in searching the database.
     */
    private String getUniqueShortLabel(String name, String taxid, EditUserI user)
            throws SearchException {
        // Save the computed a short label.
        String newlabel = computeShortLabel(name);
        // new label is empty if a label cannot be constructed.
        if (newlabel.length() == 0) {
            // Try the tax id as an external ac.
            return user.getUniqueShortLabel(taxid);
        }
        // Check for uniqueness of the computed label.
        return user.getUniqueShortLabel(newlabel, taxid);
    }

    private Xref createTaxXref(EditUserI user, String taxid, String label)
            throws SearchException {
        // The owner of the object we are editing.
        Institution owner = user.getInstitution();

        // The database the new xref belong to.
        CvDatabase db = (CvDatabase) user.getObjectByLabel(
                CvDatabase.class, TAX_DB);

        CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(
                CvXrefQualifier.class, "identity");
        return new Xref(owner, db, taxid, label, null, xqual);
    }

    /**
     * Returns the Xreference bean for given primary id and the database name.
     * @param dbname the name of the database to match.
     * @param bioview the view bean to access the xrefs and the tax id.
     * @return <code>XreferenceBean</code> whose database name and primary id
     * are as same as <code>dbname</code> and the primar id of
     * <code>bioview</code> respectively; <code>null</code> is returned for
     * otherwise.
     */
    private XreferenceBean findXref(String dbname, BioSourceViewBean bioview) {
        // The xrefs to loop.
        List xrefs = bioview.getXrefs();
        // The primary id to match.
        String primaryId = bioview.getTaxId();

        // Return as soon as a match is found.
        for (Iterator iter = xrefs.iterator(); iter.hasNext();) {
            XreferenceBean xrefbean = (XreferenceBean) iter.next();
            if (xrefbean.getDatabase().equals(dbname) &&
                    xrefbean.getPrimaryId().equals(primaryId)) {
                return xrefbean;
            }
        }
        // No mtach found.
        return null;
    }

    /**
     * Returns the short label computed using the scientific name.
     * @param sciName the scientific name to compute the short label.
     * @return this is computed as follows: concatenation of the first three
     * chars of the first word and the first two characters of the second
     * word. An empty string is returned for all other instances.
     */
    private String computeShortLabel(String sciName) {
        // Split the scientific name to extract first two awards.
        StringTokenizer stk = new StringTokenizer(sciName);
        String firstWord = stk.nextToken();
        String firstThreeChars = "";
        if (firstWord.length() >= 3) {
            firstThreeChars = firstWord.substring(0, 3);
        }
        String firstTwoChars = "";
        if (stk.hasMoreTokens()) {
            String secondWord = stk.nextToken();
            if (secondWord.length() >= 2) {
                firstTwoChars = secondWord.substring(0, 2);
            }
        }
        return firstThreeChars + firstTwoChars;
    }
}
