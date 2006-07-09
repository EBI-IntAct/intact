/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class GoaTools {

    private static final Log log = LogFactory.getLog(GoaTools.class);

    //////////////////////
    // Inner class
    //////////////////////

    private class GoaItem {

        private String database;
        private String ac;
        private String symbol;
        private String goId;
        private String goCategory;

        public GoaItem ( String database, String ac, String symbol, String goId, String goCategory ) {
            this.database = database;
            this.ac = ac;
            this.symbol = symbol;
            this.goId = goId;
            this.goCategory = goCategory;
        }

        public String getDatabase () {
            return database;
        }

        public String getAc () {
            return ac;
        }

        public String getSymbol () {
            return symbol;
        }

        public String getGoId () {
            return goId;
        }

        public String getGoCategory () {
            return goCategory;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer(256);

            sb.append( "\n Database: " + database );
            sb.append( "\n ac: " + ac );
            sb.append( "\n symbol: " + symbol );
            sb.append( "\n goId: " + goId );
            sb.append( "\n goCategory: " + goCategory );

            return sb.toString();
        }
    }



    /**
     *
     */
    private class GoaCollection {

        private URL mySourceURL;

        private BufferedReader goaBufferedReader;

        private GoaIterator currentIterator;

        public GoaCollection( String sourceUrl ) throws MalformedURLException {

            try {
                mySourceURL = new URL( sourceUrl );
            } catch ( MalformedURLException e ) {
                throw e;
            }
        }

        public Iterator iterator () {

            final String isProxySet = System.getProperty( "proxySet" );
            final String proxyHost  = System.getProperty( "proxyHost" );
            final String proxyPort  = System.getProperty( "proxyPort" );
            log.debug( "Uses: proxySet="+ isProxySet +", proxyHost="+ proxyHost +", proxyPort="+ proxyPort );

            try {
                InputStream in = mySourceURL.openStream();
                InputStreamReader isr = new InputStreamReader( in );
                goaBufferedReader = new BufferedReader( isr );

                GoaIterator goaIterator = new GoaIterator( goaBufferedReader );
                currentIterator = goaIterator;
                return goaIterator;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public long getLineProcessedCount() {
            return currentIterator.getLineProcessedCount();
        }
    } // GoaCollection



    private class GoaIterator implements Iterator {

        private BufferedReader goaBufferedReader;
        private String currentLine;
        private long goaLineCount = 0;


        public GoaIterator ( BufferedReader goaBufferedReader ) {
            this.goaBufferedReader = goaBufferedReader;
            try {
                currentLine = goaBufferedReader.readLine();
                goaLineCount++;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public boolean hasNext(){

            return (currentLine != null);
        }

        public Object next(){

            if ( ! hasNext() ) {
                throw new NoSuchElementException();
            }

            // build a GoaItem
            GoaItem goaItem = null;
            StringTokenizer st = new StringTokenizer( currentLine, "\t" );
//            log.debug ( currentLine );
            /* 01 */ String database = st.nextToken();
            /* 02 */ String ac = st.nextToken();
            /* 03 */ String symbol = st.nextToken();
            /* TODO: find out why the empty column 04 (human file) is not found */
            /* 04 */ String goId = st.nextToken();
            /* 05 */ st.nextToken();
            /* 06 */ st.nextToken();
            /* 07 */ String goCategory = st.nextToken();

            goaItem = new GoaItem(database, ac, symbol, goId, goCategory);

            // Read the next item
            try {
                currentLine = goaBufferedReader.readLine();
                goaLineCount++;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            return goaItem;
        }

        public void remove(){
            // do nothing ... can't remove element from a readonly stream.
            // TODO: pass the current item ?
        }

        public long getLineProcessedCount() {
            return goaLineCount;
        }

    } // GoaIterator




    ////////////////////////
    // Instance variables
    ////////////////////////

    private GoaCollection goaBrowser;

    private GoServerProxy goServerProxy;

    private long newAnnotationCount = 0;



    //////////////////////////
    // Instance constructors
    //////////////////////////

    public GoaTools ( ) {
        goServerProxy = new GoServerProxy();
    }

    public GoaTools ( String url ) throws MalformedURLException, IntactException {
        this();
        goaBrowser = new GoaCollection( url );
    }


    ///////////////////////
    // Instance methods
    ///////////////////////

    public GoaCollection getGoaBrowser () {
        return goaBrowser;
    }

    private boolean isXrefAlreadyExisting( Collection<Xref> xrefs, String primaryId, CvDatabase database) {

        for (Xref xref : xrefs)
        {
            if (xref != null && xref.getPrimaryId().equals(primaryId) &&
                    database != null && database.equals(xref.getCvDatabase()))
            {
                return true;
            }
        }
        return false;
    }

    public void addNewXref (AnnotatedObject current, final Xref xref)  {
        // Make sure the xref does not yet exist in the object
        Collection<Xref> xrefs = current.getXrefs();
        for (Xref anXref : xrefs)
        {
            if (anXref.equals(xref))
            {
                return; // already in, exit
            }
        }

        // add the xref to the AnnotatedObject
        current.addXref (xref);

        // That test is done to avoid to record in the database an Xref
        // which is already linked to that AnnotatedObject.
        if (xref.getParentAc() == current.getAc()) {
            try {
                DaoFactory.getXrefDao().persist(xref);
                newAnnotationCount++;
            } catch (Exception e_xref) {
                log.error ( "Could not create the Xref: " + xref.getPrimaryId() +
                                     " for the AnnotatedObject: " + current.getShortLabel() );
            }
        }
    }

    private void updateGoXref ( Institution institution, Protein protein, GoaItem goaItem, CvDatabase database ) {

        Collection<Xref> xrefs = protein.getXrefs();
        if ( ! isXrefAlreadyExisting( xrefs, goaItem.getGoId(), database ) ) {
            // add a new Xref
            String goId = goaItem.getGoId();

            GoServerProxy.GoResponse goResponse;
            try {
                goResponse = goServerProxy.query( goId );
            } catch ( IOException e ) {
                log.error ( "Could not find GO term: " + goId + ". abort creation." );
                e.printStackTrace ();
                return;
            } catch ( GoServerProxy.GoIdNotFoundException e ) {
                log.error ( "Could not find GO term: " + goId + ". abort creation." );
                e.printStackTrace ();
                return;
            }

            Xref xref = new Xref( institution,
                    database,
                    goId,
                    goResponse.getName(),
                    null, null );

            addNewXref( protein, xref );
            log.debug ( "Update protein " + protein.getShortLabel() + " with Xref: " + goId );
        }
    } // updateGoXref

    public void displayStatistics() {
        log.debug ( "#GOA line processed: " + getGoaBrowser().getLineProcessedCount());
        log.debug ( "#GO added: " + newAnnotationCount );
    }






    /**
     * D E M O
     *
     * @param args [0] goa URL from which we'll read the data
     */
    public static void main ( String[] args ) throws Exception {
        // Check parameters
        if ( args.length == 0 ) {
            // usage
            log.error ( "Usage: GoaTools <GOA source URL>" );
            System.exit( 1 );
        }

        CvDatabase goDatabase = DaoFactory.getCvObjectDao(CvDatabase.class).getByShortLabel("go");

        if ( goDatabase == null ){
            throw new IntactException ( "Could not find the CvDatabase: go. Stop processing." );
        }

        Institution institution = DaoFactory.getInstitutionDao().getInstitution();

        if ( institution == null ){
            throw new IntactException ( "Could not find the Institution: EBI. Stop processing." );
        }


        GoaTools goaTools = new GoaTools( args[0] );
        GoaCollection goaCollection = goaTools.getGoaBrowser();
        Collection<ProteinImpl> proteins;
        Protein protein;
        long count = 0;
        for ( Iterator goaIterator = goaCollection.iterator (); goaIterator.hasNext () ; ) {
            GoaItem goaItem = (GoaItem) goaIterator.next();

            count++;
            if ((count % 500) == 0) goaTools.displayStatistics();

            proteins = DaoFactory.getProteinDao().getByXrefLike(goaItem.getAc());
            if (proteins.size() != 0) {
                log.debug ( proteins.size() + " Protein found by Xref: " + goaItem.getAc() );
                for (Protein prot : proteins)
                {
                    goaTools.updateGoXref(institution, prot, goaItem, goDatabase);
                }
                continue;
            } else {
                // TODO: we should not fall in taht case ... kind of inconstistancY.

                String symbol = goaItem.getSymbol();
                if ( ! goaItem.getAc().equals( symbol ) ) {
                    protein = DaoFactory.getProteinDao().getByShortLabel(symbol);

                    if (protein != null) {
                        log.debug ( "Protein found by Label: " + symbol );
                        goaTools.updateGoXref( institution, protein, goaItem, goDatabase );
                        continue;
                    }
                }
            }

            System.out.print ( "." ); // displayed only if the Goa line hasn't been usedfull
        }
    } // main

} // GoaTools
