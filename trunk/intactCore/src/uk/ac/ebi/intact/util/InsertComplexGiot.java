/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.fileParsing.beans.GiotLineBean;
import uk.ac.ebi.intact.util.fileParsing.BeanStreamReader;
import uk.ac.ebi.intact.util.fileParsing.SerializationHelper;
import uk.ac.ebi.sptr.flatfile.yasp.YASP;
import uk.ac.ebi.sptr.flatfile.yasp.YASPException;
import uk.ac.ebi.sptr.flatfile.yasp.EntryIterator;
import uk.ac.ebi.interfaces.sptr.SPTREntry;

import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.Exception;

/**
 * Insert of the Giot data in IntAct
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InsertComplexGiot {

    ///////////////////////////////
    // Constants
    ///////////////////////////////

    public static final String ANNOTATION_cDNA = "Region of the cDNA involved in the interaction was: ";

    public static final String ANNOTATION_NEED_TO_BE_REVISITED = "Revisit when UniProt demerges.";

    public static final String ANNOTATION_cDNA_BAIT_5_UTR = ANNOTATION_cDNA + "bait5UTR";
    public static final String ANNOTATION_cDNA_BAIT_3_UTR = ANNOTATION_cDNA + "bait3UTR";
    public static final String ANNOTATION_cDNA_PREY_5_UTR = ANNOTATION_cDNA + "prey5UTR";
    public static final String ANNOTATION_cDNA_PREY_3_UTR = ANNOTATION_cDNA + "prey3UTR";

    public static final String ANNOTATION_CONFIDENCE  = "Author confidence score for the interaction was: ";
    public static final String ANNOTATION_ORIENTATION = "Interaction observed in both orientations prey to bait and bait to prey for the two proteins.";

    ////////////////////////
    // Instance variables
    ////////////////////////

    private BeanStreamReader beanReader;

    /**
     * CG -> SPTR
     */
    private HashMap cg2sptrMapping;

     /**
     * SPTR -> GENE
     */
    private HashMap sptr2geneMapping;

    /**
     * All beans which gave problem while trying to process them.
     */
    private Collection errorBeans = new ArrayList();

    private IntactHelper helper;

    private UpdateProteinsI proteinFactory;

    /**
     * Intact common objects
     */
    private Institution       owner;
    private BioSource         yeast;
    private Collection        allKnownDrosophila;
    private CvDatabase        uniprot;
    private Experiment        giotExperiment;
    private CvInteractionType interactionType;

    private CvTopic           comment;
    private CvTopic           remark;
    private Annotation        needToBeRevisitedWhenUniprotDemerges;
    private Annotation        biDirectional;
    private Annotation        bait5utr, bait3utr;
    private Annotation        prey5utr, prey3utr;

    private CvAliasType       geneNameAliasType;
    private CvComponentRole   bait;
    private CvComponentRole   prey;

    /**
     * Non redondant list of unmapped CGs encountered during the processing.
     */
    private HashSet idNotMapped = new HashSet();


    //////////////////////////
    // Instance constructors
    //////////////////////////

    public InsertComplexGiot ( String giotUrl,
                               String cgSptrMappingUrl,
                               String sptrGeneMappingUrl,
                               IntactHelper helper )
            throws MalformedURLException,
            IOException,
            IntactException, NoSuchMethodException,
            UpdateProteinsI.UpdateException {

        this.helper = helper;

        // create missing common objects: fileParsing Experiment, confidence CvAliasType, geneName CvAliasType
        loadCommonObjects();
        System.out.println ( "Common objects loaded from database..." );

        proteinFactory = new UpdateProteins( helper );
        proteinFactory.setLocalTransactionControl( false );
        System.out.println ( "Protein factory created..." );


        try {
            cg2sptrMapping = getFlybase2SptrMapping( cgSptrMappingUrl );
            System.out.println ( "Mapping FlyBase - SPTR loaded from disk: " + cgSptrMappingUrl + ", it contains " +
                                 cg2sptrMapping.size() + " associations.");
        } catch ( Exception e ) {
            e.printStackTrace ();
            System.exit( 0 );
        }

        try {
            sptr2geneMapping = getFlybase2SptrMapping( sptrGeneMappingUrl );
            System.out.println ( "Mapping SPTR -> GeneName loaded from disk: " + sptrGeneMappingUrl + ", it contains " +
                                 sptr2geneMapping.size() + " associations.");
        } catch ( Exception e ) {
            e.printStackTrace ();
            System.exit( 0 );
        }



        beanReader  = new BeanStreamReader( giotUrl, GiotLineBean.class, 1 );
        System.out.println ( "Giot data file open from disk: " + giotUrl );
    }


    ////////////////////////////
    // Public Instance methods
    ////////////////////////////

    public BeanStreamReader getLineBrowser () {
        return beanReader;
    }

    public void displayStatistics() {
        System.out.println( "\n\n#Giot line processed: " + beanReader.getItemCount() + "\n");
    }

    public void displayUnmappedIds() {
        System.out.println ( "Unmapped CGs:" );
        for ( Iterator iterator = idNotMapped.iterator (); iterator.hasNext (); ) {
            String cg = (String) iterator.next ();
            System.out.println ( cg );
        }
    }

    public void process ( String outputFilename )
            throws IOException, IntactException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        long count = 0;
        GiotLineBean bean = null;

        BufferedWriter out = new BufferedWriter( new FileWriter( outputFilename ) );

        out.write( "# output generated by InsertComplexGiot - that line is needed if you want to reprocessed it later using the same loader !" );
        out.newLine();
        out.flush();

        while ( ( bean = (GiotLineBean) beanReader.readBean() ) != null ) {
            count++;

            if ((count % 500) == 0) displayStatistics();

            /**
             * Process those data in IntAct ... each line is enclosed in a transaction
             *
             * why JDBC_TX ? we might insert Annotation related to an AnnotatedObject,
             *               the OBJECT_TX doesn't allow us to do so because it generates
             *               database constraint violation. hence, the AC of the Annotated object
             *               is needed when the Annotation is created.
             */
            try {
//                helper.startTransaction( BusinessConstants.JDBC_TX );
                processSingleItem( bean );
                System.out.print ( "C" );
//                helper.finishTransaction();
            } catch (IntactException e) { // all other Exception would be generated

                System.out.print ( "." );
                e.printStackTrace();

                errorBeans.add( bean );

                out.write( bean.getLine() );
                out.newLine();
                out.flush();

                if ( helper.isInTransaction() ) {
                    System.err.println ( "There is a running transaction." );
                    helper.undoTransaction();
                    System.err.println ( "Has been rolled back." );
                }
            }
        } // while

        out.close();

        displayStatistics();
        displayUnmappedIds();
    }


    ////////////////////////////
    // Private Instance methods
    ////////////////////////////

    private HashMap getFlybase2SptrMapping ( String filename )
            throws IOException, ClassNotFoundException {

        SerializationHelper helper = new SerializationHelper();
        HashMap mapping = (HashMap) helper.deserialize( filename );
        helper = null;
        return mapping;
    }

    private void loadCommonObjects() throws IntactException {

        owner = (Institution) helper.getObjectByLabel( Institution.class, "EBI" );
        if ( owner == null ){
            throw new IntactException ( "Could not find the Institution: EBI. Stop processing." );
        }

        uniprot = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "uniprot" );
        if ( uniprot == null ){
            throw new IntactException ( "Could not find the CvDatabase: uniprot. Stop processing." );
        }

        BioSourceFactory bioSourceFactory = new BioSourceFactory( helper, owner );

        yeast = bioSourceFactory.getValidBioSource( "4932" );

        allKnownDrosophila = new ArrayList( 8 );
        allKnownDrosophila.add( bioSourceFactory.getValidBioSource( "7215" ) );
        allKnownDrosophila.add( bioSourceFactory.getValidBioSource( "7220" ) );
        allKnownDrosophila.add( bioSourceFactory.getValidBioSource( "7224" ) );
        allKnownDrosophila.add( bioSourceFactory.getValidBioSource( "7226" ) );
        allKnownDrosophila.add( bioSourceFactory.getValidBioSource( "7227" ) );
        allKnownDrosophila.add( bioSourceFactory.getValidBioSource( "7238" ) );
        allKnownDrosophila.add( bioSourceFactory.getValidBioSource( "7240" ) );
        allKnownDrosophila.add( bioSourceFactory.getValidBioSource( "7245" ) );

        interactionType = (CvInteractionType) helper.getObjectByLabel( CvInteractionType.class, "aggregation" );
        if ( interactionType == null ){
            throw new IntactException ( "Could not find the CvInteractionType: aggregation. Stop processing." );
        }

        giotExperiment = (Experiment) helper.getObjectByLabel( Experiment.class, "giot-2003-ex1" );
        if ( giotExperiment == null ){
            // throw new IntactException ( "Could not find the Experiment: fileParsing. Stop processing." );
            giotExperiment = new Experiment( owner, "giot-2003-ex1", yeast );


            // TODO fill up here the details of that experiment ... cf. Jyoti !


            helper.create( giotExperiment );
            System.out.println ( "Create Giot experiment with label: giot-2003" );
        } else {
            System.out.println ( "giot-2003-ex1 already exist, use that one." );

            if ( giotExperiment.getBioSource() == null) {
                System.out.println ( "giot-2003-ex1 BioSource's is not set !" );
//                giotExperiment.setBioSource( yeast );
//                helper.update( giotExperiment );
//                System.out.println ( "Giot's BioSource wasn't set, update it to: " + yeast.getShortLabel() );
            }
        }

        // MUST be done before Annotation because it might be used
        comment = (CvTopic) helper.getObjectByLabel( CvTopic.class, "comment" );
        if ( comment == null ){
            throw new IntactException ( "Could not find the CvTopic: comment. Stop processing." );
        }

        remark = (CvTopic) helper.getObjectByLabel( CvTopic.class, "remark" );
        if ( remark == null ){
            throw new IntactException ( "Could not find the CvTopic: remark. Stop processing." );
        }

        Collection annotations = null;


        annotations = helper.search( Annotation.class.getName(), "annotationText", ANNOTATION_NEED_TO_BE_REVISITED );
        if ( annotations.size() < 1 ){
            // create it
            needToBeRevisitedWhenUniprotDemerges = new Annotation( owner, remark );
            needToBeRevisitedWhenUniprotDemerges.setAnnotationText( ANNOTATION_NEED_TO_BE_REVISITED );
            helper.create( needToBeRevisitedWhenUniprotDemerges );
        } else {
            needToBeRevisitedWhenUniprotDemerges = (Annotation) annotations.iterator().next(); // 1 at most
        }

        annotations = helper.search( Annotation.class.getName(), "annotationText", ANNOTATION_cDNA_BAIT_5_UTR );
        if ( annotations.size() < 1 ){
            // create it
            bait5utr = new Annotation( owner, comment );
            bait5utr.setAnnotationText( ANNOTATION_cDNA_BAIT_5_UTR );
            helper.create( bait5utr );
        } else {
            bait5utr = (Annotation) annotations.iterator().next(); // 1 at most
        }

        annotations = helper.search( Annotation.class.getName(), "annotationText", ANNOTATION_cDNA_BAIT_3_UTR );
        if ( annotations.size() < 1 ){
            // create it
            bait3utr = new Annotation( owner, comment );
            bait3utr.setAnnotationText( ANNOTATION_cDNA_BAIT_3_UTR );
            helper.create( bait3utr );
        } else {
            bait3utr = (Annotation) annotations.iterator().next(); // 1 at most
        }

        annotations = helper.search( Annotation.class.getName(), "annotationText", ANNOTATION_cDNA_PREY_5_UTR );
        if ( annotations.size() < 1 ){
            // create it
            prey5utr = new Annotation( owner, comment );
            prey5utr.setAnnotationText( ANNOTATION_cDNA_PREY_5_UTR );
            helper.create( prey5utr );
        } else {
            prey5utr = (Annotation) annotations.iterator().next(); // 1 at most
        }

        annotations = helper.search( Annotation.class.getName(), "annotationText", ANNOTATION_cDNA_PREY_3_UTR );
        if ( annotations.size() < 1 ){
            // create it
            prey3utr = new Annotation( owner, comment );
            prey3utr.setAnnotationText( ANNOTATION_cDNA_PREY_3_UTR );
            helper.create( prey3utr );
        } else {
            prey3utr = (Annotation) annotations.iterator().next(); // 1 at most
        }


        annotations = helper.search( Annotation.class.getName(), "annotationText", ANNOTATION_ORIENTATION );
        if ( annotations.size() < 1 ){
            // create it !
            biDirectional = new Annotation( owner, comment );
            biDirectional.setAnnotationText( ANNOTATION_ORIENTATION );
            helper.create( biDirectional );
        } else {
            biDirectional = (Annotation) annotations.iterator().next(); // 1 at most
        }

        geneNameAliasType = (CvAliasType) helper.getObjectByLabel( CvAliasType.class, "gene-name" );
        if ( geneNameAliasType == null ){
            throw new IntactException ( "Could not find the CvAliasType: gene-name. Stop processing." );
        }


        bait = (CvComponentRole) helper.getObjectByLabel( CvComponentRole.class, "bait" );
        if ( bait == null ){
            throw new IntactException ( "Could not find the CvComponentRole: bait. Stop processing." );
        }

        prey = (CvComponentRole) helper.getObjectByLabel( CvComponentRole.class, "prey" );
        if ( prey == null ){
            throw new IntactException ( "Could not find the CvComponentRole: prey. Stop processing." );
        }
    }

    public final String getUrl(String sptrAC) {

        // example: http://srs.ebi.ac.uk/srs7bin/cgi-bin/wgetz?-e+[SWALL-acc:P12345]+-vn+2+-ascii
        String url = "http://srs.ebi.ac.uk/srs7bin/cgi-bin/wgetz?-e+[SWALL-acc:"
                + sptrAC + "]+-vn+2+-ascii" ;

        return url ;
    }


    public String getGNLine( String ac ) {

        // Check if it is a splice variant if so, truncate the -1, -2 ...
        int index = -1;
        if (( index = ac.indexOf( "-" )) != -1) {
            // splice variant
            ac = ac.substring( 0, index );
        }

        /**
         * Try to read from cache before to use Aristotle.
         */
        if ( sptr2geneMapping != null ){
            String gene = (String) sptr2geneMapping.get( ac );
            if ( gene != null )
                return gene; // got it !
        }


        String sourceUrl = getUrl( ac );
        String gnLine = null;

        try {

            URL url = new URL ( sourceUrl );


            EntryIterator entryIterator = YASP.parseAll( url );

            /**
             * C A U T I O N
             * -------------
             *  The YASP Iterator has to taken with carefulness.
             * .next() method gives you the current element
             * .hasNext() loads the next elements and says you if there was one.
             * So, calling twice .hasNext() without processing in between would
             * make you miss an element.
             */
            if (entryIterator.hasNext()) {

                // Check if there is any exception remaining in the Entry before to use it
                if (entryIterator.hadException()) {
                    Exception originalException = entryIterator.getException().getOriginalException();

                    if (originalException != null) {
                        originalException.printStackTrace();
                    } else {
                        entryIterator.getException().printStackTrace();
                    }

                    return null;
                }

                // get the SPTREntry
                SPTREntry sptrEntry = (SPTREntry) entryIterator.next();

                if (sptrEntry == null) {
                    System.out.println("\n\nSPTR entry is NULL ... skip it");
                    return null;
                }

                // do something
                 String[][] genes = sptrEntry.getGenes();
                 gnLine = genes[ 0 ][ 0 ];
            }

        } catch (YASPException e) {
            e.getOriginalException().printStackTrace();

        } catch (MalformedURLException e) {
            System.out.println ("URL error: " + sourceUrl);
            e.printStackTrace();
            System.out.println ("Please provide a valid URL");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println ( "Error while processing an SPTREntry");
        }

        return gnLine;
    }




    /**
     * Record the data contained in the bean in the IntAct database.
     *
     * @param bean the data contained in one line of the Giot data file.
     * @throws uk.ac.ebi.intact.business.IntactException
     */
    private void processSingleItem ( GiotLineBean bean ) throws IntactException {

        /**
         * Protein 1
         */
        String sptr1 = (String) cg2sptrMapping.get( bean.getCg1() );
        if( sptr1 == null ) {
            idNotMapped.add( bean.getCg1() );
            throw new IntactException ( "\nCould not find the cg2sptrMapping corresponding to CG: " + bean.getCg1() );
        }

        // TODO fix multiple proteins bug here !
        Protein protein1 = null;
        Collection tmp = null;

        if ( sptr1.indexOf("-") == -1 ) {
            // protein
            tmp = helper.getObjectsByXref( Protein.class, sptr1 );
        } else {
            // splice variant
            tmp = helper.search( Protein.class.getName(), "shortLabel", sptr1 );
        }

        if ( tmp != null ) {

            for ( Iterator iterator = tmp.iterator (); iterator.hasNext (); ) {
                Protein protein = (Protein) iterator.next ();
                if ( allKnownDrosophila.contains( protein.getBioSource() ) ){
                    protein1 = protein;
                    System.out.print ( "p" );
                    break;
                }
            }
        }

        if ( protein1 == null ) {
            // try to create it
            Collection proteins = proteinFactory.insertSPTrProteins( sptr1, null, true );
            if ( proteins == null || proteins.isEmpty() ) {
                throw new IntactException( "Could not create the protein (shortlabel:"+ sptr1 +"). abort." );
            } else {
                // select the right one if many have been created !
                // beware, it could be a protein or s splice variant.
                if (sptr1.indexOf( "-" ) == -1) {
                    // it is a protein ... check for SPTR Xref
                    for ( Iterator iterator = proteins.iterator (); protein1 == null && iterator.hasNext (); ) {
                        Protein p = (Protein) iterator.next ();
                        for ( Iterator iterator2 = p.getXrefs ().iterator (); iterator2.hasNext (); ) {
                            Xref xref = (Xref) iterator2.next ();
                            if (xref.getCvDatabase().equals( uniprot ) && xref.getPrimaryId().equals( sptr1 )) {
                               protein1 = p;
                            }
                        }
                    }
                } else {
                    // that's a splice variant ... check for shortlabel
                    for ( Iterator iterator = proteins.iterator (); protein1 == null && iterator.hasNext (); ) {
                        Protein p = (Protein) iterator.next ();
                        if ( p.getShortLabel().equalsIgnoreCase( sptr1 ) ) {
                            protein1 = p;
                        }
                    }
                }

                for ( Iterator iterator = proteins.iterator (); protein1 == null && iterator.hasNext (); ) {
                    Protein p = (Protein) iterator.next ();
                    if ( p.getShortLabel().equalsIgnoreCase( sptr1 ) ) {
                        protein1 = p;
                    }
                }

                if( protein1 == null ){
                    // could not find it in the retreived set of proteins
                    throw new IntactException( proteins.size() + " proteins have been created from label:"+sptr1+", but could not find one with that shortLabel. abort." );
                } else {
                    System.out.print ( "P" );
                }
            }
        }


        /**
         * Protein 2
         */
        // TODO Code could be factorised ... protein 1 and 2.
        String sptr2 = (String) cg2sptrMapping.get( bean.getCg2() );
        if( sptr2 == null ) {
            idNotMapped.add( bean.getCg2() );
            throw new IntactException ( "\nCould not find the cg2sptrMapping corresponding to CG: " + bean.getCg2() );
        }

        Protein protein2 = null;
        tmp = null;

        if ( sptr2.indexOf("-") == -1 ) {
            // protein
            tmp = helper.getObjectsByXref( Protein.class, sptr2 );
        } else {
            // splice variant
            tmp = helper.search( Protein.class.getName(), "shortLabel", sptr2 );
        }

        if ( tmp != null ) {

            for ( Iterator iterator = tmp.iterator (); iterator.hasNext (); ) {
                Protein protein = (Protein) iterator.next ();
                if ( allKnownDrosophila.contains( protein.getBioSource() ) ){
                    protein2 = protein;
                    System.out.print ( "p" );
                    break;
                }
            }
        }

        if ( protein2 == null ) {
            // try to create it
            Collection proteins = proteinFactory.insertSPTrProteins( sptr2, null, true );
            if ( proteins == null || proteins.isEmpty() ) {
                throw new IntactException( "Could not create the protein (shortlabel:"+ sptr2 +"). abort." );
            } else {
                // select the right one if many have been created !
                // beware, it could be a protein or s splice variant.
                if (sptr2.indexOf( "-" ) == -1) {
                    // it is a protein ... check for SPTR Xref
                    for ( Iterator iterator = proteins.iterator (); protein2 == null && iterator.hasNext (); ) {
                        Protein p = (Protein) iterator.next ();
                        for ( Iterator iterator2 = p.getXrefs ().iterator (); iterator2.hasNext (); ) {
                            Xref xref = (Xref) iterator2.next ();
                            if (xref.getCvDatabase().equals( uniprot ) && xref.getPrimaryId().equals( sptr2 )) {
                               protein2 = p;
                            }
                        }
                    }
                } else {
                    // that's a splice variant ... check for shortlabel
                    for ( Iterator iterator = proteins.iterator(); protein2 == null && iterator.hasNext (); ) {
                        Protein p = (Protein) iterator.next ();
                        if ( p.getShortLabel().equalsIgnoreCase( sptr2 ) ) {
                            protein2 = p;
                        }
                    }
                }

                if( protein2 == null ){
                    // could not find it in the retreived set of proteins
                    throw new IntactException( proteins.size() + " proteins have been created from label:"+ sptr2 +", but could not find one with that shortLabel. abort." );
                } else {
                    System.out.print ( "P" );
                }
            }
        }

        String geneName1 = bean.getP1();
        addAlias( protein1, geneName1 );

        String geneName2 = bean.getP2();
        addAlias( protein2, geneName2 );

        boolean needToBeRevisited = false;

        String sptrGeneName1 = getGNLine( sptr1 );
        if( hasSeveralCGs( sptrGeneName1 ) ) {
            needToBeRevisited = true;
        }
        if (sptrGeneName1 == null) throw new IntactException( "No gene name found protein label: " + sptr1 );

        String sptrGeneName2 = getGNLine( sptr2 );
        if( needToBeRevisited == false && hasSeveralCGs( sptrGeneName2 ) ) {
            needToBeRevisited = true;
        }
        if (sptrGeneName2 == null) throw new IntactException( "No gene name found protein label: " + sptr2 );

        // TODO get rid of this !
        String nameBP = "";
        String namePB = "";


        Interaction interactionBaitPrey = null;
        if ( bean.getBaitPrey() > 0 ) {
            // Create an interaction where cg1 is bait and cg2 is prey
            interactionBaitPrey = createInteraction( protein1, protein2, sptrGeneName1, sptrGeneName2 );
            nameBP = interactionBaitPrey.getShortLabel();
        }


        Interaction interactionPreyBait = null;
        if ( bean.getPreyBait() > 0 ) {
            // Create an interaction where cg1 is prey and cg2 is bait
            interactionPreyBait = createInteraction( protein2, protein1, sptrGeneName2, sptrGeneName1 );
            namePB = interactionPreyBait.getShortLabel();
        }

        if( needToBeRevisited ) {
            System.out.println ( "Check that out: " + nameBP );
            System.out.println ( "Check that out: " + namePB );
           addAnnotation( interactionBaitPrey, needToBeRevisitedWhenUniprotDemerges );
           addAnnotation( interactionPreyBait, needToBeRevisitedWhenUniprotDemerges );
        }

        if ( interactionPreyBait != null || interactionBaitPrey != null ) {

            switch ( bean.getNorient() ) {
                case 0: // should not occur, means neither bait->prey nor prey->bait
                    System.err.println ( "Line " + beanReader.getItemCount() + ": data without orientation (norient=0)" );
                    break;

                case 1: // monodirectional, means either bait->prey or prey->bait
                    // do nothing
                    break;

                case 2: // bidirectional, means either bait->prey and prey->bait
                    addAnnotation( interactionBaitPrey, biDirectional );
                    addAnnotation( interactionPreyBait, biDirectional );
                    break;

                default:
                    System.err.println ( "noorient has an unsupported value: " + bean.getNorient() );
            }

            if ( bean.getBait3utr() > 0 ) {
                // Add a comment containing bait3utr
                addAnnotation( interactionPreyBait, bait3utr );
                addAnnotation( interactionBaitPrey, bait3utr );
            }

            if ( bean.getBait5utr() > 0 ) {
                // Add a comment containing bait5utr
                addAnnotation( interactionPreyBait, bait5utr );
                addAnnotation( interactionBaitPrey, bait5utr );
            }

            if ( bean.getPrey3utr() > 0 ) {
                // Add a comment containing prey3utr
                addAnnotation( interactionPreyBait, prey3utr );
                addAnnotation( interactionBaitPrey, prey3utr );
            }

            if ( bean.getPrey5utr() > 0 ) {
                // Add a comment containing prey5utr
                addAnnotation( interactionPreyBait, prey5utr );
                addAnnotation( interactionBaitPrey, prey5utr );
            }

            // add author confidence to the created interactions.
            String confidence = bean.getCconf();
            if ( confidence != null && ! confidence.equals( "" ) ) {
                Annotation confidenceAnnotation = new Annotation( owner, comment );
                confidenceAnnotation.setAnnotationText( ANNOTATION_CONFIDENCE + bean.getCconf() );
                helper.create( confidenceAnnotation );
                addAnnotation( interactionPreyBait, confidenceAnnotation );
                addAnnotation( interactionBaitPrey, confidenceAnnotation );
            } else {
                System.out.println ( "No confidence in the bean ... don't save it." );
            }

            // Add interactions to the Experiment.
            if ( interactionBaitPrey != null ) {
                giotExperiment.addInteraction( interactionBaitPrey );
            }
            if ( interactionPreyBait != null ) {
                giotExperiment.addInteraction( interactionPreyBait );
            }

        } else {
            System.err.println ( "WARNING: No interaction created out of this line." );
            System.err.println ( bean );
        }
    }

    /**
     * Check if a geneName contains several CGs.
     * If yes, if has to be a pipe (|) separated list.
     *
     * @param geneName the gene name
     * @return true if there is several CGs, else false.
     */
    private boolean hasSeveralCGs ( String geneName ) {

        if(geneName == null || geneName.equals( "" ))
            return false;

        StringTokenizer st = new StringTokenizer( geneName, "|" );
        int count = 0;
        while( st.hasMoreTokens() ) {
            st.nextToken();
            count++;
        }

        if( count > 1 )
            return true;
        else
            return false;
    }

    /**
     * Add an Annotation to the interaction given in parameter and update it.
     *
     * @param interaction the interaction to which we want to add an Annotation (only if not null)
     * @param annotation  the Annotation to add to the interaction.
     * @throws uk.ac.ebi.intact.business.IntactException if something goes wrong while making the changes persistent.
     */
    private void addAnnotation( Interaction interaction, Annotation annotation ) throws IntactException {
        if ( interaction != null ) {
            Collection annotations = interaction.getAnnotations();
            if ( annotations.contains( annotation ) )
                return;

            interaction.addAnnotation( annotation );

            if ( helper.isPersistent( interaction ) ) {
                helper.update( interaction );
            } else {
                helper.create( interaction );
            }
        }
    }


    /**
     * Creata an interaction shortlabel.
     * <br>
     * Take care about the maximum length of the field.
     *
     *
     * @param bait
     * @param prey
     * @return
     */
    public String createInteractionShortLabel(String bait, String prey) throws IntactException {

        // convert bad caracters ('-', ' ', '.') to '_'
        bait = bait.toLowerCase();
        bait = SearchReplace.replace(bait, "-", "_");
        bait = SearchReplace.replace(bait, " ", "_");
        bait = SearchReplace.replace(bait, ".", "_");

        prey = prey.toLowerCase();
        prey = SearchReplace.replace(prey, "-", "_");
        prey = SearchReplace.replace(prey, " ", "_");
        prey = SearchReplace.replace(prey, ".", "_");

        int count = 0;
        String sCount = "" + count;
        String _bait = bait;
        String _prey = prey;
        boolean foundLabel = false;
        String label = null;

        while ( ! foundLabel ) {

            sCount = "" + (++count);
            label = _bait + "-" + _prey + "-" + sCount;

            // check if truncation needed.
            while ( label.length() > 20 ) {
                if ( _bait.length() > _prey.length() ) {
                    _bait = _bait.substring( 0, _bait.length() - 1 ); // remove last charachter
                } else {
                    _prey = _prey.substring( 0, _prey.length() - 1 ); // remove last charachter
                }

                label = _bait + "-" + _prey + "-" + sCount;
            } // while

            // we have the right label's size now ... search for existing one !
            Collection interactions = helper.search(Interaction.class.getName(), "shortlabel", label);
            if ( interactions.size() == 0 ) {
                // This label is not used yet, exit the loop
                foundLabel = true;
            }
        } // while

        return label;
    }

    /**
     * Create an interaction
     *
     * @param proteinBait
     * @param proteinPrey
     * @param pBait
     * @param pPrey
     * @return
     * @throws IntactException
     */
    private Interaction createInteraction( Protein proteinBait,
                                           Protein proteinPrey,
                                           String pBait,
                                           String pPrey) throws IntactException {

        // check if it already exists
        Interaction interaction = null;
        String interactionLabel = createInteractionShortLabel( pBait, pPrey );

        interaction = (Interaction) helper.getObjectByLabel( Interaction.class, interactionLabel );
        if ( interaction != null ) {
            // Should no happens
            System.out.println ( "Interaction("+ interactionLabel +") already exist!" );
            throw new IntactException( "Interaction("+ interactionLabel +") already exist! Can't insert it twice !" );
        }

        Collection experiments = new ArrayList( 1 );
        experiments.add( giotExperiment );

        interaction = new InteractionImpl( experiments,
                                       new ArrayList( 2 ), // we know there are only 2 components to add !
                                       interactionType,
                                       interactionLabel,
                                       owner );
        interaction.setFullName( "Interaction detected by Y2H." );
        interaction.setKD( new Float( 0 ) );
        interaction.setBioSource( yeast );

        helper.create( interaction );

        Component component1 = new Component( owner, interaction, proteinBait, bait );
        Component component2 = new Component( owner, interaction, proteinPrey, prey );
        helper.create( component1 );
        helper.create( component2 );


        interaction.addComponent( component1 );
        interaction.addComponent( component2 );
        if ( helper.isPersistent( interaction ) ) {
            helper.update( interaction );
        } else {
            helper.create( interaction );
        }

        return interaction;
    }

    private void addAlias ( Protein protein, String geneName ) throws IntactException {
        Collection aliases = protein.getAliases();
        boolean found = false;
        for ( Iterator iterator = aliases.iterator (); iterator.hasNext (); ) {
            Alias alias = (Alias) iterator.next ();
            if ( alias.getName().equalsIgnoreCase( geneName ) ) {
                found = true;
                break;
            }
        }

        if ( ! found ) {
            // create a new alias as it doesn't exist.
            Alias alias = new Alias( owner, protein, geneNameAliasType, geneName);
            helper.create( alias );
        }
    }




    /**
     * D E M O
     *
     * @param args [0] fileParsing URL from which we'll read the data
     */
    public static void main ( String[] args ) throws Exception {

        // Check parameters
        if ( args.length < 4 ) {
            // usage
            System.err.println ( "Usage: InsertComplexGiot <Giot source URL> <CG - SPTR Mapping filename> <SPTR - GENE Mapping filename> <excluded_interaction filename>" );
            System.exit( 1 );
        }

        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.
        // Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        System.out.println ( "JVM max heap size: " + heapMaxSize / (1024 * 1024) + "M" );

        IntactHelper helper = new IntactHelper();
        System.out.println ( "Helper created (User: "+ helper.getDbUserName() +", DB: "+ helper.getDbName() +")" );

        System.out.println ( "Parameters: " );
        System.out.println ( "Interaction file:        " + args[0]);
        System.out.println ( "Mapping CG   -> SPTR URL:" + args[1]);
        System.out.println ( "Mapping SPTR -> GENE URL:" + args[2]);
        System.out.println ( "Excluded lines:          " + args[3]);

        InsertComplexGiot tool = new InsertComplexGiot( args[0],
                                                        args[1],
                                                        args[2],
                                                        helper );

        System.out.println ( "Start process Giot file..." );
        tool.process( args[3] );
        System.out.println ( "Finished processing." );

        helper.closeStore();
        System.out.println ( "Data store was closed." );

        System.exit( 0 );
    } // main
} // InsertComplexGiot
