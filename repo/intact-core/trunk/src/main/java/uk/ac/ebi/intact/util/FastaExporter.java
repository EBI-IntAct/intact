/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.context.IntactContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Utility class exporting all proteins sequence into a fasta file.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-Jul-2006</pre>
 */
public class FastaExporter {

    /**
     * Cross plateform - New line
     */
    public static final String NEW_LINE = System.getProperty( "line.separator" );

    /**
     * Retreive the identity of an Interactor.
     *
     * @param interactor the interactor
     *
     * @return the identity or null if not found.
     */
    private static String getIdentity( Interactor interactor ) {

        CvXrefQualifier identity = null;

        try {
            identity = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao( CvXrefQualifier.class ).getByXref( CvXrefQualifier.IDENTITY_MI_REF );
        } catch ( Exception e ) {
            e.printStackTrace();
            identity = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao( CvXrefQualifier.class ).getByXref( CvXrefQualifier.IDENTITY_MI_REF );
        }

        if ( identity == null ) {
            throw new IllegalStateException( "Could not find CvXrefQualifier( identity ) in the database." );
        }

        for ( Xref xref : interactor.getXrefs() ) {
            if ( identity.equals( xref.getCvXrefQualifier() ) ) {
                return xref.getPrimaryId();
            }
        }

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        return null;
    }


    public static void main( String[] args ) throws IOException {

        BufferedWriter out = new BufferedWriter( new FileWriter( new File( "intact.fasta" ) ) );

        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();

        System.out.println( "" );
        System.out.println( "--------------------------------------------------------" );
        System.out.println( "Legend:" );
        System.out.println( "        . : protein's sequence exported" );
        System.out.println( "        X : protein doesn't take part in any interaction." );
        System.out.println( "--------------------------------------------------------" );
        System.out.println( "" );

        System.out.println( "Loading all proteins." );

        // Load protein count using DAO.
        int proteinCount = proteinDao.countAll();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        System.out.println( proteinCount + " protein(s) loaded from the database." );

        int count = 0;
        int countExported = 0;
        int countNoSeq = 0;

        Iterator<ProteinImpl> iterator = proteinDao.iterator();
        while ( iterator.hasNext() ) {
            ProteinImpl protein = iterator.next();

            System.out.println(protein.getAc());

            // Process the chunk of data
            if ( ! protein.getActiveInstances().isEmpty() ) {

                StringBuffer sb = new StringBuffer( 512 );

                // Header contains: AC, shortlabel and primaryId.
                sb.append( '>' ).append( ' ' );
                sb.append( protein.getAc() );
                sb.append( '|' );
                sb.append( protein.getShortLabel() );
                String identity = getIdentity( protein );
                if ( identity != null ) {
                    sb.append( '|' );
                    sb.append( identity );
                }

                sb.append( NEW_LINE );

                sb.append( protein.getSequence() );

                sb.append( NEW_LINE );

                // write to file
                out.write( sb.toString() );
                out.flush();

                // stats
                countExported++;
                System.out.print( "." );
                System.out.flush();

            } else {
                // stats
                countNoSeq++;
                System.out.print( "X" );
                System.out.flush();
            }

            count++;
            if ( ( count % 70 ) == 0 ) {
                System.out.println( "   " + count );
            }
        }

        System.out.println( "" );
        System.out.println( "----------------------------------------------------------------------------------" );
        System.out.println( "Processed " + count + " protein(s)." );
        System.out.println( "Exported " + countExported + " proteins" );
        System.out.println( countNoSeq + " protein(s) were not involved in any interactions. They were filtered out." );

        out.close();
    }
}