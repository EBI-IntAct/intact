/*
 Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.log4j.Logger;

import uk.ac.ebi.intact.application.editor.struts.view.biosrc.BioSourceViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.cv.CvViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.sequence.SequenceViewBean;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.business.IntactException;

/**
 * The factory class to create edit view beans.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditViewBeanFactory implements KeyedPoolableObjectFactory {

    /**
     * Only instance of this class.
     */
    private static final EditViewBeanFactory ourInstance = new EditViewBeanFactory();

    /**
     * Handler to the pool.
     */
    private KeyedObjectPool myPool;

    // No instantiation from outside
    private EditViewBeanFactory() {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        config.maxActive = 10;
        config.whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_GROW;
        myPool = new GenericKeyedObjectPoolFactory(this, config).createPool();
    }

    /**
     * @return the only instance of this class.
     */
    public static EditViewBeanFactory getInstance() {
        return ourInstance;
    }
    
    /**
     * Gets a view from the pool 
     * @param key the key to get the view from the pool
     * @return the view for <code>key</code>.
     */
    public AbstractEditViewBean borrowObject(Class key) {
        try {
            return (AbstractEditViewBean) myPool.borrowObject(key);
        }
        catch (Exception ex) {
            Logger.getLogger(EditorConstants.LOGGER).error("", ex);
        }
        return null;
    }
    
    /**
     * Returns the view back to the pool.
     * @param view the view to return to.
     */
    public void returnObject(AbstractEditViewBean view) {
        System.out.println("Returning: " + view.getEditClass() + " and: " + view.getShortLabel());
        try {
            myPool.returnObject(view.getEditClass(), view);
        }
        catch (Exception ex) {
            Logger.getLogger(EditorConstants.LOGGER).error("", ex);
        }
//        System.out.println("Active pool size: " + myPool.getNumActive(view.getEditClass()));
//        System.out.println("Active pool size (total): " + myPool.getNumActive());
//        System.out.println("Idle pool size: " + myPool.getNumIdle(view.getEditClass()));
//        System.out.println("Idle pool size (total): " + myPool.getNumIdle());
    }

    // ------------------------------------------------------------------------

    // Implement KeyedPoolableObjectFactory methods

    public Object makeObject(Object key) throws Exception {
        // The bean to return.
        AbstractEditViewBean viewbean;

        // Class is the key.
        Class clazz = (Class) key;
//        System.out.println("Creating a view for " + clazz);

        if (BioSource.class.isAssignableFrom(clazz)) {
            viewbean = new BioSourceViewBean();
        }
        else if (Experiment.class.isAssignableFrom(clazz)) {
            viewbean = new ExperimentViewBean();
        }
        else if (Interaction.class.isAssignableFrom(clazz)) {
            viewbean = new InteractionViewBean();
        }
        else if (Feature.class.isAssignableFrom(clazz)) {
            viewbean = new FeatureViewBean();
        }
        else if (Protein.class.isAssignableFrom(clazz)) {
            viewbean = new SequenceViewBean();
        }
        else {
            // Assume it is an CV object.
            viewbean = new CvViewBean();
        }
        return viewbean;
    }

    public void destroyObject(Object key, Object obj) throws Exception {
    }

    public boolean validateObject(Object key, Object arg1) {
        return true;
    }

    public void activateObject(Object key, Object obj) throws Exception {
        try {
            ((AbstractEditViewBean) obj).loadMenus();
        }
        catch (IntactException ie) {
            Logger.getLogger(EditorConstants.LOGGER).error("", ie);
        }
    }

    public void passivateObject(Object key, Object obj) throws Exception {
        ((AbstractEditViewBean) obj).reset();
    }
}