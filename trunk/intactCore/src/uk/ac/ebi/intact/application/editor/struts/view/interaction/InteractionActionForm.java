/*
 Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

/**
 * The Interaction form.
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
    private List myComponents;

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
        myExperiments = exps;
    }

    public List getExperiments() {
        return myExperiments;
    }

    public void setComponents(List comps) {
        myComponents = comps;
    }

    public List getComponents() {
        return myComponents;
    }

    public void setExpsOnHold(List exps) {
        myExpsOnHold = exps;
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

    public ComponentBean getSelectedComponent() {
        return (ComponentBean) myComponents.get(getDispatchIndex());
    }

    // There is no need for a method to set protein dispatch because it
    // is already done via protCmd method.
    
    public void setDispatchFeature(String dispatch) {
        // Only set it if not error (defualt)
        if (!dispatch.equals("error")) {
            setDispatch(dispatch);
        }
    }

    /**
     * Validates Interaction info page.
     * 
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors.
     * If no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        // Only proceed if super method does not find any errors.
        if ((errors != null) && !errors.isEmpty()) {
            return errors;
        }

        // The dispatch parameter to find out which button was pressed.
        String dispatch = getDispatch();

        if (dispatch != null) {
            // Message resources to access button labels.
            MessageResources msgres = ((MessageResources) request
                    .getAttribute(Globals.MESSAGES_KEY));

            // Trap errors for linking two features.
            if (dispatch.equals(msgres.getMessage(
                    "int.proteins.button.feature.link"))) {
                errors = validateLinkTwoFeatures();
            }
            else if (dispatch.equals(msgres.getMessage(
                    "int.proteins.button.feature.unlink"))) {
                errors = validateUnlinkFeature();
            }
        }
        return errors;
    }

    /**
     * Validates the form for when Link Selected Features button was selected.
     * 
     * @return errors if two features not selected (exactly). A null is returned
     * if there no errors.
     */
    private ActionErrors validateLinkTwoFeatures() {
        ActionErrors errors = null;
        int count = 0;
        for (Iterator iter0 = getComponents().iterator(); iter0.hasNext()
                && count <= 2;) {
            ComponentBean cb = (ComponentBean) iter0.next();
            for (Iterator iter1 = cb.getFeatures().iterator(); iter1.hasNext()
                    && count <= 2;) {
                FeatureBean fb = (FeatureBean) iter1.next();
                if (fb.isChecked()) {
                    ++count;
                }
            }
        }
        if (count != 2) {
            errors = new ActionErrors();
            errors.add("feature.link", new ActionError("error.int.feature.link"));
        }
        return errors;
    }

    /**
     * Validates the form for when Unlink Selected Feature button was selected.
     *
     * @return errors if a single feature wasn't selected. A null is returned
     * if there no errors.
     */
    private ActionErrors validateUnlinkFeature() {
        ActionErrors errors = null;
        int count = 0;
        for (Iterator iter0 = getComponents().iterator(); iter0.hasNext()
                && count <= 1;) {
            ComponentBean cb = (ComponentBean) iter0.next();
            for (Iterator iter1 = cb.getFeatures().iterator(); iter1.hasNext()
                    && count <= 1;) {
                FeatureBean fb = (FeatureBean) iter1.next();
                if (fb.isChecked()) {
                    ++count;
                }
            }
        }
        if (count != 1) {
            errors = new ActionErrors();
            errors.add("feature.link", new ActionError("error.int.feature.unlink"));
        }
        return errors;
    }
}