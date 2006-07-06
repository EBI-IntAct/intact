/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.imex.export;

import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.dataConversion.PsiVersion;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlI;
import uk.ac.ebi.intact.application.dataConversion.util.DisplayXML;
import uk.ac.ebi.intact.application.imex.helpers.CvHelper;
import uk.ac.ebi.intact.application.imex.helpers.ExperimentHelper;
import uk.ac.ebi.intact.application.imex.helpers.InteractionHelper;
import uk.ac.ebi.intact.application.imex.helpers.PublicationHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>06-Feb-2006</pre>
 */
public class IMExExport {

    /**
     * Simple representation of a Date.
     * <p/>
     * Will be used to name our IMEx files.
     */
    private static final SimpleDateFormat SIMPLE_DATE_FORMATER = new SimpleDateFormat( "yyyy-MM-dd" );

    //////////////////////////
    // Utility methods

    public static File getTemporaryFile() throws IOException {

        // get temporary directory
        File tempDirectory = new File( System.getProperty( "java.io.tmpdir", "tmp" ) );

        // create temp file
        File tempFile = File.createTempFile( "IMEx.export", ".xml", tempDirectory );
        tempFile.deleteOnExit();

        return tempFile;
    }

    /**
     * Write a Document into a File.
     *
     * @param document the XML document.
     * @param out      the output file.
     *
     * @throws IOException if an I/O error occurs.
     */
    public static void save( Document document, File out ) throws IOException {

//        if ( ! out.exists() ) {
//            out.createNewFile();
//        }

        System.out.println( "Writing to " + out.getAbsolutePath() );
        // prepare a file writer.
        Writer writer = new BufferedWriter( new FileWriter( out ) );

        // Write the content in the file (indented !!)
        DisplayXML.write( document, writer, "  " );

        writer.flush();

        // Close the file
        writer.close();
    }

    /**
     * Compress a file using GZIP.
     *
     * @param in  file to compress
     * @param out compressed file
     *
     * @throws IOException if an I/O error occurs.
     */
    public static void compressUsingGZip( File in, File out ) throws IOException {

        // Create the GZIP output stream
        GZIPOutputStream gzout = new GZIPOutputStream( new FileOutputStream( out ) );

        // Open the input file
        FileInputStream fis = new FileInputStream( in );

        // Transfer bytes from the input file to the GZIP output stream
        byte[] buf = new byte[1024];
        int len;
        while ( ( len = fis.read( buf ) ) > 0 ) {
            gzout.write( buf, 0, len );
        }
        fis.close();

        // Complete the GZIP file
        gzout.finish();
        gzout.close();
    }

    public static int exportPublications( UserSessionDownload session,
                                          IntactHelper helper,
                                          Collection<String> pmids ) throws IntactException {
        int success = 0;

        for ( String pmid : pmids ) {

            System.out.println( "Processing publication " + pmid + " ..." );
            if ( exportPublication( session, helper, pmid ) ) {
                success++;
            }
        }

        return success;
    }

    private static String getToday() {
        return SIMPLE_DATE_FORMATER.format( new Date() ); // YYYY-MM-DD
    }

    public static int publicationExported = 0;

