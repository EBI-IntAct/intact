/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
/**
 * This class is a factory to create view bean instances. The beans to generate
 * are stored in a map with the mode class. For example, ExperimentDetailsViewBean is
 * stored under Experiment class.
 * <p>
 * This is a singleton class.
 * </p>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
package uk.ac.ebi.intact.application.search2.struts.view;

import uk.ac.ebi.intact.application.search2.struts.view.details.DetailsViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.details.InteractionDetailsViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.details.ProteinDetailsViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.ExperimentSingleViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.InteractionSingleViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.ProteinSingleViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.SingleViewBean;
import uk.ac.ebi.intact.model.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ViewBeanFactory {

    private static ViewBeanFactory ourInstance;

    ////////////////////////
    // Bean mappings
    ////////////////////////

    /**
     * Mapping related to the detailed view
     */
    private static Map ourBeanToDetailsView = new HashMap();

    // Stores the beans to create in a map.
    static {
        ourBeanToDetailsView.put( Experiment.class,  DetailsViewBean.class );
        ourBeanToDetailsView.put( Interaction.class, InteractionDetailsViewBean.class );
        ourBeanToDetailsView.put( Protein.class,     ProteinDetailsViewBean.class );
    }

    /**
     * Mapping related to the single object view
     */
    private static Map ourBeanToSingleItemView = new HashMap();

    // Stores the beans to create in a map.
    static {
        ourBeanToSingleItemView.put( Experiment.class,      ExperimentSingleViewBean.class );
        ourBeanToSingleItemView.put( Interaction.class,     InteractionSingleViewBean.class );
        ourBeanToSingleItemView.put( Protein.class,         ProteinSingleViewBean.class );
        ourBeanToSingleItemView.put( CvDatabase.class,      SingleViewBean.class );
        ourBeanToSingleItemView.put( CvXrefQualifier.class, SingleViewBean.class );
        ourBeanToSingleItemView.put( CvTopic.class,         SingleViewBean.class );
        ourBeanToSingleItemView.put( CvInteraction.class,   SingleViewBean.class );
        ourBeanToSingleItemView.put( CvComponentRole.class, SingleViewBean.class );
        ourBeanToSingleItemView.put( BioSource.class,       SingleViewBean.class );
    }



    ///////////////////////////
    // Instanciation methods
    ///////////////////////////

    // Made it private to stop from instantiating this class.
    private ViewBeanFactory() {
    }

    /**
     * Returns the only instance of this class.
     * @return the only instance of this class; always non null value is returned.
     */
    public synchronized static ViewBeanFactory getInstance() {
        if (ourInstance == null) {
            ourInstance = new ViewBeanFactory();
        }
        return ourInstance;
    }





    /**
     * Returns the appropriate view bean for given <code>Collection<code> object.
     * @param objects the <code>Collection</code> of objects to return the view for.
     * @param link the link to help page.
     * @return the appropriate view for <code>object</code>; null is
     * returned if there is no mapping or an error in creating an
     * instance of the view.
     */
    public AbstractViewBean getViewBean ( Collection objects, String link ) {

        if ( objects != null && !objects.isEmpty() ) {

            // TODO: check for heterogen collection ? Should not be necessary since we search for one type at a time.

            Object object = objects.iterator().next();

            System.out.println( object.getClass() );
            Class clazz = (Class) ourBeanToDetailsView.get( object.getClass() );
            if ( clazz != null ) {
                try {
                    Constructor constructor = clazz.getConstructor(
                            new Class[]{ Collection.class, String.class } );
                    return (AbstractViewBean) constructor.newInstance(
                            new Object[]{ objects, link } );
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * Returns the appropriate view bean for given basic object.
     *
     * @param object the <code>AnnotatedObject</code> to return the view for.
     * @param link the link to help page.
     * @return the appropriate view for <code>object</code>; null is
     * returned if there is no mapping or an error in creating an
     * instance of the view.
     */
    public AbstractViewBean getViewBean ( AnnotatedObject object, String link ) {

        if ( object != null ) {

            System.out.println( object.getClass() );
            Class clazz = (Class) ourBeanToSingleItemView.get( object.getClass() );
            if ( clazz != null ) {
                try {
                    Constructor constructor = clazz.getConstructor(
                            new Class[]{ AnnotatedObject.class, String.class } );
                    return (AbstractViewBean) constructor.newInstance(
                            new Object[]{ object, link } );
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}