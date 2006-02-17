/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.SearchReplace;
import uk.ac.ebi.intact.util.HttpProxyManager;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Updates all Experiments found in the database. <br> it updates: <li> <ul> shortlabel </ul> <ul> fullname </ul> <ul>
 * Annotation( contact-email ) </ul> <ul> Annotation( author-list ) </ul> </li>
 * <p/>
 * <br> it also generated a report on what has been done during the update process.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Jul-2005</pre>
 */
public class UpdateExperiments {

    ////////////////////////
    // Constants

    public static final String PUBMED_ID_FLAG = "${PUBMED}";
    public static final String CITEXPLORE_URL = "http://www.ebi.ac.uk/citations/citationDetails.do?externalId=" + PUBMED_ID_FLAG + "&dataSource=MED";

    public static final String headerStyle = "style=\"color: rgb(255, 255, 255);\" bgcolor=\"#3366FF\" class=\"data\"";
    public static final String bodyStyle = "align=\"center\" valign=\"middle\" class=\"data\"";

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    private static ExperimentShortlabelGenerator suffixGenerator = new ExperimentShortlabelGenerator();


    ////////////////////////
    // Private methods

    /**
     * Retreive a pubmed ID from an IntAct experience. <br> That information should be found in Xref( CvDatabase( pubmed
     * ), CvXrefQualifier( primary-reference ) ).
     *
     * @param experiment the experiment from which we try to retreive the pubmedId.
     *
     * @return the pubmedId as a String or null if none were found.
     */
    private static String getPubmedId( Experiment experiment ) {

        if ( experiment == null ) {
            return null;
        }

        String pubmedId = null;
        for ( Iterator iterator = experiment.getXrefs().iterator(); iterator.hasNext() && pubmedId == null; ) {
            Xref xref = (Xref) iterator.next();

            if ( CvDatabase.PUBMED.equals( xref.getCvDatabase().getShortLabel() ) ) {

                if ( CvXrefQualifier.PRIMARY_REFERENCE.equals( xref.getCvXrefQualifier().getShortLabel() ) ) {

                    pubmedId = xref.getPrimaryId();
                }
            }
        }

        return pubmedId;
    }

    private static String generateCitexploreUrl( String pubmedId ) {
        return SearchReplace.replace( CITEXPLORE_URL, PUBMED_ID_FLAG, pubmedId );
    }

    ////////////////////////
    // Public methods