    public static boolean exportPublication( UserSessionDownload session,
                                             IntactHelper helper,
                                             String pmid ) throws IntactException {

        Collection<Publication> publications = helper.search( Publication.class, "pmid", pmid );
        if ( publications.isEmpty() ) {
            System.out.println( "ERROR - A Publication Object should exist prior export to IMEx. Skip publication." );
            return false;
        }

        // fetch the only publication.
        Publication publication = publications.iterator().next();

        // for information, display the export history of that publication.
        List<Annotation> exportHistory = PublicationHelper.getExportHistory( helper, publication );
        if ( exportHistory.isEmpty() ) {
            System.out.println( "This is the first export of that publication to IMEX." );
        } else {
            System.out.println( "IMEx export history:" );
            for ( Annotation annotation : exportHistory ) {
                System.out.println( "  > " + annotation );
            }
        }

        //

        CvDatabase pubmed = CvHelper.getPubmed( helper );
        CvXrefQualifier primaryReference = CvHelper.getPrimaryReference( helper );

        System.out.println( "Searching for corresponding experiments..." );
        Collection<Experiment> experiments = helper.getObjectsByXref( Experiment.class, pubmed, primaryReference, pmid );
        System.out.println( experiments.size() + " experiment(s) found." );

//        if ( ! publication.getExperiments().equals( experiments ) ) {
//            System.out.println( "ERROR - The publication[ " + pmid + " ] features a different set of experiments than those found by primary-reference." );
//            System.out.println( "- Publication has:" );
//            for ( Experiment experiment : publication.getExperiments() ) {
//                System.out.println( "   * " + experiment.getAc() + " " + experiment.getShortLabel() );
//            }
//            System.out.println( "- Searching by Xref( " + pmid + ", pubmed, primary-reference ) :" );
//            for ( Experiment experiment : experiments ) {
//                System.out.println( "   * " + experiment.getAc() + " " + experiment.getShortLabel() );
//            }
//            System.out.println( "Skipping that publication." );
//            return false;
//        }

        // TODO 1. Check that the publication originate from our own institution.
        //         we could store a CvDatabase reference at the publication level (call it originatingDatabase)

        // TODO 2. Detect if the publication has been updated since the last export.

        // 3. Check on these experiments before to start exporting them
        boolean allAccepted = ExperimentHelper.areAccepted( helper, experiments, true );

        if ( allAccepted ) {

            // 4. Check that all interactions have an IMEx ID
            boolean allInteractionReady = true;
            for ( Experiment experiment : experiments ) {
                if ( ! ExperimentHelper.everyInteractionHasImexId( helper, experiment, true ) ) {
                    allInteractionReady = false;
                }
            }

            if ( allInteractionReady ) {
                // 5. Export publication
                System.out.println( "Starting to export publication to IMEx..." );

                if ( publicationExported > 0 ) {
                    // we do not create a new entry for the first publication. There's already one available.
                    System.out.println( "Creating a new entry." );
                    session.getEntryElement( true );
                }

                buildPsiXml( session, helper, experiments );

                publicationExported++;

                // Add annotation to the publication if not there already.
                CvTopic imexExported = CvHelper.getImexExported( helper );

                String text = getToday() + ": Only interaction involving solely Proteins were exported.";
                Annotation annot = new Annotation( helper.getInstitution(), imexExported, text );

                if ( ! publication.getAnnotations().contains( annot ) ) {
                    helper.create( annot );
                    publication.addAnnotation( annot );
                    helper.update( publication );
                    System.out.println( "Added new Annotation( " + imexExported.getShortLabel() + ", " + text +
                                        " ) to publication( " + pmid + " )." );
                }

                // success
                return true;

            } else {
                System.out.println( "Not all interaction had an IMEx ID, please update publication " + pmid + " before IMEx export." );
            }

        } else {
            System.out.println( "Not all experiment were accepted, please update publication " + pmid + " before IMEx export." );
        }

        // failed
        return false;
    }


    private static void buildPsiXml( UserSessionDownload session,
                                     IntactHelper helper,
                                     Collection<Experiment> experiments ) {

        CvInteractorType proteinType = CvHelper.getProteinType( helper );

        // Build factory
        Interaction2xmlI interaction2xml = Interaction2xmlFactory.getInstance( session );

        for ( Experiment experiment : experiments ) {

            // Generate every single interaction
            System.out.println( "Adding " + experiment.getShortLabel() + "(" +
                                experiment.getInteractions().size() + " interactions(s))" );

            for ( Interaction interaction : experiment.getInteractions() ) {

                if ( InteractionHelper.hasOnlyInteractorOfType( interaction, proteinType ) ) {

                    // this involved only proteins
                    interaction2xml.create( session, session.getInteractionListElement(), interaction );

                } else {

                    System.out.println( "That interaction [" + interaction.getAc() + ", " + interaction.getShortLabel() +
                                        "] doesn't solely involve Proteins. Do not export yet to IMEx." );
                }
            }
        }
    }


    public void exportIMExFile( Collection<String> pubmedIds ) throws IntactException, IOException {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.getVersion25() );

        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            System.out.println( "Database: " + helper.getDbName() );

            int success = exportPublications( session, helper, pubmedIds );

            if ( success > 0 ) {
                // continue only if some XML content has been generated

                // write it to File
                Document document = session.getPsiDocument();

                String today = SIMPLE_DATE_FORMATER.format( new Date() ); // YYYY-MM-DD
                String filenamePrefix = today;
                File tempFile = new File( filenamePrefix + ".xml" ); // getTemporaryFile();
                save( document, tempFile );

                // GZip it
                compressUsingGZip( tempFile, new File( filenamePrefix + ".xml.gz" ) );
            } else {
                System.out.println( "No publication could be exported." );
            }

        } finally {
            if ( helper != null ) {
                helper.closeStore();
            }
        }
    }

    ///////////////////////////
    // M A I N

    public static void main( String[] args ) throws IntactException, IOException {

        Collection<String> publicationIds = new ArrayList<String>();
        publicationIds.add( "16267818" );
        publicationIds.add( "16470656" );

        IMExExport exporter = new IMExExport();
        exporter.exportIMExFile( publicationIds );
    }
}