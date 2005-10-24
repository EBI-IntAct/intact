// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import uk.ac.ebi.intact.application.commons.util.AnnotationFilter;
import uk.ac.ebi.intact.application.dataConversion.PsiVersion;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.PrintStream;
import java.util.*;

/**
 * User session for a PSI Download. <br> Are stored here, everything we need to build efficiently a PSI document.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class UserSessionDownload {

    ////////////////////////////////////////
    // Constants

    public static final int PSI_VERSION_1 = 1;

    ////////////////////////////////////////
    // Instance variables

    /**
     * When generating Experiment, Interaction, Interactor, we add the accession number (AC) as an Xref. The
     * sourceDatabase is used as source.
     */
    private CvDatabase sourceDatabase = null;

    /**
     * The PSI document we are going to build.
     */
    private Document psiDocument = null;

    /**
     * The root element of the PSI document
     */
    private Element rootElement;

    /**
     * The entry Element of the PSI document
     */
    private Element entryElement;

    /**
     * The source Element under the entry of the PSI document.
     */
    private Element sourceElement;

    /**
     * The experimentList under the entry of the PSI document.
     */
    private Element experimentListElement;

    /**
     * The interactorList under the entry of the PSI document.
     */
    private Element interactorListElement;

    /**
     * The interactionList under the entry of the PSI document.
     */
    private Element interactionListElement;

    /**
     * The availibilityList under the entry of the PSI document.
     */
    private Element availabilityListElement;


    public PsiVersion getPsiVersion() {
        return psiVersion;
    }

    /**
     * Version of the PSI document we are going to generate.
     */
    private PsiVersion psiVersion = PsiVersion.VERSION_1; // set it to default.

    /**
     * Maps of already generated Experiments. AC -> DOM Element
     */
    private Map experiments = new HashMap();

    /**
     * Maps of already generated Proteins. AC -> DOM Element
     */
    private Map interactors = new HashMap();

    /**
     * Maps of already generated BioSource. BioSource -> DOM Element
     */
    private Map organismCache = new HashMap();
    private Map hostOrganismCache = new HashMap();

    /**
     * Maps of already generated CvObjects. CvObject -> DOM Element
     */
    private Map cvObjectCache = new HashMap();

    /**
     * Keep track of the time spend since the beginning of the XML generation.
     */
    private final long startTimestamp = System.currentTimeMillis();

    /**
     * Suffix of a cluster ID. Used when generating PSI feature based on IntAct feature having more than 1 range.
     */
    private int clusterIdSuffix = 0;

    /**
     *
     */
    private Set annotationFilters;

    public static final long FIRST_EXPERIMENT_ID = 1;
    public static final long FIRST_INTERACTOR_ID = 1;
    public static final long FIRST_INTERACTION_ID = 1;
    public static final long FIRST_FEATURE_ID = 1;
    public static final long FIRST_PARTICIPANT_ID = 1;

    /**
     * Experiment id in the scope of the whole document.
     */
    private long experimentIdentifier = FIRST_EXPERIMENT_ID;

    /**
     * Maps an Interaction component to participant ID.
     */
    private Map experimentIdMap = new HashMap();

    /**
     * interactor id in the scope of the whole document.
     */
    private long interactorIdentifier = FIRST_INTERACTOR_ID;

    /**
     * Maps an Interactor component to participant ID.
     */
    private Map interactorIdMap = new HashMap();

    /**
     * interactor id in the scope of the whole document.
     */
    private long interactionIdentifier = FIRST_INTERACTION_ID;

    /**
     * Maps an Interaction component to participant ID.
     */
    private Map interactionIdMap = new HashMap();

    /**
     * feature id in the scope of an interaction.
     */
    private long featureIdentifier = FIRST_FEATURE_ID;

    /**
     * Maps an Feature to feature ID.
     */
    private Map featureIdMap = new HashMap();

    /**
     * Participant id in the scope of the whole document.
     */
    private long participantIdentifier = FIRST_PARTICIPANT_ID;

    /**
     * Maps an Interaction component to participant ID.
     */
    private Map component2participantId = new HashMap();

    CvMapping cvMapping = null;


    private List messages = new ArrayList();

    private Institution source;

    //////////////////////////////////
    // Constructors

    public UserSessionDownload( PsiVersion psiVersion ) {

        this( psiVersion, null );
    }

    public UserSessionDownload( PsiVersion psiVersion, Institution source ) {

        this.psiVersion = psiVersion;
        this.source = source;
        psiDocument = PsiDocumentFactory.buildPsiDocument( this, source );
    }

    //////////////////////////////////
    // setters

    public CvMapping getReverseCvMapping() {
        return cvMapping;
    }

    public void setReverseCvMapping( CvMapping cvMapping ) {

        if ( cvMapping == null ) {
            throw new IllegalArgumentException( "You must give a non null mapping." );
        }

        this.cvMapping = cvMapping;
    }

    public boolean hasCvMapping() {
        return this.cvMapping != null;
    }

    public void setSourceDatabase( CvDatabase sourceDatabase ) {
        this.sourceDatabase = sourceDatabase;
    }

    //////////////////////////////////
    // Getters

    public CvDatabase getSourceDatabase() {
        return sourceDatabase;
    }

    /**
     * Return a PSI document. Create a new wmpty one if it doesn't exists yet.
     *
     * @return a PSI document.
     */
    public Document getPsiDocument() {

        if ( psiDocument == null ) {
            psiDocument = PsiDocumentFactory.buildPsiDocument( this );
        }

        return psiDocument;
    }

    /**
     * Return the root element of the PSI document.
     *
     * @return the source Element.
     */
    public Element getRootElement() {

        if ( rootElement == null ) {
            rootElement = getPsiDocument().getDocumentElement();
        }

        return rootElement;
    }

    /**
     * Return the entry element of the PSI document. If the element doesn't exist, it is created.
     *
     * @return the source Element.
     */
    public Element getEntryElement() {

        if ( entryElement == null ) {
            entryElement = (Element) getRootElement().getElementsByTagName( "entry" ).item( 0 );
        }

        return entryElement;
    }

    /**
     * Return the source element under the entry of the PSI document. If the element doesn't exist, it is created.
     *
     * @return the source Element.
     */
    public Element getSourceElement() {
        if ( sourceElement == null ) {

            Document document = getPsiDocument();
            sourceElement = document.createElement( "source" );

            Element entry = getEntryElement();
            entry.appendChild( sourceElement );
        }

        return sourceElement;
    }

    /**
     * Return the experimentList element under the entry of the PSI document. If the element doesn't exist, it is
     * created.
     *
     * @return the source Element.
     */
    public Element getExperimentListElement() {

        if ( experimentListElement == null ) {

            Document document = getPsiDocument();
            experimentListElement = document.createElement( "experimentList" );

            Element entry = getEntryElement();
            entry.appendChild( experimentListElement );
        }

        return experimentListElement;
    }

    /**
     * Return the interactorList element under the entry of the PSI document. If the element doesn't exist, it is
     * created.
     *
     * @return the source Element.
     */
    public Element getInteractorListElement() {

        if ( interactorListElement == null ) {

            Document document = getPsiDocument();
            interactorListElement = document.createElement( "interactorList" );

            Element entry = getEntryElement();
            entry.appendChild( interactorListElement );
        }

        return interactorListElement;
    }

    /**
     * Return the interactionList element under the entry of the PSI document. If the element doesn't exist, it is
     * created.
     *
     * @return the source Element.
     */
    public Element getInteractionListElement() {

        if ( interactionListElement == null ) {

            Document document = getPsiDocument();
            interactionListElement = document.createElement( "interactionList" );

            Element entry = getEntryElement();
            entry.appendChild( interactionListElement );
        }

        return interactionListElement;
    }

    /**
     * Return the availabilityList element under the entry of the PSI document. If the element doesn't exist, it is
     * created.
     *
     * @return the source Element.
     */
    public Element getAvailabilityListElement() {

        if ( availabilityListElement == null ) {

            Document document = getPsiDocument();
            availabilityListElement = document.createElement( "availabilityList" );

            Element entry = getEntryElement();
            entry.appendChild( availabilityListElement );
        }

        return availabilityListElement;
    }

    public Map getCvObjectCache() {
        return cvObjectCache;
    }

    public Map getOrganismCache() {
        return organismCache;
    }

    public Map getHostOrganismCache() {
        return hostOrganismCache;
    }

    //////////////////////////////////////////////
    // Methods related to identifier management
    // TODO synch ?!

    // Experiment IDs

    public long getNextExperimentIdentifier( Experiment experiment ) {

        if ( experiment == null ) {
            throw new IllegalArgumentException( "You must give a non null experiment." );
        }

        if ( experimentIdMap.containsKey( experiment ) ) {
            Long id = (Long) experimentIdMap.get( experiment );
            throw new IllegalArgumentException( "An id has already been given to that experiment (" + experiment.getAc() + "): " + id );
        }

        experimentIdMap.put( experiment, new Long( experimentIdentifier ) );

        return experimentIdentifier++;
    }

    public long getExperimentIdentifier( Experiment experiment ) {

        if ( experiment == null ) {
            throw new IllegalArgumentException( "You must give a non null experiment." );
        }

        if ( false == experimentIdMap.containsKey( experiment ) ) {

            throw new IllegalArgumentException( "Could not find an Identifier for that experiment (" + experiment.getAc() + ")." );
        }

        Long id = (Long) experimentIdMap.get( experiment );
        return id.longValue();
    }

    // Interactor IDs

    public long getNextInteractorIdentifier( Interactor interactor ) {

        if ( interactor == null ) {
            throw new IllegalArgumentException( "You must give a non null interactor." );
        }

        if ( interactorIdMap.containsKey( interactor ) ) {
            Long id = (Long) interactorIdMap.get( interactor );
            String message = "An id has already been given to that interactor (" + interactor.getAc() + "): " + id;
//            throw new IllegalArgumentException( message );
            addMessage( message );
        }

        interactorIdMap.put( interactor, new Long( interactorIdentifier ) );

        return interactorIdentifier++;
    }

    public long getInteractorIdentifier( Interactor interactor ) {

        if ( interactor == null ) {
            throw new IllegalArgumentException( "You must give a non null interactor." );
        }

        if ( false == interactorIdMap.containsKey( interactor ) ) {

            throw new IllegalArgumentException( "Could not find an Identifier for that interactor (" + interactor.getAc() + ")." );
        }

        Long id = (Long) interactorIdMap.get( interactor );
        return id.longValue();
    }

    // Interaction IDs

    public long getNextInteractionIdentifier( Interaction interaction ) {

        if ( interaction == null ) {
            throw new IllegalArgumentException( "You must give a non null interaction." );
        }

        if ( interactionIdMap.containsKey( interaction ) ) {
            Long id = (Long) interactionIdMap.get( interaction );
            System.err.println( "An id has already been given to that interaction (" + interaction.getAc() + "): " + id );
            String message = "An id has already been given to that interaction (" + interaction.getAc() + "): " + id;
//            throw new IllegalArgumentException( message );
            addMessage( message );
        }

        interactionIdMap.put( interaction, new Long( interactionIdentifier ) );

        return interactionIdentifier++;
    }

    public long getInteractionIdentifier( Interaction interaction ) {

        if ( interaction == null ) {
            throw new IllegalArgumentException( "You must give a non null interaction." );
        }

        if ( false == interactionIdMap.containsKey( interaction ) ) {
            throw new IllegalArgumentException( "Could not find an Identifier for that interaction (" + interaction.getAc() + ")." );
        }

        Long id = (Long) interactionIdMap.get( interaction );
        return id.longValue();
    }

    // Participant IDs

    public long getNextParticipantIdentifier( Component component ) {

        if ( component == null ) {
            throw new IllegalArgumentException( "You must give a non null component." );
        }

        if ( component2participantId.containsKey( component ) ) {
            Long id = (Long) component2participantId.get( component );
            String message = "An id has already been given to that component (" + component.getAc() + "): " + id;
//            throw new IllegalArgumentException( message );
            addMessage( message );
        }

        component2participantId.put( component, new Long( participantIdentifier ) );

        return participantIdentifier++;
    }

    public long getParticipantIdentifier( Component component ) {

        if ( component == null ) {
            throw new IllegalArgumentException( "You must give a non null component." );
        }

        if ( false == component2participantId.containsKey( component ) ) {

            throw new IllegalArgumentException( "Could not find an Identifier for that component (" + component.getAc() + ")." );
        }

        Long id = (Long) component2participantId.get( component );
        return id.longValue();
    }

    public void resetParticipantIdentifier() {
        participantIdentifier = FIRST_PARTICIPANT_ID;
        component2participantId.clear();
    }

    // Feature IDs

    public long getNextFeatureIdentifier( Feature feature ) {

        if ( feature == null ) {
            throw new IllegalArgumentException( "You must give a non null feature." );
        }

        if ( featureIdMap.containsKey( feature ) ) {
            Long id = (Long) featureIdMap.get( feature );
            String message = "An id has already been given to that feature (" + feature.getAc() + "): " + id;
//            throw new IllegalArgumentException( message );
            addMessage( message );
        }

        featureIdMap.put( feature, new Long( featureIdentifier ) );

        return featureIdentifier++;
    }

    public long getFeatureIdentifier( Feature feature ) {

        if ( feature == null ) {
            throw new IllegalArgumentException( "You must give a non null feature." );
        }

        if ( false == featureIdMap.containsKey( feature ) ) {

            throw new IllegalArgumentException( "Could not find an Identifier for that feature (" + feature.getAc() + ")." );
        }

        Long id = (Long) featureIdMap.get( feature );
        return id.longValue();
    }

    public void resetFeatureIdentifier() {
        featureIdentifier = FIRST_FEATURE_ID;
        featureIdMap.clear();
    }

    /////////////////////////////////
    // other public methods

    /**
     * Filtering rules:
     * ---------------
     *
     *     1) Annotations based on their CvTopic
     *     2) ...
     */

    /**
     * Load all CvTopic and check which one have been flagged 'obsolete'. Those CvTopic are automatically added to the
     * list of CvTopic to filter out.
     *
     * @param helper
     *
     * @throws IntactException
     */
    public void filterObsoleteAnnotationTopic( IntactHelper helper ) throws IntactException {

        // search all CvTopic
        Collection cvTopics = helper.search( CvTopic.class, "ac", null );

        // search for term obsolete
        CvTopic obsolete = null;
        for ( Iterator iterator = cvTopics.iterator(); iterator.hasNext() && obsolete == null; ) {
            CvTopic cvTopic = (CvTopic) iterator.next();

            if ( CvTopic.OBSOLETE.equals( cvTopic.getShortLabel() ) ) {
                obsolete = cvTopic;
            }
        }

        // if we have the obsolete term in the database, look for other CvTopic that have been flagged
        if ( obsolete != null ) {
            for ( Iterator iterator = cvTopics.iterator(); iterator.hasNext(); ) {
                CvTopic cvTopic = (CvTopic) iterator.next();

                for ( Iterator iterator1 = cvTopic.getAnnotations().iterator(); iterator1.hasNext(); ) {
                    Annotation annotation = (Annotation) iterator1.next();

                    // note: cvTopic is not null
                    if ( obsolete.equals( annotation.getCvTopic() ) ) {
                        // add this CvTopic to the list of those to filter out.
                        System.out.println( cvTopic.getShortLabel() + " was flagged obsolete, filter it out." );
                        if ( annotationFilters == null ) {
                            annotationFilters = new HashSet();
                        }

                        annotationFilters.add( cvTopic );
                    }
                }
            }
        } else {
            System.out.println( "The CvTopic( " + CvTopic.OBSOLETE + " ) could not be found. abort." );
        }
    }

    /**
     * Add cvTopic that will allow to filter annotation to be exported.
     *
     * @param cvTopic the IntAct CvTopic that should not be exported in PSI.
     */
    public void addAnnotationFilter( CvTopic cvTopic ) {

        if ( annotationFilters == null ) {
            annotationFilters = new HashSet();
        }

        annotationFilters.add( cvTopic );
    }

    /**
     * Determined if an annotation is exportable based on the filter that might have been setup.
     *
     * @param annotation the Annotation for which we want to assess if it is exportable.
     *
     * @return true is the Annotation is exportabl, false otherwise.
     */
    public boolean isExportable( Annotation annotation ) {

        if ( annotation.getCvTopic() != null && annotationFilters != null ) {
            if ( annotationFilters.contains( annotation.getCvTopic() ) ) {
                return false;
            }
        }

        // the no-export annotation remains filtered out. 
        return !AnnotationFilter.getInstance().isFilteredOut( annotation );
    }

    /**
     * Tells if the given IntAct experiment has already been generated in PSI.
     *
     * @param experiment the experiment we want to know if it has already been converted to PSI.
     *
     * @return true is the experiment has already been converted to XML, otherwise false.
     */
    public boolean isAlreadyDefined( Experiment experiment ) {

        return experiments.containsKey( experiment.getAc() );
    }

    /**
     * Declare in the session that the given experiment has been already converted in XML.
     *
     * @param experiment the experiment to declare as already converted.
     */
    public void declareAlreadyDefined( Experiment experiment ) {

        experiments.put( experiment.getAc(), null );
    }

    /**
     * Tells if the given IntAct Protein has already been generated in PSI.
     *
     * @param protein the protein we want to know if it has already been converted to PSI.
     *
     * @return true is the protein has already been converted to XML, otherwise false.
     */
    public boolean isAlreadyDefined( Interactor interactor ) {

        return interactors.containsKey( interactor.getAc() );
    }

    /**
     * Declare in the session that the given protein has been already converted in XML.
     *
     * @param protein the protein to declare as already converted.
     */
    public void declareAlreadyDefined( Interactor interactor ) {

        interactors.put( interactor.getAc(), null );
    }

    /**
     * return the current cluster ID suffix and increment it by 1.
     *
     * @return a new cluster ID suffix.
     */
    synchronized public int getNextClusterIdSuffix() {

        return ++clusterIdSuffix;
    }

    /**
     * return the current cluster ID suffix.
     *
     * @return a cluster ID suffix.
     */
    synchronized public int getClusterIdSuffix() {

        return clusterIdSuffix;
    }

    ///////////////////////////
    // Message management

    public void addMessage( String message ) {
        messages.add( message );
    }

    public List getMessages() {
        return Collections.unmodifiableList( messages );
    }

    public boolean hasMessages() {
        return ( false == messages.isEmpty() );
    }

    /**
     * If any message have been stored in the session, it prints a report.
     *
     * @param ps where the report should be written (eg. System.err).
     */
    public void printMessageReport( PrintStream ps ) {

        if ( hasMessages() ) {

            List messages = getMessages();

            ps.println( "*******************************************************************" );
            ps.println( "***********************   M E S S A G E S   ***********************" );
            ps.println( "*******************************************************************" );

            int i = 1;
            for ( Iterator iterator = messages.iterator(); iterator.hasNext(); ) {
                String msg = (String) iterator.next();
                ps.println( "(" + ( i++ ) + ") " + msg );
            }

            ps.println( "___________________________________________________________________" );
            ps.println( messages.size() + " message" + ( messages.size() > 1 ? "s" : "" ) + "." );

            ps.println( "*******************************************************************" );

        } else {
            ps.println( "no message." );
        }
    }

    /////////////////////////////
    // XML convenience methods

    public Element createElement( String name ) {
        return getPsiDocument().createElement( name );
    }

    public Text createTextNode( String text ) {
        return getPsiDocument().createTextNode( text );
    }

}