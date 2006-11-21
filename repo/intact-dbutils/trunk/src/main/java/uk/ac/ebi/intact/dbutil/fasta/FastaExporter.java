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
import java.util.List;

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

        CvXrefQualifier identity = IntactContext.getCurrentInstance().getCvContext().getByMiRef( CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF );

        for ( Xref xref : interactor.getXrefs() ) {
            if ( identity.equals( xref.getCvXrefQualifier() ) ) {
                return xref.getPrimaryId();
            }
        }

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
        
        List<ProteinImpl> proteins = null;
        int firstResult = 0;
        int maxResults = 300;

        do
        {
            proteins = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getProteinDao().getAll(firstResult, maxResults);

            firstResult += maxResults;


            for (ProteinImpl protein : proteins)
            {
                if (log.isDebugEnabled())
                {
                    log.debug(protein.getAc());
                }

                int componentsSize = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                        .getProteinDao().countComponentsForInteractorWithAc(protein.getAc());

                // Process the chunk of data
                if (componentsSize > 0)
                {

                    StringBuffer sb = new StringBuffer(512);

                    // Header contains: AC, shortlabel and primaryId.
                    sb.append('>').append(' ');
                    sb.append(protein.getAc());
                    sb.append('|');
                    sb.append(protein.getShortLabel());
                    String identity = getIdentity(protein);
                    if (identity != null)
                    {
                        sb.append('|');
                        sb.append(identity);
                    }

                    sb.append(NEW_LINE);

                    String sequence = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                            .getPolymerDao().getSequenceByPolymerAc(protein.getAc());
                    sb.append(sequence);

                    sb.append(NEW_LINE);

                    // write to file
                    fastaWriter.write(sb.toString());
                    fastaWriter.flush();

                    // stats
                    countExported++;
                    out.print(".");

                }
                else
                {
                    // stats
                    countNoSeq++;
                    out.print("X");
                }

                count++;
                if ((count % 70) == 0)
                {
                    out.println("   " + count);
                }
            }

            IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        } while (!proteins.isEmpty());

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