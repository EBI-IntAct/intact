package uk.ac.ebi.intact.application.search3.struts.view.beans;

import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.SearchReplace;

import java.sql.Timestamp;
import java.util.*;

/**
 * This view bean is used to provide the information for JSP display relating to Interactions and Experiments. Both
 * these classes require the same JSP view to be displayed, therefore a single bean is used to handle data
 * transformations as required. Note that for large Experiments this bean is also responsible for providing the tabbed
 * content (NOT the means to display it!).
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class MainDetailViewBean extends AbstractViewBean {

    /**
     * This view bean provides beans for an Experiment.
     */
    private Experiment obj;

    /**
     * Flag to identify the view details to be provided - true for Interaction context, false otherwise. Assumes
     * Experiment view by default. Avoids messing around with null values etc for a wrapped Interaction.
     */
    private boolean interactionView;

    /**
     * Holds the URL to perform subsequent searches from JSPs - used to build 'complete' URLs for use by JSPs
     */
    private String searchURL;

    /**
     * Cached search URL, set up on first request for it.
     */
    private String objSearchURL;

    /**
     * Cached search URL for the wrapped object's biosource
     */
    private String bioSourceSearchURL;

    /**
     * Cached CvInteraction search URL, (used only by Experiments) set up on first request for it.
     */
    private String cvInteractionSearchURL = "";

    /**
     * Cached CvIdentification search URL, (used only by Experiments) set up on first request for it.
     */
    private String cvIdentificationSearchURL = "";

    /**
     * The intact type of the wrapped AnnotatedObject. Note that only the interface types are relevant for display
     * purposes - thus any concrete 'Impl' types will be considered to be their interface types in this case (eg a
     * wrapped InteractionImpl will have the intact type of 'Interaction'). Would be nice to get rid of the proxies one
     * day ...:-)
     */
    private String intactType;

    /**
     * Cached String for the wrapped object's full biosource name. Must be set to something because it may not exist and
     * JSPs don't like nulls - this is handled in the get method for it.
     */
    private String bioSourceName;

    /**
     * Holds a list of Annotations that may be publicly displayed, ie a filtered list removing those to be excluded
     * (currently rrmakrs and uiprot exports)
     */
    private Collection annotationsForDisplay = new ArrayList();

    /**
     * Map of retrieved DB URLs already retrieved from the DB. This is basically a cache to avoid recomputation every
     * time a CvDatabase URL is requested.
     */
    private Map dbUrls;

    /**
     * The List of Interations for display. For Experiments of 'small' size this is the whole list - for 'large'
     * Interactions this is a sublist that is dynamically changed upon different user requests. This is marked as
     * transient because it seems that the java subList is not serializable.
     */
    private transient Collection interactionList = new ArrayList();

    /**
     * This is only defined for 'Interaction context' views and holds the Interaction that is to be viewed in the
     * context of its Experiment. It is returned transparently to clients wishing to display Interaction details and
     * should be set by bean creators. (eg result Action classes)
     */
    private Interaction wrappedInteraction;

    /**
     * ArrayList to provide a Filter on Gene Names
     */
    private static ArrayList geneNameFilter = new ArrayList( 4 );

    static {
        // TODO somehow find a way to use MI references that are stable
        geneNameFilter.add( "gene name" );
        geneNameFilter.add( "gene name-synonym" );
        geneNameFilter.add( "orf name" );
        geneNameFilter.add( "locus name" );
    }

    /**
     * The maximum number of pages that can be displayed. This is only of relevance to 'large' Experiments, ie those
     * with more than {@link Constants} MAX_PAGE_SIZE Interactions.
     */
    int maxPages;

    private static final String PMID_PARAM = "${pmid}";
    private static final String YEAR_PARAM = "${year}";
    private static final String FTP_CURRENT_RELEASE = "ftp://ftp.ebi.ac.uk/pub/databases/intact/current";
    private static final String PSI1_URL  = FTP_CURRENT_RELEASE + "/psi1/pmid/" + YEAR_PARAM + "/" + PMID_PARAM + ".zip";
    private static final String PSI25_URL = FTP_CURRENT_RELEASE + "/psi25/pmid/" + YEAR_PARAM + "/" + PMID_PARAM + ".zip";

    private static boolean hasYearParam = ( PSI1_URL.indexOf( YEAR_PARAM ) != -1 );
    private static boolean hasPubmedParam = ( PSI1_URL.indexOf( PMID_PARAM ) != -1 );

    private String psi1Url;
    private String psi25Url;

    private boolean hasPsi1URL = false;
    private boolean hasPsi25URL = false;

    /**
     * The bean constructor requires an Experiment to wrap, plus beans on the context path to the search application and
     * the help link.
     *
     * @param obj         The Experiment whose beans are to be displayed
     * @param link        The link to the help pages
     * @param searchURL   The general URL to be used for searching (can be filled in later).
     * @param contextPath The path to the search application.
     *
     * @throws NullPointerException     thrown if the object to wrap is null
     * @throws IllegalArgumentException thrown if the parameter is not an Experiment
     */
    public MainDetailViewBean( Experiment obj, String link, String searchURL, String contextPath ) {

        super( link, contextPath );
        if ( obj == null ) {
            throw new NullPointerException( "MainDetailViewBean: can't display null object!" );
        }
        if ( !( obj instanceof Experiment ) ) {
            throw new IllegalArgumentException( "MainDetailViewBean: Wrong object passed to view bean: type is " +
                                                obj.getClass().getName() );
        }
        this.searchURL = searchURL;
        this.obj = obj;
        dbUrls = new HashMap();

        //now calculate the largest page size possible (only for large Experiments)
        if ( obj.getInteractions().size() > Constants.MAX_PAGE_SIZE ) {

            // calculate the maximum number of pages (either size/page size, or
            //size/page size + 1 if it does not divide exactly)
            maxPages = obj.getInteractions().size() / Constants.MAX_PAGE_SIZE;
            if ( obj.getInteractions().size() % Constants.MAX_PAGE_SIZE > 0 ) {
                maxPages++;
            }
            //set the initial page of Interactions
            setInteractionPage( 1 );
        } else {
            interactionList = obj.getInteractions();
        }

        // TODO centralize where the PSI links are created.
        psi1Url = PSI1_URL;
        psi25Url = PSI25_URL;

        if ( hasPubmedParam ) {
            String pubmedId = getPubmedId( obj );

            if ( pubmedId != null ) {
                // create PSI 1 URL
                psi1Url = SearchReplace.replace( psi1Url, PMID_PARAM, pubmedId );
                hasPsi1URL = true;

                // create PSI 2.5 URL
                psi25Url = SearchReplace.replace( psi25Url, PMID_PARAM, pubmedId );
                hasPsi25URL = true;
            }
        }

        if ( hasYearParam ) {
            String year = getCreatedYear( obj );
            if( year != null ) {
                  // create PSI 1 URL
                psi1Url = SearchReplace.replace( psi1Url, YEAR_PARAM, year );
                hasPsi1URL = true;

                // create PSI 2.5 URL
                psi25Url = SearchReplace.replace( psi25Url, YEAR_PARAM, year );
                hasPsi25URL = true;
            }
        }
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
     * Returns the help section. Needs to be reviewed.
     */
    public String getHelpSection() {
        return "protein.single.view";
    }

    /**
     * Used to tell the bean whether or not it is to be used for a 'full' Experiment view or simply an 'Interaction
     * context' view.
     *
     * @param interaction The Interaction for which the context is required
     */
    public void setWrappedInteraction( Interaction interaction ) {
        wrappedInteraction = interaction;
        interactionView = true; //set the boolean flag
    }

    /**
     * Determines whether the bean is currentlly set to render a full Experiment view or an 'Interaction context@ view.
     *
     * @return true for a Interaction context view, false otherwise.
     */
    public boolean isInteractionView() {
        return interactionView;
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
     * Provides direct access to the wrapped Experiment itself.
     *
     * @return Experiment The reference to the wrapped object.
     */
    public Experiment getObject() {
        return obj;
    }

    /**
     * Provides the basic Intact type of the wrapped Experiment (ie no java package beans). NOTE: only the INTERFACE
     * types are provided as these are the only ones of interest in the model - display pages are not interested in
     * objects of type XXXImpl.
     *
     * @return String The intact type of the wrapped Experiment
     */
    public String getIntactType() {

        if ( intactType == null ) {
            //set on first call
            String className = obj.getClass().getName();
            String basicType = className.substring( className.lastIndexOf( "." ) + 1 );

            //now check for 'Impl' and ignore it...
            intactType = ( ( basicType.indexOf( "Impl" ) == -1 ) ?
                           basicType : basicType.substring( 0, basicType.indexOf( "Impl" ) ) );
        }
        return intactType;

    }

    /**
     * Returns the BioSource of the given Experiment, returns "-" if the BioSource got no valid fullname
     *
     * @return String which contains the fullname of the biosource object
     */
    public String getBioSourceName() {

        if ( bioSourceName == null ) {
            //set on fiirst call
            bioSourceName = obj.getBioSource().getFullName();
            if ( bioSourceName == null ) {
                bioSourceName = "-";   //may not have one, and can't use null in JSPs
            }

        }
        return bioSourceName;
    }

    /**
     * Provides the actual Annotation list of the wrapped object. Clients can use this method to gain access to
     * Annotation data for display. Note: this method filters out those Annotations which are not for public view;
     * currently these are 'remark' and 'uniprot-dr-export' Annotations.
     *
     * @return Collection the list of Annotation objects for the wrapped instance.
     */
    public Collection getFilteredAnnotations() {

        if ( annotationsForDisplay.isEmpty() ) {
            //set on first call
            for ( Iterator it = obj.getAnnotations().iterator(); it.hasNext(); ) {
                Annotation annotation = (Annotation) it.next();

                //run through the filter
                if ( false == AnnotationFilter.getInstance().isFilteredOut( annotation ) ) {
                    annotationsForDisplay.add( annotation );
                }
            }
        }

        return annotationsForDisplay;
    }

    /**
     * Convenience method to provide a filtered list of Annotations for a given Interaction. Useful in JSP display to
     * apply the same filters of the wrapped Experiment to one of its Interactions.
     *
     * @return Collection the filtered List of Annotations (empty if there are none)
     */
    public Collection getFilteredAnnotations( Interaction interaction ) {

        Collection filteredAnnots = new ArrayList();

        for ( Iterator it = interaction.getAnnotations().iterator(); it.hasNext(); ) {
            Annotation annotation = (Annotation) it.next();

            //run through the filter
            if ( false == AnnotationFilter.getInstance().isFilteredOut( annotation ) ) {
                filteredAnnots.add( annotation );
            }
        }

        return filteredAnnots;
    }

    /**
     * Filters the Annotations for the 'comment' ones only. This is useful for JSPs wishing to display Annotations in
     * certain orders.
     *
     * @param annots a Collection of Annotations to filter
     *
     * @return Collection a list of the 'comment' Annotations, or empty if there are none.
     */
    public Collection getComments( Collection annots ) {

        Collection comments = new ArrayList();
        for ( Iterator it = annots.iterator(); it.hasNext(); ) {
            Annotation annot = (Annotation) it.next();
            if ( annot.getCvTopic().getShortLabel().equals( "comment" ) ) {
                comments.add( annot );
            }
        }
        return comments;
    }

    /**
     * Convenience method for obtaining the wrapped object's xrefs. Useful for clients to use in conjunction with other
     * methods on this view bean to provide suitably formatted Xref data.
     *
     * @return Collection the list of xrefs for the wrapped object.
     */
    public Collection getXrefs() {
        return obj.getXrefs();
    }

    /**
     * Convenience method for obtaining the uniprot ID for a given Protein.
     *
     * @param protein The protein we want the uniprot ID for
     *
     * @return String the uniprot ID of the Protein, or '-' if none found.
     */
    public String getUniprotLabel( Protein protein ) {

        String uniprotLabel = "-";  //default for display
        Collection xrefs = protein.getXrefs();
        for ( Iterator it = xrefs.iterator(); it.hasNext(); ) {
            Xref xref = (Xref) it.next();
            if ( xref.getCvDatabase().getShortLabel().equals( CvDatabase.UNIPROT ) ) {
                uniprotLabel = xref.getPrimaryId();
                break;  //done
            }
        }
        return uniprotLabel;
    }

    /**
     * Provides the Interactions for the wrapped Experiment.
     * <p/>
     * NOTE: For Experiments that have a large number of Interactions, this method will provide a page of the total
     * list, as set by the {@link MainDetailViewBean#setInteractionPage} method. If the Interaction list is small enough
     * then the complete list is returned in any case. </p>
     *
     * @return Collection a list of Interactions - either all of them or a chunk (of pre-defined size).
     */
    public Collection getInteractions() {

        Collection result = new ArrayList();
        //first check for an 'Interaction context' view - if so then return that one only
        if ( isInteractionView() ) {
            result.add( wrappedInteraction );
        } else {
            result = interactionList;
        }

        return result;
    }

    /**
     * Used for 'large' Experiments. The list of Interactions returned by this viewbean will be reset to the page as
     * specified by the page parameter. The page size is defined in the {@link Constants} class, and the page number is
     * bound above by the size of the actual full Interaction list of the large Experiment - that is, the largest index
     * will be (max size/page size), or (max size/page size) + 1 if it does not divide without remainder.
     *
     * @param page The page number to be used.
     *
     * @throws IndexOutOfBoundsException thrown is the index is out of range
     */
    public void setInteractionPage( int page ) {

        if ( page > maxPages ) {
            throw new IndexOutOfBoundsException( "interaction page number too large! Got: "
                                                 + page + " Max: " + maxPages );
        }
        if ( page < 1 ) {
            throw new IndexOutOfBoundsException( "interaction page number < 1!" );
        }

        //now need to work out the actual start index from the page number itself, as follows:
        //  index of first Interaction in sublist = (page size)*(page number - 1)
        int fromIndex = Constants.MAX_PAGE_SIZE * ( page - 1 );

        //The 'toIndex' has to be the smallest of either a 'page size' offset from the
        //startIndex, or the last index (ie size) of the whole List (ie if we are at the end)
        int toIndex = Math.min( fromIndex + Constants.MAX_PAGE_SIZE,
                                obj.getInteractions().size() );

        //'fromIndex' inclusive, 'toIndex' exclusive for subList method!
        if ( List.class.isAssignableFrom( obj.getInteractions().getClass() ) ) {
            interactionList = ( (List) obj.getInteractions() ).subList( fromIndex, toIndex );
        } else {
            // copy the collection in a List
            List tmpList = new ArrayList( obj.getInteractions() );
            interactionList = tmpList.subList( fromIndex, toIndex );
        }

    }

    /**
     * Can be useful for a JSP to find out how many pages can be displayed for the warpped Experiment.
     *
     * @return int The page number of the last possible page for display.
     */
    public int getMaxPage() {
        return maxPages;
    }

    /**
     * This method provides beans of all the Features for an Interaction which are linked to other Features. Feature
     * view beans, rather than the Features themselves, are provided to allow JSPs an easier display method.
     *
     * @param interaction The Interaction we want the linked Features for
     *
     * @return Collection a List of FeatureViewBeans providing a view on each linked Feature of the Interaction.
     */
    public Collection getLinkedFeatures( Interaction interaction ) {

        //TODO: needs refactoring - perhaps cache some beans for Interactions done...
        Collection linkedFeatures = new ArrayList();

        // while we populate the collection of feature to display, we keep track of those are are already going to
        // be display. eg. F1 interacts with F2, when F2 comes again, we don't create a bean for it.
        Collection seen = new ArrayList();

        //Go through the Features and for each linked one, build a view bean and save it...
        //NB The Feature beans are held inside Collections within each Component of the
        //Interaction - they are called 'binding domains'...
        Collection components = interaction.getComponents();

        for ( Iterator it = components.iterator(); it.hasNext(); ) {
            Component component = (Component) it.next();

            Collection features = component.getBindingDomains();

            for ( Iterator it1 = features.iterator(); it1.hasNext(); ) {
                Feature feature = (Feature) it1.next();

                if ( feature.getBoundDomain() != null ) {
                    if ( false == seen.contains( feature ) ) {
                        linkedFeatures.add( new FeatureViewBean( feature, getHelpLink(), searchURL, this.getContextPath() ) );

                        seen.add( feature );

                        if ( feature.getBoundDomain().getBoundDomain() == feature ) {
                            // if the features relate to each other
                            seen.add( feature.getBoundDomain() );
                        }
                    }
                }
            } // features

        } // components

        return linkedFeatures;
    }

    /**
     * Provides a List of view beans for unlinked Features of an Interaction.
     *
     * @param interaction The Interaction we want the single Feature information for.
     *
     * @return Collection a List of FeatureViewBeans for the INteraction's unlinked Features.
     */
    public Collection getSingleFeatures( Interaction interaction ) {

        //TODO: Needs refactoring - see above....
        Collection singleFeatures = new ArrayList();
        //Go through the Features and for each single one, build a view bean and save it...
        //NB The Feature beans are held inside Collections within each Component of the
        //Interaction - they are called 'binding domains'...
        Collection components = interaction.getComponents();

        for ( Iterator it = components.iterator(); it.hasNext(); ) {
            Component component = (Component) it.next();
            Collection features = component.getBindingDomains();

            for ( Iterator it1 = features.iterator(); it1.hasNext(); ) {
                Feature feature = (Feature) it1.next();

                if ( feature.getBoundDomain() == null ) {
                    singleFeatures.add( new FeatureViewBean( feature, getHelpLink(), searchURL, this.getContextPath() ) );
                }
            }
        } //

        return singleFeatures;
    }

    /**
     * Convenience method to obtain all of the Proteins for a given Interaction. Note that this method will only return
     * the Proteins, NOT complexes that might be a Component's Interactor.
     *
     * @param interaction The Interaction we want the Proteins for
     *
     * @return Collection a List of the Interaction's Proteins, or empty if none found
     */
    public Collection getProteins( Interaction interaction ) {

        // sort here with Collections.sort() with the Protein Comparator as
        // so that we get in that the baits before the preys

        Collection results = new ArrayList();
        for ( Iterator it = interaction.getComponents().iterator(); it.hasNext(); ) {
            Component comp = (Component) it.next();
            Interactor interactor = comp.getInteractor();
            if ( interactor instanceof Protein ) {
                results.add( interactor );
            }
        }

        return results;
    }

    /**
     * Provides the Component that holds a Protein for a given Interaction. Assumes that a Protein only appears ONCE as
     * a Component of an Interaction.
     *
     * @param protein     The Protein we are interested in
     * @param interaction The Interaction for which the Protein is of relevance
     *
     * @return Component the Component of the Interaction which holds the Protein, or null if it is not present.
     */
    public Component getComponent( Protein protein, Interaction interaction ) {

        //go through the Components holding the Protein and pull out the Interaction match...
        for ( Iterator it = protein.getActiveInstances().iterator(); it.hasNext(); ) {
            Component comp = (Component) it.next();
            if ( comp.getInteraction().equals( interaction ) ) {
                return comp;
            }
        }

        return null;

    }

    /**
     * Convenience method to obtain all Gene Names of a Proteins
     *
     * @param protein The Protein we are interested in
     *
     * @return Collection a Set of Gene Names as Strings, empty if none found
     */
    public Collection getGeneNames( Protein protein ) {

        Collection geneNames = new HashSet();
        //geneNames = new StringBuffer();
        //the gene names are obtained from the Aliases for the Protein
        //which are of type 'gene name'...
        Collection aliases = protein.getAliases();
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
     * Provides a String representation of a URL to access the CV related to the Xref (ie the Cv beans describing the
     * Xref's database).
     *
     * @param xref The Xref for which the URL is required
     *
     * @return String a String representation of a URL link for the Xref beans (CvDatabase)
     */
    public String getCvDbURL( Xref xref ) {

        return ( searchURL + xref.getCvDatabase().getAc() +
                 "&amp;searchClass=CvDatabase&amp;" );
    }

    /**
     * Provides a String representation of a URL to access the CV qualifier info related to the Xref (ie the Cv beans
     * describing the Xref's qualifier info).
     *
     * @param xref The Xref for which the URL is required
     *
     * @return String a String representation of a URL link for the Xref beans (CvXrefQualifier)
     */
    public String getCvQualifierURL( Xref xref ) {

        return ( searchURL + xref.getCvXrefQualifier().getAc() +
                 "&amp;searchClass=CvXrefQualifier&amp;" );
    }

    /**
     * Provides a String representation of a URL to provide acces to an Xrefs' database (curently via AC). The URL is at
     * present stored via an Annotation for the Xref in the Intact DB itself.
     *
     * @param xref The Xref for which the DB URL is required
     *
     * @return String a String representation of a DB URL link for the Xref, or a '-' if there is no stored URL link for
     *         this Xref
     */
    public String getPrimaryIdURL( Xref xref ) {

        // Check if the id can be hyperlinked
        String searchUrl = (String) dbUrls.get( xref.getCvDatabase() );
        if ( searchUrl == null ) {
            //not yet requested - do it now and cache it..
            Collection annotations = xref.getCvDatabase().getAnnotations();
            Annotation annot = null;
            for ( Iterator it = annotations.iterator(); it.hasNext(); ) {
                annot = (Annotation) it.next();
                if ( annot.getCvTopic().getShortLabel().equals( "search-url" ) ) {
                    //found one - we are done
                    searchUrl = annot.getAnnotationText();
                    break;
                }
            }

            //cache it - even if the URL is null, because it may be
            //requested again
            dbUrls.put( xref.getCvDatabase(), searchUrl );
        }

        //if it isn't null, fill it in properly and return
        if ( searchUrl != null ) {
            //An Xref's primary can't be null - the constructor doesn't allow it..
            searchUrl = SearchReplace.replace( searchUrl, "${ac}", xref.getPrimaryId() );

        }
        return searchUrl;
    }

    /**
     * Provides a serahc URL for the Uniprot databse entry of the specified Protein.
     *
     * @param protein The Protein we are interested in
     *
     * @return String a uniprot search URL for the Protein, or '-' if none found
     */
    public String getUniprotSearchURL( Protein protein ) {

        Collection xrefs = protein.getXrefs();
        String uniprotURL = "-";
        for ( Iterator it = xrefs.iterator(); it.hasNext(); ) {
            Xref xref = (Xref) it.next();
            if ( xref.getCvDatabase().getShortLabel().equals( CvDatabase.UNIPROT ) ) {
                uniprotURL = this.getPrimaryIdURL( xref );
                break;  //done
            }
        }
        return uniprotURL;

    }

    /**
     * Provides a String representation of a URL to perform a search on this Experiment's beans (curently via AC)
     *
     * @return String a String representation of a search URL link for the wrapped Experiment
     */
    public String getObjSearchURL() {

        if ( objSearchURL == null ) {
            //set it on the first call
            //NB need to get the correct intact type of the wrapped object
            objSearchURL = searchURL + obj.getAc() + "&amp;searchClass=" + getIntactType() +
                           "&filter=ac";
        }
        return objSearchURL;
    }

    /**
     * Provides a String representation of a URL to perform a search on CvInteraction
     *
     * @return String a String representation of a search URL link for CvInteraction, or "-" if there is No
     *         CvInteraction defined for the object.
     */
    public String getCvInteractionSearchURL() {

        if ( obj.getCvInteraction() == null ) {
            return "-";
        }
        if ( cvInteractionSearchURL == "" ) {
            //set it on the first call
            //get the CvInteraction object and pull out its AC
            cvInteractionSearchURL = searchURL + obj.getCvInteraction().getAc()
                                     + "&amp;searchClass=CvInteraction&filter=ac";
        }
        return cvInteractionSearchURL;
    }

    /**
     * Provides a String representation of a URL to perform a search on CvIdentification (Experiments only)
     *
     * @return String a String representation of a search URL link for CvIdentification, or "-" if there is NO
     *         CvIdentification specified for the object.
     */
    public String getCvIdentificationSearchURL() {

        if ( obj.getCvIdentification() == null ) {
            return "-";
        }
        if ( cvIdentificationSearchURL == "" ) {
            //set it on the first call
            //get the CvIdentification object and pull out its AC
            cvIdentificationSearchURL = searchURL + obj.getCvIdentification().getAc()
                                        + "&amp;searchClass=CvIdentification&filter=ac";
        }
        return cvIdentificationSearchURL;
    }

    /**
     * Provides a String representation of a URL to perform a search on CvInteractionType (Interactions only)
     *
     * @return String a String representation of a search URL link for CvInteractionType, or "-" if there is NO
     *         CvInteractionType specified for the interaction.
     */
    public String getCvInteractionTypeSearchURL( Interaction interaction ) {

        if ( interaction.getCvInteractionType() == null ) {
            return "-";
        }
        return searchURL + interaction.getCvInteractionType().getAc()
               + "&amp;searchClass=CvInteractionType&filter=ac";
    }

    /**
     * Provides a String representation of a URL to perform a search on BioSource.
     *
     * @return String a String representation of a search URL link for BioSource.
     */
    public String getBioSourceSearchURL() {

        if ( bioSourceSearchURL == null ) {
            //set it on the first call
            bioSourceSearchURL = searchURL + obj.getBioSource().getAc()
                                 + "&amp;searchClass=BioSource";
        }
        return bioSourceSearchURL;
    }

    /**
     * Provides a String representation of URL to perform a search to the BioSource Object from the give Protein
     *
     * @param protein the Protein in which we are interested in
     *
     * @return String contains the URL
     */
    public String getProteinBiosourceURL( Protein protein ) {
        return searchURL + protein.getBioSource().getAc() + "&amp;searchClass=BioSource";
    }

    /**
     * Provides a String representation of the URL to perform a search to the BioSource Object
     *
     * @param bioSource the BioSource in which we are interested in
     *
     * @return Sring contains the URL
     */
    public String getBiosourceURL( BioSource bioSource ) {

        if ( bioSource != null ) {
            return searchURL + bioSource.getAc() + "&amp;searchClass=BioSource";
        } else {
            return "-";
        }
    }

    /**
     * Provides a String representation of the bioSource name for the used in the for the interacting molecules.
     *
     * @param bioSource
     *
     * @return String a String representation of the name of the BioSource
     */
    public String getBioSourceName( BioSource bioSource ) {

        if ( bioSource != null && bioSource.getShortLabel() != null ) {
            return bioSource.getShortLabel();
        }
        return "-";

    }

    /**
     * Provides a String representation of the bioSource name for the used in the for the host in the experiemnt
     *
     * @return String a String representation of the host name of the Experiment
     */
    public String getExperimentBioSourceName() {
        BioSource bioSource = obj.getBioSource();
        if ( bioSource != null ) {
            return bioSource.getShortLabel();
        } else {
            return "-";
        }
    }

    /**
     * Convenience method to provide a String representation of a URL to perform a search on CvTopic for a particular
     * Annotation
     *
     * @param annot The Annotation we want a search URL for
     *
     * @return String a String representation of a search URL link for CvInteraction.
     */
    public String getCvTopicSearchURL( Annotation annot ) {

        return searchURL + annot.getCvTopic().getAc() + "&amp;searchClass=CvTopic&filter=ac";
    }

    /**
     * Convenience method to provide a String representation of a URL to perform a search on CvComponentRole for a
     * particular Protein/Interaction pair
     *
     * @param comp The Component role we want a search URL for
     *
     * @return String a String representation of a search URL link for CvComponentRole.
     */
    public String getCvComponentRoleSearchURL( Component comp ) {

        return searchURL + comp.getCvComponentRole().getAc() +
               "&amp;searchClass=CvComponentRole&filter=ac";
    }

    /**
     * Convenience method to provide a String representation of a URL to perform a search on Protein. This method will
     * provide a Protein 'detail' view.
     *
     * @param prot The Protein we want a search URL for
     *
     * @return String a String representation of a search URL link for Protein.
     */
    public String getProteinSearchURL( Protein prot ) {

        return searchURL + prot.getAc() + "&amp;searchClass=Protein&amp;view=single&filter=ac";
    }

    /**
     * Convenience method to provide a String representation of a URL to perform a search on Protein. This method will
     * provide a Protein 'partners' view.
     *
     * @param prot The Protein we want a search URL for
     *
     * @return String a String representation of a search URL link for Protein.
     */
    public String getProteinPartnerURL( Protein prot ) {

        return searchURL + prot.getAc() + "&amp;searchClass=Protein&amp;view=partner&filter=ac";
    }

    //////////////////////////
    // PSI links

    private static String getCreatedYear( Experiment exp ) {

        Timestamp created = exp.getCreated();
        java.sql.Date d = new java.sql.Date( created.getTime() );
        Calendar c = new GregorianCalendar();
        c.setTime( d );

        int year = c.get( Calendar.YEAR );

        return String.valueOf( year );
    }

    private String getPubmedId( Experiment experiment ) {

        for ( Iterator iterator = experiment.getXrefs().iterator(); iterator.hasNext(); ) {
            Xref xref = (Xref) iterator.next();

            if ( xref.getCvDatabase().getShortLabel().equals( CvDatabase.PUBMED ) ) {
                CvXrefQualifier qualifier = xref.getCvXrefQualifier();

                if ( qualifier != null && qualifier.getShortLabel().equals( CvXrefQualifier.PRIMARY_REFERENCE ) ) {
                    // found it
                    return xref.getPrimaryId();
                }
            }
        }
        return null;
    }

    public String getPsi1Url() {
        return psi1Url;
    }

    public String getPsi25Url() {
        return psi25Url;
    }

    public boolean hasPsi1URL() {
        return hasPsi1URL;
    }

    public boolean hasPsi25URL() {
        return hasPsi25URL;
    }
}