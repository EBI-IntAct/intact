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
import uk.ac.ebi.intact.application.editor.struts.view.biosrc.BioSourceActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.biosrc.BioSourceViewBean;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Xref;
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
     * @param mapping  - The <code>ActionMapping</code> used to select this instance
     * @param form     - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request  - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     * @return - represents a destination to which the action servlet,
     *         <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     *         or HttpServletResponse.sendRedirect() to, as a result of processing
     *         activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // To report errors.
        ActionErrors errors;

        // Extract the tax id from the form.
        BioSourceActionForm bsform = (BioSourceActionForm) form;

        // The tax id to search the Newt database.
        String taxid = bsform.getTaxId();

        // Handler to the user.
        EditUserI user = getIntactUser(request);

        // Need to validate here because we have set the validate to false.
        // Can't reply on form validation here because it validates the short
        // label as well (the short label is derived from this action!).
        try {
            Integer.parseInt(taxid);
        }
        catch (NumberFormatException nfe) {
            errors = new ActionErrors();
            errors.add("bs.taxid", new ActionError("error.taxid.mask", taxid));
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
        String newtName = newtResponse.getFullName();

        // Compute a new name using scientific name and tax id if the Newt server
        // hasn't got a short label.
        String newtLabel = newtResponse.hasShortLabel()
                ? newtResponse.getShortLabel() : getUniqueShortLabel(newtResponse);

        // Covert to lowercase as we only allow LC characters for a short label.
        newtLabel = newtLabel.toLowerCase();

        // Validate the scientific name.
        if (newtName.length() == 0) {
            errors = new ActionErrors();
            errors.add("bs.taxid", new ActionError("error.newt.name", taxid));
            // Save the error and continue on.
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

        // Collection of biosources for the current taxid.
        Collection results = user.search1(BioSource.class.getName(),
                "taxId", taxid);

        // AC of the current biosource.
        String ac = user.getView().getAc();

        // Any existing tax ids?
        if (taxIdExists(results, ac)) {
            // Found a tax id which belongs to another biosource.
            ActionMessages msgs = new ActionMessages();
            String topic = user.getSelectedTopic();
            String link = getBioSourcesLink(results, ac, topic);
            msgs.add("bs.taxid", new ActionMessage("error.newt.taxid", taxid,
                    link));
            saveMessages(request, msgs);
        }

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
            errors.add("bs.taxid",
                    new ActionError("error.newt.connection", ioe.getMessage()));
            saveErrors(request, errors);
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            errors = new ActionErrors();
            errors.add("bs.taxid", new ActionError("error.newt.search", taxid));
            saveErrors(request, errors);
        }
        return newtResponse;
    }

    /**
     * Returns a unique short label.
     * @param response the response from the Newt server.
     * @return default short label from the Newt response if the computed short
     * label is empty; otherwise it is the new computed label.
     */
    private String getUniqueShortLabel(NewtServerProxy.NewtResponse response) {
        // Save the computed a short label.
        String newlabel = computeShortLabel(response.getFullName());
        // new label is empty if a label cannot be constructed.
        if (newlabel.length() == 0) {
            // Get the default short label from the newt response.
            return response.getShortLabel();
        }
        return newlabel;
    }

    private Xref createTaxXref(EditUserI user, String taxid, String label)
            throws SearchException {
        // The owner of the object we are editing.
        Institution owner = user.getInstitution();

        // The database the new xref belong to.
        CvDatabase db = (CvDatabase) user.getObjectByLabel(CvDatabase.class, TAX_DB);

        CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(CvXrefQualifier.class, "identity");
        return new Xref(owner, db, taxid, label, null, xqual);
    }

    /**
     * Returns the Xreference bean for given primary id and the database name.
     *
     * @param dbname  the name of the database to match.
     * @param bioview the view bean to access the xrefs and the tax id.
     * @return <code>XreferenceBean</code> whose database name and primary id
     *         are as same as <code>dbname</code> and the primar id of
     *         <code>bioview</code> respectively; <code>null</code> is returned for
     *         otherwise.
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
     *
     * @param sciName the scientific name to compute the short label.
     * @return this is computed as follows: concatenation of the first three
     *         chars of the first word and the first two characters of the second
     *         word. An empty string is returned for all other instances.
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

    /**
     * True if there is a tax id other than the current one exists of the
     * persistent system.
     *
     * @param results a collection of biosources for the current tax id.
     * @param ac      the AC of the current biosource. Could be null for a new biosource.
     * @return <code>false</code> if the search fails or a BioSource
     *         instance found  with the same <code>taxid</code>.
     */
    private boolean taxIdExists(Collection results, String ac) {
        if (results.isEmpty()) {
            // Don't have this tax id on the database.
            return false;
        }
        // If we found a single record; is it the current record ?
        if (results.size() == 1) {
            String resultAc = ((BioSource) results.iterator().next()).getAc();
            if (resultAc.equals(ac)) {
                // We have retrieved the same record from the DB.
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a list of biosource links derived from given collection.
     *
     * @param results a collection of BioSOurce objects.
     * @param ac      the AC to filter out. The biosource with this AC is not
     *                included in the list.
     * @param topic   the topic to incliude in the search link.
     * @return a link to access various biosources from the search application.
     */
    private String getBioSourcesLink(Collection results, String ac, String topic) {
        // The buffer to construct existing labels.
        StringBuffer sb = new StringBuffer();

        // Flag to indicate processing of the first item.
        boolean first = true;

        // Search the database.
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            BioSource bs = (BioSource) iter.next();
            if (bs.getAc().equals(ac)) {
                // Filter out the current ac.
                continue;
            }
            String label = bs.getShortLabel();
            if (first) {
                // Avoid prefixing with "," for the first item.
                first = false;
            }
            else {
                sb.append(", ");
            }
            sb.append("<a href=\"" + "javascript:show('" + topic + "', '"
                    + label + "')\"" + ">" + label + "</a>");
        }
        return sb.toString();
    }
}
