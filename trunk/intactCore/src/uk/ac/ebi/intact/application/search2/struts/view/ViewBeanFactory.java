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

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.search2.business.Constants;
import uk.ac.ebi.intact.application.search2.struts.view.details.BinaryDetailsViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.details.DetailsViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.details.InteractionDetailsViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.details.ProteinDetailsViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.ExperimentSingleViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.InteractionSingleViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.ProteinSingleViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.SingleViewBean;
import uk.ac.ebi.intact.application.search2.struts.view.single.chunked.ExperimentChunkedSingleViewBean;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.proxy.InteractionProxy;
import uk.ac.ebi.intact.model.proxy.ProteinProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ViewBeanFactory {

    private transient static final Logger logger = Logger.getLogger( Constants.LOGGER_NAME );

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

    /**
     * Maps: Model class -> chunked view bean
     */
    private static Map ourBeanToChunkedView = new HashMap ();

    // Stores the beans to create in a map. Make sure to update HtmlBuilder when you
    // add a new view bean.
    static {
        // Details view beans.
        ourBeanToDetailsView.put ( Experiment.class, DetailsViewBean.class );
//        ourBeanToDetailsView.put ( Interaction.class, InteractionDetailsViewBean.class );
//        ourBeanToDetailsView.put ( Protein.class, ProteinDetailsViewBean.class );
//        ourBeanToDetailsView.put ( InteractionProxy.class, InteractionDetailsViewBean.class );
//        ourBeanToDetailsView.put ( ProteinProxy.class, ProteinDetailsViewBean.class );
        ourBeanToDetailsView.put ( InteractionImpl.class, InteractionDetailsViewBean.class );
        ourBeanToDetailsView.put ( ProteinImpl.class, ProteinDetailsViewBean.class );

        // Single view bean.
        ourBeanToSingleItemView.put ( Experiment.class, ExperimentSingleViewBean.class );
//        ourBeanToSingleItemView.put ( Interaction.class, InteractionSingleViewBean.class );
//        ourBeanToSingleItemView.put ( Protein.class, ProteinSingleViewBean.class );
//        ourBeanToSingleItemView.put ( InteractionProxy.class, InteractionSingleViewBean.class );
//        ourBeanToSingleItemView.put ( ProteinProxy.class, ProteinSingleViewBean.class );
        ourBeanToSingleItemView.put ( InteractionImpl.class, InteractionSingleViewBean.class );
        ourBeanToSingleItemView.put ( ProteinImpl.class, ProteinSingleViewBean.class );

        ourBeanToSingleItemView.put ( CvDatabase.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvXrefQualifier.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvTopic.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvInteraction.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvInteractionType.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvComponentRole.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( CvIdentification.class, SingleViewBean.class );
        ourBeanToSingleItemView.put ( BioSource.class, SingleViewBean.class );

        // Binary views.
//        ourBeanToBinaryView.put ( Protein.class, BinaryDetailsViewBean.class );
//        ourBeanToBinaryView.put ( ProteinProxy.class, BinaryDetailsViewBean.class );
        ourBeanToBinaryView.put ( ProteinImpl.class, BinaryDetailsViewBean.class );

        // chunked view
        ourBeanToChunkedView.put( Experiment.class, ExperimentChunkedSingleViewBean.class );
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
    public AbstractViewBean getBinaryViewBean ( Collection objects, String link, String contextPath ) {

        Object firstItem = objects.iterator ().next ();
        Class objsClass = firstItem.getClass ();

        logger.info ( objsClass );

        Class clazz = (Class) ourBeanToBinaryView.get ( objsClass );
        return getViewBean( clazz, objects, link, contextPath);
    }


    /**
     * Returns the appropriate view bean for given <code>Collection<code> object.
     * @param objects the <code>Collection</code> of objects to return the view for.
     * @param link the link to help page.
     * @return the appropriate view for <code>object</code>; null is
     * returned if there is no mapping or an error in creating an
     * instance of the view.
     */
    public AbstractViewBean getDetailsViewBean ( Collection objects, String link, String contextPath ) {

        Object firstItem = objects.iterator().next ();
        Class objsClass = firstItem.getClass ();

        logger.info ( objsClass );

        Class clazz = (Class) ourBeanToDetailsView.get ( objsClass );
        return getViewBean( clazz, objects, link, contextPath );
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
    public AbstractViewBean getSingleViewBean ( AnnotatedObject object, String link, String contextPath ) {

        logger.info ( object.getClass () );
        Class beanClass = (Class) ourBeanToSingleItemView.get ( object.getClass () );

        return getViewBean( beanClass, object, link, contextPath );
    }


    /**
     *
     * @param object
     * @param link
     * @param contextPath
     * @return
     */
    public AbstractViewBean getChunkedSingleViewBean ( AnnotatedObject object, String link, String contextPath, int maxChunk, int selectedChunk ) {

        logger.info ( object.getClass () );
        Class beanClass = (Class) ourBeanToChunkedView.get ( object.getClass () );

        return getViewBean( beanClass, object, link, contextPath, maxChunk, selectedChunk );
    }

    /**
     * Returns the appropriate view bean for given object.
     * The object can be either a <code>Collection</code> or an
     * <code>AnnotatedObject</code>.
     *
     * @param beanClazz the type of the bean which will wrap the object to display
     * @param objectToWrap the object to display
     * @param link the link to help page.
     * @param contextPath the context path of the appliction
     * @param maxChunk the count of displayable chunk for that bean
     * @param selectedChunk the chunk we are going to display (0 <= selectedChunk < maxChunk)

     * @return the appropriate view for <code>object</code>; null is
     * returned if there is no mapping or an error in creating an
     * instance of the view.
     *
     * @return
     */
    private AbstractViewBean getViewBean ( Class beanClazz,
                                           AnnotatedObject objectToWrap,
                                           String link,
                                           String contextPath,
                                           int maxChunk,
                                           int selectedChunk ) {

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

            logger.info ( "ClassToWrap affected to: " + classToWrap);

            logger.info ( "Ask constructor to: " + beanClazz.getName());
            logger.info ( "Param1: " + Class.class.getName()   + " value: " + objectToWrap.getClass().getName() );
            logger.info ( "Param2: " + String.class.getName()  + " value: " + link );
            logger.info ( "Param3: " + String.class.getName()  + " value: " + contextPath );
            logger.info ( "Param4: " + Integer.class.getName() + " value: " + maxChunk );
            logger.info ( "Param5: " + Integer.class.getName() + " value: " + selectedChunk );

            Constructor constructor = beanClazz.getConstructor (
                    new Class[]{ classToWrap, String.class, String.class, Integer.class, Integer.class } );

            return (AbstractViewBean) constructor.newInstance (
                    new Object[]{ objectToWrap, link, contextPath, new Integer( maxChunk ), new Integer( selectedChunk ) } );
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


    /**
     * Returns the appropriate view bean for given object.
     * The object can be either a <code>Collection</code> or an
     * <code>AnnotatedObject</code>.
     *
     * @param beanClazz the type of the bean which will wrap the object to display
     * @param objectToWrap the object to display
     * @param link the link to help page.
     *
     * @return the appropriate view for <code>object</code>; null is
     * returned if there is no mapping or an error in creating an
     * instance of the view.
     */
    private AbstractViewBean getViewBean ( Class beanClazz,
                                           Object objectToWrap,
                                           String link,
                                           String contextPath ) {

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

            logger.info ( "ClassToWrap affected to: " + classToWrap);

            logger.info ( "Ask constructor to: " + beanClazz.getName());
            logger.info ( "Param1: " + classToWrap.getName() + " value: " + objectToWrap );
            logger.info ( "Param2: " + String.class.getName() + " value: " + link );
            logger.info ( "Param3: " + String.class.getName() + " value: " + contextPath );

            Constructor constructor = beanClazz.getConstructor (
                    new Class[]{ classToWrap, String.class, String.class } );

            return (AbstractViewBean) constructor.newInstance (
                    new Object[]{ objectToWrap, link, contextPath } );
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