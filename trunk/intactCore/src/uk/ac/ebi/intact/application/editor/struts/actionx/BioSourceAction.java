/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.actionx;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.viewx.BioSourceViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.business.DuplicateLabelException;
import uk.ac.ebi.intact.persistence.SearchException;
import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Collection;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;

/**
 * Handles when the user enters a value for tax id. The short label and the full
 * name of the BioSource object is replaced with the values retrieved from the
 * Newt server using the tax id.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceAction extends AbstractEditorAction {

    /**
     * Regular expression to extract short label and fullname. The pattern is
     * a number|text for short label|full name|ignore other text
     */
    private static final Pattern REG_EXP =
            Pattern.compile("(\\d+)\\|(.*?)\\|(.*?)\\|.*");

    /**
     * The tax id database.
     */
    private static final String TAX_DB = "SGD";

    /**
     * Action for submitting the CV info form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in searching the database; refresh
     * mapping if the screen needs to be updated (this will only happen if the
     * short label on the screen is different to the short label returned by
     * getUnqiueShortLabel method. For all other instances, success mapping is
     * returned.
     * @throws java.lang.Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Extract the tax id from the form.
        DynaActionForm theForm = (DynaActionForm) form;
        String taxid = (String) theForm.get("taxId");

        // Handler to the user.
        EditUserI user = getIntactUser(request);

        // Validate the tax id; it should be unique.
        if (!validateTaxId(user, taxid, request)) {
            return new ActionForward(mapping.getInput());
        }
        // To report errors.
        ActionErrors errors;

        // The service instance to access newt server properties.
        EditorService service = super.getService();

        // Check for null URL - for an invalid URL loaded from properties file.
        URL newtUrl = service.getNewtURL();
        if (newtUrl == null) {
            errors = new ActionErrors();
            errors.add(EDITOR_ERROR, new ActionError("error.newt.connection",
                    newtUrl.toExternalForm()));
            saveErrors(request, errors);
            return new ActionForward(mapping.getInput());
        }
        // The queries to send to Newt server.
        String query = service.getNewtQueryParameters() + taxid + "\r\n";

        // Get the newt response.
        String newtResp = null;
        try {
            newtResp = this.getNewtResponse(newtUrl, query);
            super.log("newt response: " + newtResp);
        }
        catch (IOException ioe) {
            // Error in communcating with the server.
            errors = new ActionErrors();
            errors.add(EDITOR_ERROR, new ActionError("error.newt.io",
                    newtUrl.toExternalForm()));
            saveErrors(request, errors);
            return new ActionForward(mapping.getInput());
        }
        // Parse the newt response.
        Matcher matcher = REG_EXP.matcher(newtResp);

        // This shouldn't crash the application as we had
        // already created the correct editor view bean.
        BioSourceViewBean bioview = (BioSourceViewBean) user.getView();

        // We should have matches.
        if (!matcher.matches()) {
            errors = new ActionErrors();
            errors.add("cvinfo", new ActionError("error.newt.search", taxid));
            saveErrors(request, errors);
            // Restore (on screen) back to the bean's tax id.
            theForm.set("taxId", bioview.getTaxId());
            return new ActionForward(mapping.getInput());
        }
        // Values from newt.
        String newtLabel = matcher.group(2);
        String newtName = matcher.group(3);

        // Validate the short label; compute the new name using
        // scientific name and tax id for an empty short label.
        newtLabel = newtLabel.length() == 0
                ? this.getUniqueShortLabel(newtName, taxid, user)
                : user.getUniqueShortLabel(newtLabel, taxid);

        // Validate the scientific name.
        if (newtName.length() == 0) {
            errors = new ActionErrors();
            errors.add("cvinfo", new ActionError("error.newt.name", taxid));
            // Display the error and continue on.
            saveErrors(request, errors);
        }
        // Retrieve the xref bean for previous tax id.
        XreferenceBean taxXref = findXref(TAX_DB, bioview);

        // Set the values for existing tax ref.
        if (taxXref != null) {
            // We have a xref bean for previous tax id; set it with inputs from
            // the form.
            taxXref.setPrimaryId(taxid);
            taxXref.setSecondaryId(newtLabel);
            // Need to update this xref.
            bioview.addXrefToUpdate(taxXref);
        }
        else {
            try {
                // We don't have an xref for tax id yet; create xref with data
                // from the form.
                Xref xref = createTaxXref(user, taxid, newtLabel);
                bioview.addXref(xref);
            }
            catch (DuplicateLabelException se) {
                // Duplicate databases?
                log(ExceptionUtils.getStackTrace(se));
                errors = new ActionErrors();
                errors.add(AbstractEditorAction.EDITOR_ERROR,
                        new ActionError("error.search", se.getNestedMessage()));
                saveErrors(request, errors);
                return mapping.findForward(EditorConstants.FORWARD_FAILURE);
            }
        }
        // Set the view with the new inputs.
        bioview.setShortLabel(newtLabel);
        bioview.setFullName(newtName);
        bioview.setTaxId(taxid);

        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }

    /**
     * Validates the tax id.
     * @param user the handler to user to search the database.
     * @param taxid the tax id to search in the database for.
     * @param request the HTTP request to save errors
     * @return <code>false</code> if the search fails or a BioSource
     * instance found  with the same <code>taxid</code>.
     */
    private boolean validateTaxId(EditUserI user, String taxid,
                                  HttpServletRequest request) {
        // Holds the results from the search.
        Collection results;
        try {
            results = user.search(BioSource.class.getName(), "taxId", taxid);
        }
        catch (SearchException se) {
            // Can't query the database.
            super.log(ExceptionUtils.getStackTrace(se));
            ActionErrors errors = new ActionErrors();
            errors.add("cvinfo", new ActionError("error.search",
                    "Unable to search the database to check for unique tax ids"));
            super.saveErrors(request, errors);
            return false;
        }
        // result is not empty if we have this taxid on the database.
        if (!results.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add("cvinfo", new ActionError("error.newt.taxid", taxid));
            super.saveErrors(request, errors);
            return false;
        }
        return true;
    }

    private String getNewtResponse(URL url, String query) throws IOException {
        URLConnection servletConnection = url.openConnection();
        // Turn off caching
        servletConnection.setUseCaches(false);

        // Wrting to the server.
        servletConnection.setDoOutput(true);

        // Write the taxid to the server.
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(servletConnection.getOutputStream()));
            // Send the query and flush it.
            writer.write(query);
            writer.flush();
            writer.close();
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException ioe) {
                }
            }
        }
        // The reader to read response from the server.
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(servletConnection.getInputStream()));
            // We are expcting a single line from the server.
            return reader.readLine();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ioe) {
                }
            }
        }
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
     * @throws uk.ac.ebi.intact.persistence.SearchException
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
            throws DuplicateLabelException {
        // The owner of the object we are editing.
        Institution owner = user.getInstitution();

        // The database the new xref belong to.
        CvDatabase db = (CvDatabase) user.getObjectByLabel(
                CvDatabase.class, TAX_DB);

        return  new Xref(owner, db, taxid, label, null, null);
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
        ArrayList xrefs = bioview.getXrefs();
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
