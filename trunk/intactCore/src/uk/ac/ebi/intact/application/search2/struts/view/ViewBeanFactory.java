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

import uk.ac.ebi.intact.application.search2.struts.view.details.BinaryDetailsViewBean;
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
    private static Map ourBeanToDetailsView = new HashMap ();

    /**
     * Mapping related to the single object view
     */
    private static Map ourBeanToSingleItemView = new HashMap ();

    /**
     * Maps: Model class -> binary view bean
     */
    private static Map ourBeanToBinaryView = new HashMap ();

    // Stores the beans to create in a map. Make sure to update HtmlBuilder when you
    // add a new view bean.
    static {
        // Details view beans.
        ourBeanToDetailsView.put ( Experiment.class, DetailsViewBean.class );
        ourBeanToDetailsView.put ( Interaction.class, InteractionDetailsViewBean.class );
        ourBeanToDetailsView.put ( Protein.class, ProteinDetailsViewBean.class );

        // Single view bean.
        ourBeanToSingleItemView.put ( Experiment.class, ExperimentSingleViewBean.class );
        ourBeanToSingleItemView.put ( Interaction.class, InteractionSingleViewBean.class );
        ourBeanToSingleItemView.put ( Protein.class, ProteinSingleViewBean.class );
        ourBeanToSingleItemView.put ( CvDatabase.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvXrefQualifier.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvTopic.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvInteraction.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvInteractionType.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvComponentRole.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvIdentification.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( BioSource.class, SingleViewBean.class );

        // Binary views.
        ourBeanToBinaryView.put ( Protein.class, BinaryDetailsViewBean.class );
    }


    ///////////////////////////
    // Instanciation methods
    ///////////////////////////

    // Made it private to stop from instantiating this class.
    private ViewBeanFactory () {
    }

    /**
     * Returns the only instance of this class.
     * @return the only instance of this class; always non null value is returned.
     */
    public synchronized static ViewBeanFactory getInstance () {
        if ( ourInstance == null ) {
            ourInstance = new ViewBeanFactory ();
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
    public AbstractViewBean getBinaryViewBean ( Collection objects, String link ) {

        Object firstItem = objects.iterator ().next ();
        Class objsClass = firstItem.getClass ();

        System.out.println ( objsClass );

        Class clazz = (Class) ourBeanToBinaryView.get ( objsClass );
        return getViewBean( clazz, objects, link);
    }


    /**
     * Returns the appropriate view bean for given <code>Collection<code> object.
     * @param objects the <code>Collection</code> of objects to return the view for.
     * @param link the link to help page.
     * @return the appropriate view for <code>object</code>; null is
     * returned if there is no mapping or an error in creating an
     * instance of the view.
     */
    public AbstractViewBean getDetailsViewBean ( Collection objects, String link ) {

        Object firstItem = objects.iterator().next ();
        Class objsClass = firstItem.getClass ();

        System.out.println ( objsClass );

        Class clazz = (Class) ourBeanToDetailsView.get ( objsClass );
        return getViewBean( clazz, objects, link );
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
    public AbstractViewBean getSingleViewBean ( AnnotatedObject object, String link ) {

        System.out.println ( object.getClass () );
        Class beanClass = (Class) ourBeanToSingleItemView.get ( object.getClass () );

        return getViewBean( beanClass, object, link );
    }


    /**
     * Returns the appropriate view bean for given object.
     * The object can be either a <code>Collection</code> or an
     * <code>AnnotatedObject</code>.
     *
     * @param beanClazz
     * @param objectToWrap
     * @param link the link to help page.
     * @return the appropriate view for <code>object</code>; null is
     * returned if there is no mapping or an error in creating an
     * instance of the view.
     */
    private AbstractViewBean getViewBean ( Class beanClazz,
                                           Object objectToWrap,
                                           String link ) {

        if (beanClazz == null) {
            return null;
        }

        try {
            Class classToWrap = null;

            /* TODO: would be nice to get rid of it
             * If an Experiment (ArrayList) is given, it's not automatically
             * casted to AnnotatedObject (Collection) as required in the constructor
             * of the Bean.
             * So we get a NoSuchMethodException.
             */
            if ( objectToWrap instanceof AnnotatedObject ) {
                classToWrap = AnnotatedObject.class;
            } else {
                classToWrap = Collection.class;
            }

            System.out.println ( "CLassToWrap affected to: " + classToWrap);

            System.out.println ( "Ask constructor to: " + beanClazz.getName());
            System.out.println ( "Param1: " + classToWrap.getName() );
            System.out.println ( "Param2: " + String.class.getName() );

            Constructor constructor = beanClazz.getConstructor (
                    new Class[]{ classToWrap, String.class } );

            return (AbstractViewBean) constructor.newInstance (
                    new Object[]{ objectToWrap, link } );
        } catch ( InstantiationException e ) {
            e.printStackTrace ();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace ();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace ();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace ();
        }
        return null;
    }
}