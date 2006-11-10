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
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;

import java.io.*;
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

    /**
     * Creates a fasta file with the protein sequences in the database
     * @param exportedFasta The fasta file to be created
     * @return an <code>OutputStream</code> with the log of the process
     * @throws IOException bad thing
     */
    public static OutputStream exportToFastaFile(File exportedFasta) throws IOException
    {
        if (exportedFasta == null)
        {
            throw new NullPointerException("Provided exportedFasta file is null");
        }

        OutputStream logOutStream = new ByteArrayOutputStream();
        BufferedWriter logOutWriter = new BufferedWriter(new OutputStreamWriter(logOutStream));

        BufferedWriter fastaWriter = new BufferedWriter( new FileWriter( exportedFasta ) );

        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();

        logOutWriter.write( "" +NEW_LINE);
        logOutWriter.write( "--------------------------------------------------------" +NEW_LINE);
        logOutWriter.write( "Legend:" +NEW_LINE);
        logOutWriter.write( "        . : protein's sequence exported" +NEW_LINE);
        logOutWriter.write( "        X : protein doesn't take part in any interaction." +NEW_LINE);
        logOutWriter.write( "--------------------------------------------------------" +NEW_LINE);
        logOutWriter.write( "" +NEW_LINE);

        logOutWriter.write( "Loading all proteins." +NEW_LINE);

        // Load protein count using DAO.
        int proteinCount = proteinDao.countAll();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        logOutWriter.write( proteinCount + " protein(s) loaded from the database." +NEW_LINE);

        int count = 0;
        int countExported = 0;
        int countNoSeq = 0;

        Iterator<ProteinImpl> iterator = proteinDao.iterator();
        while ( iterator.hasNext() ) {
            ProteinImpl protein = iterator.next();

            logOutWriter.write(protein.getAc());

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

                // this is necessary, because the sequence cannot be loaded lazily
                protein = proteinDao.getByAc(protein.getAc());

                sb.append( protein.getSequence() );

                sb.append( NEW_LINE );

                // write to file
                fastaWriter.write( sb.toString() );
                fastaWriter.flush();

                // stats
                countExported++;
                logOutWriter.write( "." );
                logOutWriter.flush();

            } else {
                // stats
                countNoSeq++;
                logOutWriter.write( "X" );
                logOutWriter.flush();
            }

            count++;
            if ( ( count % 70 ) == 0 ) {
                logOutWriter.write( "   " + count +NEW_LINE);
            }
        }

        logOutWriter.write( "" +NEW_LINE);
        logOutWriter.write( "----------------------------------------------------------------------------------" +NEW_LINE);
        logOutWriter.write( "Processed " + count + " protein(s)." +NEW_LINE);
        logOutWriter.write( "Exported " + countExported + " proteins" +NEW_LINE);
        logOutWriter.write( countNoSeq + " protein(s) were not involved in any interactions. They were filtered out." +NEW_LINE);

        fastaWriter.close();
        logOutWriter.close();

        return logOutStream;
    }



    public static void main( String[] args ) throws IOException {
        exportToFastaFile( new File( "intact.fasta") );
    }
}