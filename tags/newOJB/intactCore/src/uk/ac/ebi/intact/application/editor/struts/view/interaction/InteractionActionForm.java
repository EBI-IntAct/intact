/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * The form to edit bio experiment data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionActionForm extends EditorActionForm {

    /**
     * KD
     */
    private Float myKD;

    /**
     * Host organism.
     */
    private String myOragnism;

    /**
     * CV Interaction type.
     */
    private String myInteractionType;

    /**
     * The short label to search an experiment.
     */
    private String myExpSearchLabel;

    /**
     * The AC to search an experiment.
     */
    private String myExpSearchAC;

    /**
     * The short label to search a protein.
     */
    private String myProSearchLabel;

    /**
     * The SP AC to search a protein.
     */
    private String myProtSearchSpAC;

    /**
     * The AC to search a protein.
     */
    private String myProtSearchAC;

    /**
     * The list of experiments in the current interaction.
     */
    private List myExperiments;

    /**
     * The list of proteins in the current interaction.
     */
    private List myProteins;

    /**
     * The list of proteins on hold for the current interaction.
     */
    private List myExpsOnHold;

    // Setter / Getter methods.
    public void setKd(Float kd) {
        myKD = kd;
    }

    public Float getKd() {
        return myKD;
    }

    public void setOrganism(String organism) {
        myOragnism = organism;
    }

    public String getOrganism() {
        return myOragnism;
    }

    public void setInteractionType(String inter) {
        myInteractionType = inter;
    }

    public String getInteractionType() {
        return myInteractionType;
    }

    public void setExpSearchLabel(String label) {
        myExpSearchLabel = label;
    }

    public String getExpSearchLabel() {
        return myExpSearchLabel;
    }

    public void setExpSearchAC(String ac) {
        myExpSearchAC = ac;
    }

    public String getExpSearchAC() {
        return myExpSearchAC;
    }

    public void setProtSearchLabel(String label) {
        myProSearchLabel = label.trim();
    }

    public String getProtSearchLabel() {
        return myProSearchLabel;
    }

    public void setProtSearchSpAC(String spac) {
        myProtSearchSpAC = spac.trim();
    }

    public String getProtSearchSpAC() {
        return myProtSearchSpAC;
    }

    public void setProtSearchAC(String ac) {
        myProtSearchAC = ac.trim();
    }

    public String getProtSearchAC() {
        return myProtSearchAC;
    }

    public void setExperiments(List exps) {
        if (myExperiments != null) {
            // See the comment for setProteins(List)
            if (CollectionUtils.isEqualCollection(myExperiments, exps)) {
                return;
            }
        }
        myExperiments = new ArrayList(exps);
    }

    public List getExperiments() {
        return myExperiments;
    }

    public void setProteins(List proteins) {
        if (myProteins != null) {
            // No need to create a new proteins if both collections contain same.
            // This might be the case for page refresh for other than proteins.
            if (CollectionUtils.isEqualCollection(myProteins,  proteins)) {
                return;
            }
        }
        myProteins = new ArrayList(proteins);
    }

    public List getProteins() {
        return myProteins;
    }

    public void setExpsOnHold(List exps) {
        if (myExpsOnHold != null) {
            // See the comment for setProteins(List)
            if (CollectionUtils.isEqualCollection(myExpsOnHold, exps)) {
                return;
            }
        }
        myExpsOnHold = new ArrayList(exps);
    }

    public List getExpsOnHold() {
        return myExpsOnHold;
    }

    public void setExpCmd(int index, String value) {
        setDispatch(index, value);
    }

    public ExperimentBean getSelectedExperiment() {
        return (ExperimentBean) myExperiments.get(getDispatchIndex());
    }

    public void setExpOnHoldCmd(int index, String value) {
        setDispatch(index, value);
    }

    public ExperimentBean getSelectedExpOnHoldCmd() {
        return (ExperimentBean) myExpsOnHold.get(getDispatchIndex());
    }

    public void setProtCmd(int index, String value) {
        setDispatch(index, value);
    }

    public ProteinBean getSelectedProtein() {
        return (ProteinBean) myProteins.get(getDispatchIndex());
    }

    /**
     * Validates Interaction info page.
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
        if (getInteractionType().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors = new ActionErrors();
            errors.add("int.interaction", new ActionError("error.int.cvtype"));
            return errors;
        }
        if (getOrganism().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors = new ActionErrors();
            errors.add("int.organism", new ActionError("error.int.biosrc"));
            return errors;
        }
        // Must have at least one experiment. This is a business decision.
//        if (getExperiments().isEmpty()) {
//            errors = new ActionErrors();
//            errors.add(ActionErrors.GLOBAL_ERROR,
//                    new ActionError("error.int.validation.exp"));
//        }
        return errors;
    }
}
