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
package uk.ac.ebi.intact.plugins.fasta;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class exporting all proteins sequence into a fasta file.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-Jul-2006</pre>
 */
public class FastaExporter {

    private static final Log log = LogFactory.getLog( FastaExporter.class );

    /**
     * Cross plateform - New line
     */
    public static final String NEW_LINE = System.getProperty( "line.separator" );

    /**
     * Retreive the identity of an Interactor.
     *
     * @param interactor the interactor
     * @return the identity or null if not found.
     */
    private static String getIdentity( Interactor interactor ) {

        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef( CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF );
        Collection identities = new ArrayList( 2 );


        for ( Xref xref : interactor.getXrefs() ) {
            if ( identity.equals( xref.getCvXrefQualifier() ) ) {
                identities.add( xref.getPrimaryId() );
            }
        }

        StringBuilder sb = new StringBuilder( 32 );
        for ( Iterator iterator = identities.iterator(); iterator.hasNext(); ) {
            String id = ( String ) iterator.next();
            sb.append( id );
            if ( iterator.hasNext() ) {
                sb.append( '+' );
            }
        }

        return sb.toString();
    }

    private static String removeLineReturn( String s ) {
        if ( s == null ) {
            return null;
        }
        return s.replace( "\n", "" );
    }

    /**
     * Creates a fasta file with the protein sequences in the database
     *
     * @param exportedFasta The fasta file to be created
     * @return an <code>OutputStream</code> with the log of the process
     * @throws IOException bad thing
     */
    public static void exportToFastaFile( PrintStream out, File exportedFasta ) throws IOException, IntactTransactionException {

        if ( exportedFasta == null ) {
            throw new NullPointerException( "Provided exportedFasta file is null" );
        }

        if ( IntactContext.getCurrentInstance().getDataContext().isTransactionActive() ) {
            throw new IllegalStateException( "Transaction must be closed when trying to export to fasta" );
        }

        BufferedWriter fastaWriter = new BufferedWriter( new FileWriter( exportedFasta ) );

        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();

        out.println( "" );
        out.println( "--------------------------------------------------------" );
        out.println( "Legend:" );
        out.println( "        . : protein's sequence exported" );
        out.println( "        X : protein doesn't take part in any interaction." );
        out.println( "        @ : protein without sequence." );
        out.println( "--------------------------------------------------------" );
        out.println( "" );

        out.println( "Loading all proteins." );

        // Load protein count using DAO.
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        int proteinCount = proteinDao.countAll();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        out.println( proteinCount + " protein(s) loaded from the database." );

        int count = 0;
        int countExported = 0;
        int countNoSeq = 0;

        List<ProteinImpl> proteins = null;
        int firstResult = 0;
        int maxResults = 300;

        do {
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            proteins = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getProteinDao().getAll( firstResult, maxResults );

            firstResult += maxResults;


            for ( ProteinImpl protein : proteins ) {
                if ( log.isDebugEnabled() ) {
                    log.debug( protein.getAc() );
                }

                int componentsSize = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                        .getProteinDao().countComponentsForInteractorWithAc( protein.getAc() );

                // Process the chunk of data
                if ( componentsSize > 0 ) {

                    String sequence = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                            .getPolymerDao().getSequenceByPolymerAc( protein.getAc() );
                    if ( sequence != null ) {
                        sequence = sequence.trim();
                        if ( sequence.length() > 0 ) {

                            // only output polymer with a sequence
                            StringBuilder sb = new StringBuilder( 512 );

                            // Header contains: AC, shortlabel and primaryId.
                            sb.append( ">INTACT:" );
                            sb.append( protein.getAc() );

                            String identity = getIdentity( protein );
                            if ( identity != null ) {
                                sb.append( ' ' );
                                sb.append( removeLineReturn( identity.trim() ) );
                            }
                            
                            sb.append( ' ' );
                            sb.append( removeLineReturn( protein.getShortLabel() ) );

                            final String gn = ProteinUtils.getGeneName( protein );
                            if ( gn != null ) {
                                sb.append( ' ' );
                                sb.append( removeLineReturn( gn ) );
                            }

                            sb.append( NEW_LINE );

                            sb.append( sequence );

                            sb.append( NEW_LINE );

                            // write to file
                            fastaWriter.write( sb.toString() );

                            // stats
                            countExported++;
                            out.print( "." );
                        } else {
                            out.print( "@" );
                        }
                    }

                } else {
                    // stats
                    countNoSeq++;
                    out.print( "X" );
                }

                count++;
                if ( ( count % 70 ) == 0 ) {
                    out.println( "   " + count );
                    fastaWriter.flush();
                }
            }

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        } while ( !proteins.isEmpty() );

        out.println( "" );
        out.println( "----------------------------------------------------------------------------------" );
        out.println( "Processed " + count + " protein(s)." );
        out.println( "Exported " + countExported + " proteins" );
        out.println( countNoSeq + " protein(s) were not involved in any interactions. They were filtered out." );

        fastaWriter.flush();
        fastaWriter.close();
    }

    public static void main( String[] args ) throws Exception {
        exportToFastaFile( System.out, new File( "intact.fasta" ) );
    }
}