    /**
     * Update the given experiment and generate a report for it.
     *
     * @param helper          access to the database
     * @param contactTopic    the topic associated to the author's email
     * @param authorListTopic the topic associated to the author list
     * @param experiment      the experiemnt to update
     * @param out             where to write the report
     */
    public static void updateExperiment( IntactHelper helper,
                                         CvTopic contactTopic,
                                         CvTopic authorListTopic,
                                         Experiment experiment,
                                         Writer out ) throws IOException {
        String experimentShortlabel = null;

        // find experiment pubmed id
        String pubmedId = getPubmedId( experiment );

        if ( pubmedId == null ) {
            System.err.println( experiment.getShortLabel() + " doesn't have a primary-reference pubmed id." );
            return ;
        }

        try {

            IntactCitation c = IntactCitationFactory.getInstance().buildCitation( pubmedId );

            // get the year of publication
            int year = c.getYear();

            // get the first author last name
            String authorLastName = null;
            if ( false == c.hasAuthorLastName() ) {
                System.err.println( experiment.getShortLabel() + ", " + pubmedId + ": Could not find an author name." );
                throw new Exception();
            } else {
                authorLastName = c.getAuthorLastName();
            }

            // generate a suffix based upon the author name, the year and the pubmed ID
            String suffix = suffixGenerator.getSuffix( authorLastName, year, pubmedId );

            // Build the shortlabel
            // Here we don't care (yet) about the suffixes ... but keeping a list of all already generated
            // shortlabel in the scope of the experimentList should allow us to generate it easily.
            experimentShortlabel = authorLastName + "-" + year + suffix;

            String current = experiment.getShortLabel();

            Annotation mailAnnot = null;
            if ( contactTopic != null ) {
                mailAnnot = new Annotation( helper.getInstitution(), contactTopic, c.getEmail() );
            }

            Annotation authorListAnnot = null;
            if ( c.hasAuthorList() && null != authorListTopic ) {
                authorListAnnot = new Annotation( helper.getInstitution(), authorListTopic, c.getAuthorList() );
            }

            if ( false == current.startsWith( experimentShortlabel ) ||
                 false == experiment.getAnnotations().contains( mailAnnot ) ||
                 false == experiment.getAnnotations().contains( authorListAnnot ) ) {

                //////////////////////////////
                // update the intact object

                boolean updated = false;
                StringBuffer status = new StringBuffer( 64 );
                if ( false == experiment.getShortLabel().equals( experimentShortlabel ) ) {
                    experiment.setShortLabel( experimentShortlabel );
                    status.append( "shortlabel" );
                    updated = true;
                }

                String title = c.getTitle();

                if ( false == title.equals( experiment.getFullName() ) ) {
                    experiment.setFullName( title );
                    if ( status.length() > 0 ) {
                        status.append( " &amp; " );
                    }
                    status.append( "fullname" );
                    updated = true;
                }

                if ( contactTopic != null && c.hasEmail() ) {
                    if ( false == experiment.getAnnotations().contains( mailAnnot ) ) {

                        // persist the annotation
                        helper.create( mailAnnot );

                        experiment.addAnnotation( mailAnnot );
                        if ( status.length() > 0 ) {
                            status.append( " &amp; " );
                        }
                        status.append( "email" );
                        updated = true;
                    }
                }

                if ( authorListTopic != null && c.hasAuthorList() ) {
                    if ( false == experiment.getAnnotations().contains( authorListAnnot ) ) {

                        // if it contains already one, re-use it. we consider here that there is only one.
                        boolean found = false;
                        for ( Iterator iterator = experiment.getAnnotations().iterator(); iterator.hasNext() &&
                                                                                          false == found; ) {
                            Annotation annotation = (Annotation) iterator.next();
                            if ( CvTopic.AUTHOR_LIST.equals( annotation.getCvTopic().getShortLabel() ) ) {
                                found = true;

                                annotation.setAnnotationText( c.getAuthorList() );
                                helper.update( annotation );

                                if ( status.length() > 0 ) {
                                    status.append( " &amp; " );
                                }
                                status.append( "author list" );
                                updated = true;
                            }
                        }

                        if ( false == found ) {
                            // persist the annotation
                            helper.create( authorListAnnot );

                            experiment.addAnnotation( authorListAnnot );
                            if ( status.length() > 0 ) {
                                status.append( " &amp; " );
                            }
                            status.append( "author list" );
                            updated = true;
                        }
                    }
                }

                if ( updated ) {
                    helper.update( experiment );
                }


                ////////////////////////////////
                // Write report.

                out.write( "  <tr>" + NEW_LINE );
                out.write( "    <td " + bodyStyle + ">" + experiment.getAc() + "</td>" + NEW_LINE );
                out.write( "    <td " + bodyStyle + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" + NEW_LINE );
                out.write( "    <td " + bodyStyle + "><code>" + current + "</code></td>" + NEW_LINE );
                out.write( "    <td " + bodyStyle + "><code>" + experimentShortlabel + "</code></td>" + NEW_LINE );
                out.write( "    <td " + bodyStyle + "><a href=\"" + generateCitexploreUrl( pubmedId ) + "\" target=\"_blank\">" +
                           pubmedId +
                           "</a></td>" + NEW_LINE );
                String email = c.getEmail();
                out.write( "    <td " + bodyStyle + ">" + ( email == null ? "-" : "<code>" + email + "</code>" ) + "</td>" + NEW_LINE );

                System.out.println( "\n" + StringUtils.rightPad( experiment.getAc(), 15 ) +
                                    StringUtils.rightPad( current + " / " + experimentShortlabel, 50 ) +
                                    pubmedId + "   " + generateCitexploreUrl( pubmedId ) );

                if ( updated ) {
                    out.write( "    <td " + bodyStyle + "> <font color=\"green\"><b>UPDATED</b> (" + status.toString() +
                               ")</font> </td>" + NEW_LINE );
                } else {
                    out.write( "    <td " + bodyStyle + "> <font color=\"black\">UP TO DATE</font> </td>" + NEW_LINE );
                }

                out.write( "  </tr>" + NEW_LINE );
                out.flush();
            }

        } catch ( Exception e ) {

            out.write( "  <tr>" + NEW_LINE );
            out.write( "    <td " + bodyStyle + ">" + experiment.getAc() + "</td>" + NEW_LINE );
            out.write( "    <td " + bodyStyle + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>" + NEW_LINE );
            out.write( "    <td " + bodyStyle + "><code>" + experiment.getShortLabel() + "</code></td>" + NEW_LINE );
            out.write( "    <td " + bodyStyle + "> - </td>" + NEW_LINE );

            if ( pubmedId != null ) {
                out.write( "    <td " + bodyStyle + "><a href=\"" + generateCitexploreUrl( pubmedId ) + "\" target=\"_blank\">" +
                           pubmedId +
                           "</a></td>" + NEW_LINE );
            } else {
                out.write( "<td " + bodyStyle + "> No pubmed ID available </td>" + NEW_LINE );
            }

            // email ...
            out.write( "<td " + bodyStyle + "> &nbsp; </td>" + NEW_LINE );
            System.out.println( "\n" + StringUtils.rightPad( experiment.getAc(), 15 ) +
                                StringUtils.rightPad( experiment.getShortLabel(), 23 ) +
                                pubmedId + "   " + generateCitexploreUrl( pubmedId ) );

            out.write( "    <td " + bodyStyle + "> <font color=\"red\"><b>ERROR</b><br><small><small>" );
            e.printStackTrace( new PrintWriter( out ) );
            out.write( "</small></small></font> </td>" + NEW_LINE );

            System.err.println( "Message: " + e.getMessage() );

            e.printStackTrace();

            // display causes (if any)
            Throwable t = e.getCause();
            while ( t != null ) {
                System.err.println( "============================ CAUSED BY  ========================" );
                System.err.println( "Message: " + t.getMessage() );
                t.printStackTrace();
                t = t.getCause();
            }
        }
    }


    ////////////////////////
    // M A I N

    public static void main( String[] args ) throws IntactException,
                                                    IOException,
                                                    SQLException {

        try{
            //See intactCore/config/proxy.properties
            HttpProxyManager.setup();
        }
        catch(HttpProxyManager.ProxyConfigurationNotFound e){
            System.err.println(e.getMessage());
        }
        IntactHelper helper = null;
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd@HH.mm" );
        String time = formatter.format( new Date() );
        File file = new File( "ExperimentReport-" + time + ".html" );
        System.out.println( "Output saved in " + file.getAbsolutePath() );

        BufferedWriter out = new BufferedWriter( new FileWriter( file ) );

        // header of the HTML report
        out.write( "<html>" + NEW_LINE );
        out.write( "<head>" + NEW_LINE );
        out.write( "<title>Experiment's shortlabel checking</title>" + NEW_LINE );
        out.write( "<link rel=\"stylesheet\" type=\"text/css\" href =\"http://www.ebi.ac.uk/intact/search/layouts/styles/intact.css\"/>" + NEW_LINE );
        out.write( "</head>" + NEW_LINE );
        out.write( "<body>" + NEW_LINE );
        out.write( "<table border=\"1\" cellspacing=\"0\">" + NEW_LINE );
        out.write( "  <tr>" + NEW_LINE );

        out.write( "    <td " + headerStyle + ">Experiment AC</td>" + NEW_LINE );
        out.write( "    <td " + headerStyle + ">Valid ?</td>" + NEW_LINE );
        out.write( "    <td " + headerStyle + ">Current Shortlabel</td>" + NEW_LINE );
        out.write( "    <td " + headerStyle + ">Generated</td>" + NEW_LINE );
        out.write( "    <td " + headerStyle + ">CitExplore</td>" + NEW_LINE );
        out.write( "    <td " + headerStyle + ">author's email</td>" + NEW_LINE );
        out.write( "    <td " + headerStyle + ">status</td>" + NEW_LINE );
        out.write( "  </tr>" + NEW_LINE );

        try {
            helper = new IntactHelper();

            try {
                System.out.println( "Helper created (User: " + helper.getDbUserName() + " " +
                                    "Database: " + helper.getDbName() + ")" );
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            // retreive all experiment ACs
            System.out.print( "Loading experiments ... " );
            System.out.flush();
            Connection connection = helper.getJDBCConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( "SELECT ac FROM ia_experiment ORDER BY created" );
            List experimentAcs = new ArrayList();
            while ( resultSet.next() ) {
                experimentAcs.add( resultSet.getString( 1 ) );
            }
            resultSet.close();
            statement.close();
            connection = null; // release the connection, don't close it, the helper is doing that for us.

            System.out.println( experimentAcs.size() + " experiment's AC loaded." );

            // search for the Cv to attach to author's email
            CvTopic authorEmail = (CvTopic) helper.getObjectByLabel( CvTopic.class, CvTopic.CONTACT_EMAIL );
            if ( authorEmail == null ) {
                System.err.println( "Could not find CvTopic(" + CvTopic.CONTACT_EMAIL +
                                    ")... no email will be attached/updated to the experiments." );
            }

            CvTopic authorList = (CvTopic) helper.getObjectByLabel( CvTopic.class, CvTopic.AUTHOR_LIST );
            if ( authorList == null ) {
                System.err.println( "Could not find CvTopic(" + CvTopic.AUTHOR_LIST +
                                    ")... no author list will be attached/updated to the experiment." );
            }


            for ( Iterator iterator = experimentAcs.iterator(); iterator.hasNext(); ) {
                String ac = (String) iterator.next();

                // get the experiment
                Collection experiments = helper.search( Experiment.class, "ac", ac );
                Experiment experiment = (Experiment) experiments.iterator().next();
                experiments = null;

                updateExperiment( helper, authorEmail, authorList, experiment, out );

                iterator.remove(); // empty the collection as we go
            }

            out.write( "</table>" + NEW_LINE );
            out.write( "</body>" + NEW_LINE );
            out.write( "</html>" + NEW_LINE );
            out.flush();

        } finally {
            if ( helper != null ) {
                System.out.println( "Datasource closed." );
                helper.closeStore();
            }

            if ( out != null ) {
                out.close();
            }
        }
    }
}