/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * The form to edit bio experiment data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentActionForm extends EditorActionForm {

    private String myInteractionAC;
    private String myOragnism;
    private String myInter;
    private String myIdent;
    private String myIntSearchLabel;
    private String myIntSearchAC;

    public void setIntac(String intac) {
        myInteractionAC = intac;
    }

    public String getIntac() {
        return myInteractionAC;
    }

    public void setOrganism(String organism) {
        myOragnism = organism;
    }

    public String getOrganism() {
        return myOragnism;
    }

    public void setInter(String inter) {
        myInter = inter;
    }

    public String getInter() {
        return myInter;
    }

    public void setIdent(String ident) {
        myIdent = ident;
    }

    public String getIdent() {
        return myIdent;
    }

    public void setIntSearchLabel(String label) {
        myIntSearchLabel = label;
    }

    public String getIntSearchLabel() {
        return myIntSearchLabel;
    }

    public void setIntSearchAC(String ac) {
        myIntSearchAC = ac;
    }

    public String getIntSearchAC() {
        return myIntSearchAC;
    }

    /**
     * Validate the properties that have been set from the HTTP request.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     * no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        // Only proceed if super method does not find any errors.
        if ((errors != null) && !errors.isEmpty()) {
            return errors;
        }
        // Must select from the drop down list.
        if (getOrganism().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors = new ActionErrors();
            errors.add("exp.biosrc", new ActionError("error.exp.biosrc"));
        }
        else if (getInter().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors = new ActionErrors();
            errors.add("exp.inter", new ActionError("error.exp.inter"));
        }
        else if (getIdent().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors = new ActionErrors();
            errors.add("exp.ident", new ActionError("error.exp.ident"));
        }
        return errors;
    }
}
