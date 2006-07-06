/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.struts.view.sm;

import uk.ac.ebi.intact.model.SmallMolecule;
import uk.ac.ebi.intact.model.CvInteractorType;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.SmallMoleculeImpl;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.util.IntactHelperUtil;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import org.apache.log4j.Logger;
import org.apache.struts.tiles.ComponentContext;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Small molecule edit view bean.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class SmallMoleculeViewBean extends AbstractEditViewBean<SmallMolecule>  {
    protected static final Logger LOGGER = Logger.getLogger(EditorConstants.LOGGER);

    /**
     * The map of menus for this view.
     */
    private transient Map<String, List<String>> myMenus = new HashMap<String, List<String>>();

    /**
     * Override to provide the menus for this view.
     * @return a map of menus for this view. It consists of common menus for
     * annotation/xref.
     */
    @Override
    public Map<String, List<String>> getMenus() throws IntactException {
        return myMenus;
    }

    // --------------------- Protected Methods ---------------------------------

    // Implements abstract methods
    @Override
    protected void updateAnnotatedObject(IntactHelper helper) throws IntactException {
        // The current small molecule object.
        SmallMolecule sm = getAnnotatedObject();

        // Have we set the annotated object for the view?
        if (sm == null) {
            // Not persisted; create a new small Molecule object.
            try {
                CvInteractorType smInteractorType = getSmallMoleculeInteractorType();
                if( smInteractorType == null){

                    Logger.getLogger(EditorConstants.LOGGER).error("Could not find the cvInteractorType "
                        + CvInteractorType.SMALL_MOLECULE + " using it's psi-mi id : "
                        + CvInteractorType.SMALL_MOLECULE_MI_REF );
                        throw new IntactException("Could not find the cvInteractorType " + CvInteractorType.SMALL_MOLECULE
                        + " using it's psi-mi id : " + CvInteractorType.SMALL_MOLECULE_MI_REF );

                }
                Constructor ctr = getEditClass().getDeclaredConstructor(
                        String.class, Institution.class, CvInteractorType.class);
                sm = (SmallMoleculeImpl) ctr.newInstance(
                        getShortLabel(),getService().getOwner(), smInteractorType );
            }
            catch (NoSuchMethodException ne) {
                // Shouldn't happen.
                Logger.getLogger(EditorConstants.LOGGER).error("", new IntactException(ne.getMessage()));
                throw new IntactException(ne.getMessage());
            }
            catch (SecurityException se) {
                Logger.getLogger(EditorConstants.LOGGER).error("", new IntactException(se.getMessage()) );
                throw new IntactException(se.getMessage());
            }
            catch (InstantiationException ie) {
                Logger.getLogger(EditorConstants.LOGGER).error("", new IntactException(ie.getMessage()) );
                throw new IntactException(ie.getMessage());
            }
            catch (IllegalAccessException le) {
                Logger.getLogger(EditorConstants.LOGGER).error("", new IntactException(le.getMessage()) );
                throw new IntactException(le.getMessage());
            }
            catch (InvocationTargetException te) {
                Logger.getLogger(EditorConstants.LOGGER).error("", new IntactException(te.getMessage()) );
                throw new IntactException(te.getMessage());
            }
            CvInteractorType smInteractorType = getSmallMoleculeInteractorType();
            if( smInteractorType != null){
                sm.setCvInteractorType(smInteractorType);
            }else{
                Logger.getLogger(EditorConstants.LOGGER).error("Could not find the cvInteractorType "
                        + CvInteractorType.SMALL_MOLECULE + " using it's psi-mi id : "
                        + CvInteractorType.SMALL_MOLECULE_MI_REF );
                throw new IntactException("Could not find the cvInteractorType " + CvInteractorType.SMALL_MOLECULE
                        + " using it's psi-mi id : " + CvInteractorType.SMALL_MOLECULE_MI_REF );

            }
            setAnnotatedObject(sm);
        }
    }

    /**
     * Get the small molecule interactorType having the psi-mi id MI:0328.
     * @return the small molecule cvInteractorType.
     */
    private static CvInteractorType getSmallMoleculeInteractorType(){
        IntactHelper helper = IntactHelperUtil.getIntactHelper();
        CvInteractorType smallMolecule = helper.getObjectByPrimaryId(CvInteractorType.class,
                                                                     CvInteractorType.SMALL_MOLECULE_MI_REF);
        return smallMolecule;
    }



    /**
     * Override to load the menus for this view.
     */
    @Override
    public void loadMenus() throws IntactException {
        myMenus.clear();


        //LOGGER.info("help tag : " + this.getHelpTag());
       myMenus = super.getMenus();
//        myMenus = super.getMenus(CvObject.class.getName());//EditorMenuFactory.TOPIC);
    }

     // Override to provide Experiment layout.
    @Override
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.sm.layout");
    }
}