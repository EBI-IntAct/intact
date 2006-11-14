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
package uk.ac.ebi.intact.dbutil.fasta;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;

import java.io.*;
import java.util.Iterator;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class exporting all proteins sequence into a fasta file.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10-Jul-2006</pre>
 */
public class FastaExporter {

    private static final Log log = LogFactory.getLog(FastaExporter.class);

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

    /**
     * Creates a fasta file with the protein sequences in the database
     * @param exportedFasta The fasta file to be created
     * @return an <code>OutputStream</code> with the log of the process
     * @throws IOException bad thing
     */
    public static void exportToFastaFile(PrintStream out, File exportedFasta) throws IOException
    {
        if (exportedFasta == null)
        {
            throw new NullPointerException("Provided exportedFasta file is null");
        }

        BufferedWriter fastaWriter = new BufferedWriter( new FileWriter( exportedFasta ) );

        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();

        out.println( "");
        out.println( "--------------------------------------------------------");
        out.println( "Legend:" );
        out.println( "        . : protein's sequence exported" );
        out.println( "        X : protein doesn't take part in any interaction." );
        out.println( "--------------------------------------------------------" );
        out.println( "" );

        out.println( "Loading all proteins." );

        // Load protein count using DAO.
        int proteinCount = proteinDao.countAll();

        out.println( proteinCount + " protein(s) loaded from the database." );

        int count = 0;
        int countExported = 0;
        int countNoSeq = 0;

        Iterator<ProteinImpl> iterator = proteinDao.getAllIterator();
        while ( iterator.hasNext() ) {
            ProteinImpl protein = iterator.next();

            if (log.isDebugEnabled())
                log.debug(protein.getAc());

            // HACK: to avoid lazyloading exceptions, we capture the exception and rebuild de Dao again (refreshing the protein)
            Collection<Component> activeInstances = null;
            String sequence = null;
            try
            {
                activeInstances = protein.getActiveInstances();
                sequence = protein.getSequence();
            }
            catch (Throwable t)
            {
                // this is necessary, because the sequence cannot be loaded lazily
               // due to the autocommits done by the iterator
                proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();
                protein = proteinDao.getByAc(protein.getAc());

                activeInstances = protein.getActiveInstances();
                sequence = protein.getSequence();
            }

            // Process the chunk of data
            if ( ! activeInstances.isEmpty() ) {

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
                sb.append( sequence );

                sb.append( NEW_LINE );

                // write to file
                fastaWriter.write( sb.toString() );
                fastaWriter.flush();

                // stats
                countExported++;
                out.print( "." );

            } else {
                // stats
                countNoSeq++;
                out.print( "X" );
            }

            count++;
            if ( ( count % 70 ) == 0 ) {
                out.println( "   " + count );
            }
        }

        out.println( "" );
        out.println( "----------------------------------------------------------------------------------" );
        out.println( "Processed " + count + " protein(s)." );
        out.println( "Exported " + countExported + " proteins" );
        out.println( countNoSeq + " protein(s) were not involved in any interactions. They were filtered out." );

        fastaWriter.close();
    }



    public static void main( String[] args ) throws IOException {
        exportToFastaFile( System.out, new File( "intact.fasta") );
    }
}