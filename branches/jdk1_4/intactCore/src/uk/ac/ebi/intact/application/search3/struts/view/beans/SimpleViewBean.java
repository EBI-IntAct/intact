/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.model.*;

import java.util.*;

/**
 * This class provides JSP view information for a particular AnnotatedObject. Its main purpose is to provide very simple
 * beans for display in an initial search result page. Currenty the types that may be displayed with this bean are
 * Experiment, Protein, Interaction and CvObject.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class SimpleViewBean extends AbstractViewBean {

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
    private String relatedItemsSize = " ";

    /**
     * Holds the URL to perform subsequent searches from JSPs - used to build 'complete' URLs for use by JSPs
     */
    private String searchURL;

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

    private static ArrayList geneNameFilter = new ArrayList();

    static {
        // TODO somehow find a way to use MI references that are stable
        geneNameFilter.add( "gene name" );
        geneNameFilter.add( "gene name-synonym" );
        geneNameFilter.add( "orf name" );
        geneNameFilter.add( "locus name" );
    }


    /**
     * The bean constructor requires an AnnotatedObject to wrap, plus beans on the context path to the search
     * application and the help link. The object itself can be any one of Experiment, Protein, Interaction or CvObject
     * type.
     *
     * @param obj         The AnnotatedObject whose beans are to be displayed
     * @param link        The link to the help pages
     * @param searchURL   The general URL to be used for searching (can be filled in later).
     * @param contextPath The path to the search application.
     */
    public SimpleViewBean( AnnotatedObject obj, String link, String searchURL, String contextPath ) {
        super( link, contextPath );
        this.searchURL = searchURL;
        this.obj = obj;
    }

    /**
     * Adds the shortLabel of the AnnotatedObject to an internal list used later for highlighting in a display. NOT SURE
     * IF WE STILL NEED THIS!!
     */
    public void initHighlightMap() {
        Set set = new HashSet( 1 );
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

        //TODO Remoove this with ProteinUtils
        Set someComponents = new HashSet( anInteractor.getActiveInstances() );
        Collection uniqueInteractions = new HashSet();

        for ( Iterator iterator = someComponents.iterator(); iterator.hasNext(); ) {
            Component aComponent = (Component) iterator.next();
            Interaction anInteraction = aComponent.getInteraction();
            uniqueInteractions.add( anInteraction );

        }

        return Integer.toString( uniqueInteractions.size() );
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
            objSearchURL = searchURL + obj.getAc() + "&amp;searchClass=" + getIntactType();
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
    public String getObjSearchURL( Class clazz, String ac ) {

        return searchURL + ac + "&amp;searchClass=" + getIntactType( clazz );
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
        if ( relatedItemsSize == " " ) {
            //obtain the number of its related 'items'...
            Class clazz = obj.getClass();
            int size = 0;
            if ( Experiment.class.isAssignableFrom( clazz ) ) {
                Experiment exp = (Experiment) obj;
                size = exp.getInteractions().size();
                relatedItemsSize = Integer.toString( size );
            }

            //just check to be sure!!
            if ( Interaction.class.isAssignableFrom( clazz ) ) {
                Interaction interaction = (Interaction) obj;

                //this should check for complexes also....
                size = countInteractors( interaction.getComponents() );
                relatedItemsSize = Integer.toString( size );
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
    public Collection getGeneNames( Interactor interactor ) {

        Collection geneNames = new HashSet();
        //geneNames = new StringBuffer();
        //the gene names are obtained from the Aliases for the Protein
        //which are of type 'gene name'...
        Collection aliases = interactor.getAliases();
        for ( Iterator it = aliases.iterator(); it.hasNext(); ) {
            Alias alias = (Alias) it.next();

            if ( geneNameFilter.contains( alias.getCvAliasType().getShortLabel() ) ) {
                geneNames.add( alias.getName() );
            }
        }
        //now strip off trailing comma - if there are any names....
        if ( geneNames.size() == 0 ) {
            geneNames.add( "-" );
        }
        return geneNames;
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

    private static Map typeCache = null;

    /**
     * Provides the basic Intact type of the wrapped AnnotatedObject (ie no java package beans). NOTE: only the
     * INTERFACE types are provided as these are the only ones of interest in the model - display pages are not
     * interested in objects of type XXXImpl. For subclasses of CvObject we only need 'CvObject' for display purposes.
     *
     * @param clazz the class of the object we are interrested in.
     *
     * @return String The intact type of the wrapped object (eg 'Experiment')
     */
    public String getIntactType( Class clazz ) {

        if ( typeCache == null ) {
            typeCache = new HashMap();
        }

        String type = (String) typeCache.get( clazz );

        if ( type == null ) {

            String className = clazz.getName();
            String basicType = className.substring( className.lastIndexOf( "." ) + 1 );

            //now check for 'Impl' and ignore it...
            type = ( ( basicType.indexOf( "Impl" ) == -1 ) ?
                     basicType : basicType.substring( 0, basicType.indexOf( "Impl" ) ) );

            typeCache.put( clazz, type );
        }

        return type;
    }

    /**
     * This method will count up the number of Proteins that a List of Components contains. It will recurse through all
     * nested Interactions if necessary, and so handle complexes.
     *
     * @param components The Components to check
     *
     * @return int the number or Proteins present in the Component List.
     */
//    private int countProteins( Collection components ) { //2 usage in SimpleViewBean
    private int countInteractors( Collection components ) {

        Interactor interactor = null;
        int count = 0;
        for ( Iterator it = components.iterator(); it.hasNext(); ) {
            Component comp = (Component) it.next();
            interactor = comp.getInteractor();

            //if the interactor is another Interaction then we need to
            //go deeper until we get to some Proteins.....
            if ( interactor instanceof Interaction ) {
                Interaction interaction = (Interaction) interactor;
                count = count + countInteractors( interaction.getComponents() );
            } else {
                count = count + 1;
            }
        }

        return count;
    }


}
