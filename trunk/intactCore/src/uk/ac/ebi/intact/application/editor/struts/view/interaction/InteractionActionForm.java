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
import org.apache.struts.util.MessageResources;
import org.apache.struts.Globals;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * The form to edit bio experiment data.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id: InteractionActionForm.java,v 1.5 2004/06/15 16:03:02 smudali
 * Exp $
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

    /**
     * List of checked link items.
     */
    //    private List myLinkedItems;
    /**
     * Stores the feature dispatch action (to determine what course of action to
     * take when Edit/Delete Feature button selected).
     */
    private String myFeatureDispatch;

    /**
     * The AC of the selected feature.
     */
    private String mySelectedFeatureAc;

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

    public void setComponents(List comps) {
        if (myComponents != null) {
            // No need to create a new proteins if both collections contain
            // same.
            // This might be the case for page refresh for other than proteins.
            if (CollectionUtils.isEqualCollection(myComponents, comps)) {
                Logger.getLogger(EditorConstants.LOGGER).debug(
                        "THEY ARE EQUAL collections");
                return;
            }
        }
        Logger.getLogger(EditorConstants.LOGGER)
                .debug("Setting the components");
        // Clear previous components.
//        myComponents.clear();
//        // Create new components or form will share the objects with the bean.
//        for (Iterator iter = comps.iterator(); iter.hasNext();) {
//            ComponentBean cb = (ComponentBean) iter.next();
//            myComponents.add(cb);
//            myComponents.
//        }
        myComponents = new ArrayList(comps);
    }

    public List getComponents() {
        //        System.out.println("getting proteins");
        return myComponents;
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

    public ComponentBean getSelectedComponent() {
        return (ComponentBean) myComponents.get(getDispatchIndex());
    }

    public String getDispatchFeature() {
        return myFeatureDispatch;
    }

    public void setDispatchFeature(String dispatch) {
        myFeatureDispatch = dispatch;
    }

    public void resetDispatchFeature() {
        myFeatureDispatch = "";
    }

//    public void setSelectedFeatureAc(String ac) {
//        mySelectedFeatureAc = ac;
//    }

//    public String getSelectedFeatureAc() {
//        return mySelectedFeatureAc;
//    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        if (myComponents == null) {
            return;
        }
        for (Iterator iterator = myComponents.iterator(); iterator.hasNext();) {
            ComponentBean compBean = (ComponentBean) iterator.next();
            for (Iterator iterator1 = compBean.getFeatures().iterator(); iterator1
                    .hasNext();) {
                FeatureBean featureBean = (FeatureBean) iterator1.next();
                featureBean.setLinked(false);//"off");
            }
        }
    }

    /**
     * Returns an array that contains two selected (checkboxes) Feature beans.
     * This method assumes that {@link #validate(ActionMapping, HttpServletRequest)}
     * has been called prior to calling this method because it returns the first
     * two selected features (even when the user has incorrcetly selected more
     * than two features).
     * 
     * @return an array containing two selected features beans. The array contains
     * null items if none were selected.
     *
     * @see #validate(ActionMapping, HttpServletRequest)
     *
     * <pre>
     * pre: validate(ActionMapping, HttpServletRequest)
     * <pre>
     */
    public FeatureBean[] getFeaturesForLink() {
        // The two feature beans to return.
        FeatureBean[] fbs = new FeatureBean[2];

        // For array indexing.
        int idx = 0;

        // Loop through components until we found two items.
        for (Iterator iter0 = getComponents().iterator(); iter0.hasNext()
                && fbs[1] == null;) {
            ComponentBean compBean = (ComponentBean) iter0.next();
            for (Iterator iter1 = compBean.getFeatures().iterator(); iter1
                    .hasNext()
                    && fbs[1] == null;) {
                FeatureBean featureBean = (FeatureBean) iter1.next();
                if (featureBean.isLinked()) {
                    fbs[idx] = featureBean;
                    ++idx;
                }
            }
        }
        return fbs;
    }

    /**
     * Returns the selected feature for selecting a checkbox. This method
     * asseumns that {@link #validate(ActionMapping, HttpServletRequest)}has
     * been called prior to calling this method because it returns the first
     * selected feature (even when the user has incorrcetly selected more than
     * one feature).
     *
     * @return the selected feature or null if none was selected.
     */
    public FeatureBean getFeatureForUnlink() {
        // Loop till we found the selected feature.
        for (Iterator iter0 = getComponents().iterator(); iter0.hasNext();) {
            ComponentBean compBean = (ComponentBean) iter0.next();
            for (Iterator iter1 = compBean.getFeatures().iterator();
                 iter1.hasNext();) {
                FeatureBean fb = (FeatureBean) iter1.next();
                if (fb.isLinked()) {
                    return fb;
                }
            }
        }
        // None found so far, return null.
        return null;
    }

    /**
     * Returns the selected feature bean by way of selecting edit/delete feature
     * buttons.
     * @return the selected feature bean. <code>null</code> is returned
     * if a Feature wasn't selected.
     */
    public FeatureBean getSelectedFeature() {
        for (Iterator iter1 = myComponents.iterator(); iter1.hasNext();) {
            ComponentBean cb = (ComponentBean) iter1.next();
            for (Iterator iter2 = cb.getFeatures().iterator(); iter2.hasNext();) {
                FeatureBean fb = (FeatureBean) iter2.next();
                if (fb.isSelected()) {
                    return fb;
                }
            }
        }
        return null;
    }

//    public void updateFeature(FeatureBean fb) {
//        for (Iterator iter = myComponents.iterator(); iter.hasNext();) {
//            ComponentBean cb = (ComponentBean) iter.next();
//            List features = cb.getFeatures();
//            if (features.contains(fb)) {
//                int idx = features.indexOf(fb);
//                features.remove(idx);
//                features.add(idx, fb);
//                break;
//            }
//        }
//    }

    //    public String getLinked() {
    //        return "true";
    //    }
    //
    //    public void setLinked(int index, String value) {
    //        System.out.println("Index: " + index + " value: " + value);
    //    }
    //
    //    public ComponentBean getProteins(int idx) {
    //        System.out.println("Getting the protein at: " + idx);
    //        return (ComponentBean) myComponents.get(idx);
    //    }
    //
    //    public void setProteins(int idx) {
    //        System.out.println("Setting the protein at: " + idx);
    //    }
    //
    //    public FeatureBean getFeatures(int x) {
    //        System.out.println("in get features");
    //        return null;
    //    }
    //    public void setFeatureCmd(int index, int y, String value) {
    //        System.out.println("Received in the interaction action form: " + index +
    // " value: " + value);
    //    }
    //
    //    public void setFeatureCmd(String value) {
    //        System.out.println("Received in the interaction action form: " + value);
    //    }
    //
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
                if (fb.isLinked()) {
                    ++count;
                }
            }
        }
        if (count != 2) {
            errors = new ActionErrors();
            errors.add("feature.link",
                    new ActionError("error.int.feature.link"));
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
                if (fb.isLinked()) {
                    ++count;
                }
            }
        }
        if (count != 1) {
            errors = new ActionErrors();
            errors.add("feature.link",
                    new ActionError("error.int.feature.unlink"));
        }
        return errors;
    }
}