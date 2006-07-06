/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.SearchException;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.sql.SQLException;

import org.apache.ojb.broker.accesslayer.LookupException;

/**
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinExport {

    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * Export the protein's UNIPROT identifier to a flat file.
     *
     * @param outputFile the path of the output file.
     * @param bioSourceShortLabel the biosource shortLabel to filter on. if <code>null</code>,
     *                            we take every proteins.
     * @throws IntactException if an IO error occurs. don't forget to check the nested exception.
     * @throws SearchException if an IntAct object can't be find.
     */
    private void exportProteinUniprotAC( final String outputFile, String bioSourceShortLabel )
            throws IntactException,
            SearchException {
        IntactHelper helper = new IntactHelper();
        try {
            System.out.println("Helper created (User: "+helper.getDbUserName()+ " " +
                               "Database: "+helper.getDbName()+")");
        } catch ( LookupException e ) {
            e.printStackTrace ();
        } catch ( SQLException e ) {
            e.printStackTrace ();
        }

        BioSource bioSource = null;
        if ( bioSourceShortLabel != null ) {
            bioSource = (BioSource) helper.getObjectByLabel( BioSource.class, bioSourceShortLabel );
            if ( bioSource == null ) {
                throw new SearchException( "The requested bioSource ("+ bioSourceShortLabel +") could not be found." );
            }
        }

        CvDatabase uniprotDatabase = null;
        uniprotDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, CvDatabase.UNIPROT );
        if ( uniprotDatabase == null ) {
            throw new SearchException( "Could not find the UNIPROTKB database in the current intact node." );
        }

        // collect proteins
        Collection proteins = null;
        if ( bioSource == null) {
            proteins = helper.search( Protein.class.getName(), "ac", null); // all proteins
        } else {
            Collection interactors = helper.getInteractorBySource( Protein.class, bioSource );
            // keep only instances of Protein.
            proteins = new ArrayList( interactors.size() );
            for ( Iterator iterator = interactors.iterator (); iterator.hasNext (); ) {
                Interactor interactor = (Interactor) iterator.next ();
                if ( interactor instanceof Protein ) {
                    proteins.add( interactor );
                }
            }
        }
        System.out.println ( proteins.size() + " proteins found." );

        // init output file.
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter( outputFile ), 8192);
        } catch (IOException e) {
            throw new IntactException( "Could not create output file: " + outputFile, e );
        }

        // export found proteins
        String uniprotAc    = null;
        Collection xrefs = null;
        for ( Iterator iterator = proteins.iterator (); iterator.hasNext (); ) {
            Protein protein = (Protein) iterator.next ();

            xrefs = protein.getXrefs();
            for ( Iterator iterator2 = xrefs.iterator (); iterator2.hasNext () && uniprotAc == null; ) {
                Xref xref = (Xref) iterator2.next ();
                if ( xref.getCvDatabase().equals( uniprotDatabase ) ) {
                   uniprotAc = xref.getPrimaryId();
                }
            } // xrefs loop

            if ( uniprotAc != null ) {
                // write the UNIPROT AC in the output file
                try {
                    out.write( uniprotAc + NEW_LINE );
                } catch ( IOException e ) {
                    throw new IntactException( "Could not write in the output file: " + outputFile, e );
                }

                uniprotAc = null; // in order to make the second loop test valid.
            } else {
                System.out.println ( "no UNIPROT AC for protein " + protein );
            }
        } // proteins loop

        try {
            out.close();
        } catch ( IOException e ) {
            throw new IntactException( "Could not close the output file: " + outputFile, e );
        }
    }



    /**
     * D E M O
     *
     * @param args [0] output file, [1] optional biosource shortlabel
     */
    public static void main ( String[] args ) {

        if ( args.length < 1 ) {
           System.err.println ( "Usage: ProteinExport output_file [biosource shortlabel]" );
            System.exit( 1 );
        }

        String outputFilename = args[0];
        String bioSource = null;
        if ( args.length > 1 ) {
           bioSource = args[1];
        }

        ProteinExport export = new ProteinExport();
        try {
            export.exportProteinUniprotAC( outputFilename, bioSource );
        } catch ( IntactException e ) {
            e.printStackTrace ();
            System.exit( 1 );
        } catch ( SearchException e ) {
            e.printStackTrace ();
            System.exit( 1 );
        }

        System.exit( 0 );
    }
}