/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import uk.ac.ebi.intact.application.editor.struts.view.biosrc.BioSourceViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.cv.CvViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * The factory class to create edit view beans.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditViewBeanFactory {

    /**
     * Maps: name -> views. Cache for view beans. It is safe to reuse the view
     *  beans as only one can be used at any time. We cache them to reuse
     *  rather than creating a new instance for each selection.
     */
    private Map myNameToView = new HashMap();

    /**
     * CV editor is stored under this key.
     */
//    private static final String CV_EDITOR = "0";
//
//    /**
//     * BioSource editor is stored under this key.
//     */
//    private static final String BIOSRC_EDITOR = "1";
//
//    /**
//     * Experiment editor is stored under this key.
//     */
//    private static final String EXPERIMENT_EDITOR = "2";
//
//    /**
//     * Interaction editor is stored under this key.
//     */
//    private static final String INTERACTION_EDITOR = "3";

    /**
     * The factory to create various menus (single menu factory for a user).
     */
    private EditorMenuFactory myMenuFactory;

    /**
     * Creates the factory with an instnce of the Intact helper.
     * @param helper the Intact helper to create the menu factory.
     */
    public EditViewBeanFactory(IntactHelper helper) {
        myMenuFactory = new EditorMenuFactory(helper);
    }

    public AbstractEditViewBean factory(Class clazz) {
        if (myNameToView.containsKey(clazz)) {
            return (AbstractEditViewBean) myNameToView.get(clazz);
        }
        // Create a a new view put it in the cache.
        AbstractEditViewBean viewbean;
        if (clazz.isAssignableFrom(BioSource.class)) {
            viewbean = new BioSourceViewBean();
        }
        else if (clazz.isAssignableFrom(Experiment.class)) {
            viewbean = new ExperimentViewBean();
        }
        else if (clazz.isAssignableFrom(Interaction.class)) {
            viewbean = new InteractionViewBean();
        }
        else {
            // Assume it is an CV object.
            viewbean = new CvViewBean();
        }
        viewbean.setMenuFactory(myMenuFactory);
        myNameToView.put(clazz, viewbean);
        return viewbean;

    }

    /**
     * Factory method to return an instance of an Edit view bean.
     * @param annot the Annotated object to initialize the edit view bean.
     * @return an instance of <code>AbstractEditViewBean</code>; the type to return
     * is determined by <code>annot</code>. This method does not create new
     * instances of beans; instead it recyles them.
     */
//    public AbstractEditViewBean factory(AnnotatedObject annot) {
//        // The view bean to return.
//        AbstractEditViewBean viewbean;
//        Class clazz = annot.getClass();
//        if (clazz.isAssignableFrom(BioSource.class)) {
//            viewbean = getBioSourceEditor((BioSource) annot);
//        }
//        else if (clazz.isAssignableFrom(Experiment.class)) {
//            viewbean = getExperimentEditor((Experiment) annot);
//        }
//        else if (clazz.isAssignableFrom(Interaction.class)) {
//            viewbean = getInteractionEditor((Interaction) annot);
//        }
//        else {
//            // Assume it is an CV object.
//            viewbean = getCvEditor((CvObject) annot);
//        }
//        return viewbean;
//    }

    // Helper methods.

//    private AbstractEditViewBean getBioSourceEditor(BioSource biosrc) {
//        String name = BIOSRC_EDITOR;
//        BioSourceViewBean view;
//        // Check the cache first.
//        if (myNameToView.containsKey(name)) {
//            view = (BioSourceViewBean) myNameToView.get(name);
//        }
//        else {
//            view = new BioSourceViewBean(myHelper);
////            view.setMenuFactory(myMenuFactory);
//            myNameToView.put(name, view);
//        }
//        view.setAnnotatedObject(biosrc);
//        return view;
//    }
//
//    private AbstractEditViewBean getExperimentEditor(Experiment exp) {
//        String name = EXPERIMENT_EDITOR;
//        ExperimentViewBean view;
//        // Check the cache first.
//        if (myNameToView.containsKey(name)) {
//            view = (ExperimentViewBean) myNameToView.get(name);
//        }
//        else {
//            view = new ExperimentViewBean(myHelper);
////            view.setMenuFactory(myMenuFactory);
//            myNameToView.put(name, view);
//        }
//        view.setAnnotatedObject(exp);
//        return view;
//    }
//
//    private AbstractEditViewBean getInteractionEditor(Interaction intact) {
//        String name = INTERACTION_EDITOR;
//        InteractionViewBean view;
//        // Check the cache first.
//        if (myNameToView.containsKey(name)) {
//            view = (InteractionViewBean) myNameToView.get(name);
//        }
//        else {
//            view = new InteractionViewBean(myHelper);
////            view.setMenuFactory(myMenuFactory);
//            myNameToView.put(name, view);
//        }
//        view.setAnnotatedObject(intact);
//        return view;
//    }
//
//    private AbstractEditViewBean getCvEditor(CvObject cvobj) {
//        String name = CV_EDITOR;
//        CvViewBean view;
//        // Check the cache first.
//        if (myNameToView.containsKey(name)) {
//            view = (CvViewBean) myNameToView.get(name);
//        }
//        else {
//            view = new CvViewBean(myHelper);
////            view.setMenuFactory(myMenuFactory);
//            myNameToView.put(name, view);
//        }
//        view.setAnnotatedObject(cvobj);
//        return view;
//    }
}
