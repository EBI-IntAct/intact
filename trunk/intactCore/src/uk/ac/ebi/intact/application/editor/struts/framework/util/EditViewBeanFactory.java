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

    /**
     * Returns a view bean constructed from given class.
     * @param clazz the Class to determine the view bean to retun.
     * @return a view bean from the cache is returned for an existing
     * view or a new view bean is returned (cached). CV view bean is returned
     * for an unknown <code>clazz</code> (other than Experiment, Interaction
     * and  BioSource).
     */
    public AbstractEditViewBean factory(Class clazz) {
        if (myNameToView.containsKey(clazz)) {
            return (AbstractEditViewBean) myNameToView.get(clazz);
        }
        // Create a a new view put it in the cache.
        AbstractEditViewBean viewbean;
        if (BioSource.class.isAssignableFrom(clazz)) {
            viewbean = new BioSourceViewBean();
        }
        else if (Experiment.class.isAssignableFrom(clazz)) {
            viewbean = new ExperimentViewBean();
        }
        else if (Interaction.class.isAssignableFrom(clazz)) {
            viewbean = new InteractionViewBean();
        }
        else {
            // Assume it is an CV object.
            viewbean = new CvViewBean();
        }
        // New view bean, set the factory and cache it.
        viewbean.setMenuFactory(myMenuFactory);
        myNameToView.put(clazz, viewbean);
        return viewbean;
    }
}
