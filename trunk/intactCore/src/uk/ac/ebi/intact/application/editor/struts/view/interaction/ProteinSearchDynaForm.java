/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.DynaValidatorForm;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The form to search for Proteins. Overrides the validate method to provide
 * validation check for empty input values.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinSearchDynaForm extends DynaValidatorForm {

    /**
     * Must start with either o or P or Q, followed by 5 characters consists of
     * either A - Z or 0 - 9.
     */
    private static final Pattern ourSpAcPat = Pattern.compile("[O|P|Q][A-Z0-9]{5}$");

    /**
     * Must start with either uppercase characters, followed by '-' and a number.
     */
    private static final Pattern ourIntactAcPat = Pattern.compile("[A-Z]+\\-[0-9]+$");

    /**
     * Validates the search input values.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     * no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        String ac = (String) get("ac");
        String spAc = (String) get("spAc");
        String shortLabel = (String) get("shortLabel");

        // Cache string lengths.
        int acLen = ac.length();
        int spAcLen = spAc.length();
        int shortLabelLen = shortLabel.length();

        // Error if all three fields are empty.
        if ((acLen == 0) && (spAcLen == 0) && (shortLabelLen == 0)) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.protein.search.input"));
            return errors;
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
                return errors;
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
                return errors;
            }
            value = spAc;
            param = "spAc";
        }
        set("param", param);
        set("value", value);
        return null;
    }
}