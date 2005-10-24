// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload;

import org.apache.ojb.broker.accesslayer.LookupException;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.*;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlI;
import uk.ac.ebi.intact.application.dataConversion.util.DisplayXML;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.util.Chrono;

import javax.xml.transform.TransformerException;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentDownload {

    /**
     * @param root
     * @param file
     *
     * @throws javax.xml.transform.TransformerException
     *
     */
    public static void write( Element root, File file ) throws IOException {

        System.out.print( "\nWriting DOM to " + file.getAbsolutePath() + " ... " );
        System.out.flush();

        // doesn't do any indentation !!
//        DOMSource source = new DOMSource( document );
//        TransformerFactory tFactory = TransformerFactory.newInstance();
//        Transformer transformer = tFactory.newTransformer();
//        StreamResult result = new StreamResult( file );
//        transformer.transform( source, result );

        // prepare a file writer.
        Writer writer = new BufferedWriter( new FileWriter( file ) );

        // Write the content in the file (indented !!)
        DisplayXML.write( root, writer, "   " );

        writer.flush();

        // Close the file
        writer.close();

        System.out.println( "done." );
    }

    private static CvMapping cvMapping = null;

    public static void exportExperiment( Experiment experiment, PsiVersion version ) throws IOException, TransformerException {


        System.out.println( "Processing: " + experiment.getShortLabel() + ", has " +
                            experiment.getInteractions().size() + " interaction(s)." );
        System.out.println( version );

        UserSessionDownload session = new UserSessionDownload( version );

        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            session.filterObsoleteAnnotationTopic( helper );

        } catch ( IntactException e ) {
            e.printStackTrace();
        } finally {
            if ( helper != null ) {
                try {
                    helper.closeStore();
                    helper = null;
                } catch ( IntactException e ) {
                    e.printStackTrace();
                }
            }
        }


        boolean loadMapping = false;

        if ( loadMapping && version == PsiVersion.VERSION_1 ) {

            if ( cvMapping == null ) {

                try {
                    cvMapping = new CvMapping();
                    helper = new IntactHelper();

//                    cvMapping.loadFile( new File( "C:/reverseMapping.txt" ), helper );

                } catch ( IntactException e ) {
                    e.printStackTrace();
                } finally {
                    if ( helper != null ) {
                        try {
                            helper.closeStore();
                        } catch ( IntactException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        Interaction2xmlI interaction2xml = Interaction2xmlFactory.getInstance( session );

        // in order to have them in that order, experimentList, then interactorList, at last interactionList.
        session.getExperimentListElement();
        session.getInteractorListElement();

        Collection interactions = experiment.getInteractions();
        int count = 0;
        for ( Iterator iterator1 = interactions.iterator(); iterator1.hasNext(); ) {
            Interaction interaction = (Interaction) iterator1.next();

            interaction2xml.create( session, session.getInteractionListElement(), interaction );
            if ( ( count % 50 ) == 0 ) {
                System.out.println( "" );
            }
            System.out.print( "." );
            System.out.flush();
            count++;
        }


        File file = new File( "C:/psi/" + experiment.getShortLabel() + ".PSI" + version.getVersion() + ".xml" );
        write( session.getPsiDocument().getDocumentElement(), file );

        DataBuilder builder = new PsiDataBuilder();
        try {
            builder.writeData( file.getAbsolutePath(), session.getPsiDocument() );
        } catch ( DataConversionException e ) {
            e.printStackTrace();
        }

        PsiValidator.validate( file );

//                DisplayXML.print( session.getPsiDocument().getDocumentElement() );
//                System.out.println( "\n\n\n" );

    }


    public static void main( String[] args ) throws IntactException, SQLException, LookupException,
                                                    TransformerException, IOException {

        IntactHelper helper = null;

        Chrono chrono = new Chrono();
        chrono.start();

        try {
            helper = new IntactHelper();
            System.out.println( "Database: " + helper.getDbName() );
            System.out.println( "Username: " + helper.getDbUserName() );

            System.out.print( "Searching for all experiment: " );

            String human_small = "attera-2004-1,fribourg-2003-2,kung-2004-1,mattera-2004-2,mattera-2004-3,";

            Collection experiments = new ArrayList();
            Collection experimentsLabel = new ArrayList();

            StringTokenizer st = new StringTokenizer( human_small, "," );
            while ( st.hasMoreTokens() ) {
                experimentsLabel.add( st.nextToken() );
            }

            for ( Iterator iterator = experimentsLabel.iterator(); iterator.hasNext(); ) {
                String label = (String) iterator.next();
                System.out.print( "Loading " + label + "..." );
                System.out.flush();
                experiments.addAll( helper.search( Experiment.class, "shortlabel", label ) );
                System.out.println( "done." );
            }

            System.out.println( experiments.size() + " experiment" + ( experiments.size() > 1 ? "s" : "" ) + " found." );

            long timePsi1 = 0;
//            long timePsi2 = 0;
            long timePsi25 = 0;

            for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {

                Experiment experiment = (Experiment) iterator.next();

//                if ( experiment.getInteractions().size() > 500 ) {
//                    iterator.remove();
//                    continue;
//                }
                long start;

                start = System.currentTimeMillis();
                exportExperiment( experiment, PsiVersion.VERSION_1 );
                timePsi1 += System.currentTimeMillis() - start;

//                start = System.currentTimeMillis();
//                exportExperiment( experiment, PsiVersion.VERSION_2 );
//                timePsi2 += System.currentTimeMillis() - start;

                start = System.currentTimeMillis();
                exportExperiment( experiment, PsiVersion.VERSION_25 );
                timePsi25 += System.currentTimeMillis() - start;

                iterator.remove();

                System.out.println( "Generation Time" );
                System.out.println( "PSI v1:  " + timePsi1 );
//                System.out.println( "PSI v2:  " + timePsi2 );
                System.out.println( "PSI v25: " + timePsi25 );
            }

        } finally {

            if ( helper != null ) {
                helper.closeStore();
                System.out.println( "Database connexion closed." );
            }
        }

        chrono.stop();
        System.out.println( "Time elapsed: " + chrono );
    }
}