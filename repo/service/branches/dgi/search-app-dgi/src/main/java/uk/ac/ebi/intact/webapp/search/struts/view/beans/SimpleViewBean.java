/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.webapp.search.struts.view.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.searchengine.SearchClass;
import uk.ac.ebi.intact.webapp.search.SearchWebappContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class provides JSP view information for a particular AnnotatedObject. Its main purpose is to provide very simple
 * beans for display in an initial search result page. Currenty the types that may be displayed with this bean are
 * Experiment, Protein, Interaction and CvObject.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class SimpleViewBean extends AbstractViewBean {
    
    private static final Log logger = LogFactory.getLog(SimpleViewBean.class);

    private static String EMPTY_SPACE = " ";

    /**
     * The AnnotatedObject (currently Experiment, Interaction, Protein or CvObject) that the view bean provides beans
     * for.
     */
    private AnnotatedObject obj;

    /**
     * Used for wrapped Experiments and Interactions. In the case of Experiments this holds the number of Interactions
     * it has, but for Interactions it contains the number of Proteins for the Interaction. In other cases (currently
     * CvObject and Protein) this should be an empty String (JSPs hate null objects :-)). It will be initialised to that
     * in the constructor.
     */
    private String relatedItemsSize = EMPTY_SPACE;

    /**
     * Cached search URL, set up on first request for it.
     */
    private String objSearchURL;

    /**
     * The intact type of the wrapped AnnotatedObject. Note that only the interface types are relevant for display
     * purposes - thus any concrete 'Impl' types will be considered to be their interface types in this case (eg a
     * wrapped ProteinImpl will have the intact type of 'Protein'). Would be nice to get rid of the proxies one day
     * ...:-)
     */
    private String intactType;

    /**
     * The bean constructor requires an AnnotatedObject to wrap, plus beans on the context path to the search
     * webapp and the help link. The object itself can be any one of Experiment, Protein, Interaction or CvObject
     * type.
     *
     * @param obj         The AnnotatedObject whose beans are to be displayed
     */
    public SimpleViewBean( AnnotatedObject obj ) {
        super( );
        this.obj = obj;
    }

    /**
     * Adds the shortLabel of the AnnotatedObject to an internal list used later for highlighting in a display. NOT SURE
     * IF WE STILL NEED THIS!!
     */
    @Override
    public void initHighlightMap() {
        Set<String> set = new HashSet<String>( 1 );
        set.add( obj.getShortLabel() );
        setHighlightMap( set );
    }


    /**
     * Returns a string  represents the url of the help section
     *
     * @return string contains the url link to the help section
     *
     * @deprecated use getHelpUrl instead
     */
    @Deprecated
    @Override
    public String getHelpSection() {
        return "protein.single.view";
    }

    /**
     * Returns a string  represents the url of the help section
     *
     * @return string contains the url link to the help section
     */
    public String getHelpUrl() {
        String helpUrl = this.getHelpLink();

        if ( Interactor.class.isAssignableFrom( obj.getClass() ) ) {
            return helpUrl + "Interactor";
        } else {
            if ( Experiment.class.isAssignableFrom( obj.getClass() ) ) {
                return helpUrl + "Experiment";
            } else {
                if ( Interaction.class.isAssignableFrom( obj.getClass() ) ) {
                    return helpUrl + "Interaction";
                } else {
                    if ( CvObject.class.isAssignableFrom( obj.getClass() ) ) {
                        return helpUrl + "CVS";
                    }
                }
            }
        }
        return helpUrl;
    }


    /**
     * The intact name for an object is its shortLabel. Required in all view types.
     *
     * @return String the object's Intact name.
     */
    public String getObjIntactName() {
        return obj.getShortLabel();
    }

    /**
     * The AnnotatedObject's AC. Required in all view types.
     *
     * @return String the AC of the wrapped object.
     */
    public String getObjAc() {
        return obj.getAc();
    }

    /**
     * This is currently assumed to be the AnnotatedObject's full name. Required by all view types.
     *
     * @return String a description of the AnnotatedObject, or a "-" if there is none.
     */
    public String getObjDescription() {
        if ( obj.getFullName() != null ) {
            return obj.getFullName();
        }
        return "-";
    }

    /**
     * Provides a String representation of the the Number of particapting interactions of a Protein
     *
     * @param anInteractor a Intact Interactor
     *
     * @return String a String representation of a s Number of particapting interactions
     */
//    public String getNumberOfInteractions( Protein aProtein ) {   //   1 usage in simple.jsp
    public String getNumberOfInteractions( Interactor anInteractor ) {

       return String.valueOf(
               getDaoFactory().getInteractorDao().countInteractionsForInteractorWithAc(anInteractor.getAc()));
    }

    /**
     * Provides a String representation of the the Number of particapting interactors of a Protein
     *
     * @param anInteractor a Intact Interactor
     *
     * @return String a String representation of a s Number of particapting interactors
     */
    public String getNumberOfInteractors( Interactor anInteractor ) {

       return String.valueOf(
               getDaoFactory().getProteinDao().countPartnersByProteinAc(anInteractor.getAc()));
    }


    /**
     * Provides a String representation of a URL to perform a search on this AnnotatedObject's beans (curently via AC)
     *
     * @return String a String representation of a search URL link for the wrapped AnnotatedObject
     */
    public String getObjSearchURL() {

        if ( objSearchURL == null ) {
            //set it on the first call
            //NB need to get the correct intact type of the wrapped object
            objSearchURL = SearchWebappContext.getCurrentInstance().getSearchUrl() + obj.getAc() + "&amp;searchClass=" + getIntactType();
        }
        return objSearchURL;
    }

    /**
     * Provides a String representation of a URL to perform a search on this AnnotatedObject's beans (curently via AC)
     *
     * @param clazz the class of the object we want to link to.
     * @param ac    the AC of the object we want to link to.
     *
     * @return String a String representation of a search URL link for the wrapped AnnotatedObject
     */
    public String getObjSearchURL( Class<? extends IntactObject> clazz, String ac ) {

        return SearchWebappContext.getCurrentInstance().getSearchUrl() + ac + "&amp;searchClass=" + getIntactType( clazz );
    }

    /**
     * Provides a String representation of the number of 'related items' for the wrapped AnnotatedObject. For an
     * Experiment this will be the number of Interactions it has; for an Interaction it will be the number of active
     * instances (Proteins) it has. In all other cases this method will return an empty String. NOTE: For Interactions
     * this will return the number of Components it has - this may need to become more sophisticated when Components
     * have Interactors other than single Proteins.
     *
     * @return String a String representation of the number of related items for an AnnotatedObject.
     */
    public String getRelatedItemsSize() {

        //set on first call
        if ( relatedItemsSize.equals(EMPTY_SPACE) ) {
            //obtain the number of its related 'items'...
            Class clazz = obj.getClass();

            if ( Experiment.class.isAssignableFrom( clazz ) ) {
                long size = getDaoFactory().getExperimentDao().countInteractionsForExperimentWithAc(getObjAc());
                logger.debug("Counting interactions for experiment with AC "+getObjAc()+": "+size);
                relatedItemsSize = String.valueOf( size );
            }

            //just check to be sure!!
            if ( Interaction.class.isAssignableFrom( clazz ) ) {
                //this should check for complexes also....
                int interactorCount = countInteractors(obj.getAc());
                relatedItemsSize = String.valueOf(interactorCount);
            }
        }
        return relatedItemsSize;

    }

    /**
     * Provides direct access to the wrapped AnnotatedObject itself.
     *
     * @return AnnotatedObject The reference to the wrapped object.
     */
    public AnnotatedObject getObject() {
        return obj;
    }

    /**
     * Return a protein's gene name.
     *
     * @param interactor the interactor for which we want the gene name.
     *
     * @return gene name of the given protein.
     */

//    public Collection getGeneNames( Protein protein ) {    //1 usage in simple.jsp
    public Collection<String> getGeneNames( Interactor interactor ) {
        return getDaoFactory().getInteractorDao().getGeneNamesByInteractorAc(interactor.getAc());
    }


    /**
     * Provides the basic Intact type of the wrapped AnnotatedObject (ie no java package beans). NOTE: only the
     * INTERFACE types are provided as these are the only ones of interest in the model - display pages are not
     * interested in objects of type XXXImpl. For subclasses of CvObject we only need 'CvObject' for display purposes.
     *
     * @return String The intact type of the wrapped object (eg 'Experiment')
     */
    public String getIntactType() {

        if ( intactType == null ) {

            intactType = getIntactType( obj.getClass() );
        }
        return intactType;

    }

    /**
     * Provides the basic Intact type of the wrapped AnnotatedObject (ie no java package beans). NOTE: only the
     * INTERFACE types are provided as these are the only ones of interest in the model - display pages are not
     * interested in objects of type XXXImpl. For subclasses of CvObject we only need 'CvObject' for display purposes.
     *
     * @param clazz the class of the object we are interrested in.
     *
     * @return String The intact type of the wrapped object (eg 'Experiment')
     */
    public String getIntactType( Class<? extends IntactObject> clazz ) {
         return SearchClass.valueOfMappedClass(clazz).getShortName();
    }


    /**
     * This method will count up the number of Proteins that a List of Components contains. It will recurse through all
     * nested Interactions if necessary, and so handle complexes.
     *
     * @param interactionAc The AC of the interaction to check
     *
     * @return int the number or Proteins present in the Component List.
     */
    private int countInteractors(String interactionAc)
    {
        int count = 0;

        InteractionDao dao = getDaoFactory().getInteractionDao();

        int interactorsIncInteractionsCount = dao.countInteractorsByInteractionAc(interactionAc);

        List<String> interactionsAcForInteraction = dao.getNestedInteractionAcsByInteractionAc(interactionAc);

        int interactions = interactorsIncInteractionsCount-interactionsAcForInteraction.size();

        count = count+interactions;

        for (String nestedInteractionAc : interactionsAcForInteraction)
        {
            count = count+countInteractors(nestedInteractionAc);
        }

        return count;
    }

    private DaoFactory getDaoFactory()
    {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }


}
