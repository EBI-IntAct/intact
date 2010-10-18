/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.wswublast.client.BlastClient;
import uk.ac.ebi.intact.bridges.wswublast.client.WuBlastClientException;
import uk.ac.ebi.intact.bridges.wswublast.model.*;
import uk.ac.ebi.intact.confidence.blastmapping.BlastMappingException;
import uk.ac.ebi.intact.confidence.blastmapping.BlastMappingReader;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.EBIApplicationResult;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.TAlignment;
import uk.ac.ebi.intact.confidence.blastmapping.jaxb.THit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * EBI web service WUBlast implementation.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre>
 *        12 Sep 2007
 *        </pre>
 */
public class EbiWsWUBlast extends AbstractBlastService {
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( EbiWsWUBlast.class );
    private BlastClient bc;
    private String email;

    // ///////////////
    // Constructor
    public EbiWsWUBlast( File dbFolder, String tableName, File workDir, String email, int nrPerSubmission )
            throws BlastServiceException {
        super( dbFolder, tableName, nrPerSubmission );
        this.setWorkDir( workDir );
        this.email = email;
        bc = newBlastClientInstance();
    }

    protected BlastClient newBlastClientInstance() throws BlastServiceException {
        try {
            return new BlastClient( this.email );
        } catch ( WuBlastClientException e ) {
            throw new BlastServiceException( e );
        }
    }

    // /////////////////
    // public Methods
    public Job runBlast( UniprotAc uniprotAc ) throws WuBlastClientException {
        return bc.blast( new BlastInput( uniprotAc ) );
    }

    @Override
    public Job runBlast( BlastInput blastInput ) throws WuBlastClientException {
        return bc.blast( blastInput );
    }

    public List<Job> runBlast( Set<UniprotAc> uniprotAcs ) throws WuBlastClientException {
        Set<BlastInput> toBlast = convertToBlastInput( uniprotAcs );
        return bc.blast( toBlast );
    }

    public BlastJobStatus checkStatus( Job job ) throws WuBlastClientException {
        return bc.checkStatus( job );
    }

    public BlastOutput getResult( Job job ) throws WuBlastClientException {
        return bc.getResult( job );
    }

    public BlastResult processOutput( File blastFile ) throws WuBlastClientException {
        return parseXmlOutput( blastFile );
    }

    // /////////////////
    // private Methods
    private Set<BlastInput> convertToBlastInput( Set<UniprotAc> uniprotAcs ) {
        Set<BlastInput> toBlast = new HashSet<BlastInput>( uniprotAcs.size() );
        for ( UniprotAc uniprotAc : uniprotAcs ) {
            toBlast.add( new BlastInput( uniprotAc ) );
        }
        return toBlast;
    }

    private BlastResult parseXmlOutput( File xmlFile ) throws WuBlastClientException {
        // BlastResult result = new BlastResult();
        String fileName = xmlFile.getName();
        String[] strs = fileName.split( "\\." );
        String uniprotAc = strs[0];
        List<Hit> blastHits = new ArrayList<Hit>();
        BlastMappingReader bmr = new BlastMappingReader();

        try {
            if ( log.isTraceEnabled() ) {
                log.trace( "reading xml : " + xmlFile.getPath() );
            }
            EBIApplicationResult appResult = bmr.read( xmlFile );
            if ( appResult != null ) {
                List<THit> xmlHits = appResult.getSequenceSimilaritySearchResult().getHits().getHit();
                for ( THit hit : xmlHits ) {
                    String accession = hit.getAc();
                    String desc = hit.getDescription();
                    if ( desc.contains( "Isoform" ) ) {
                        accession = hit.getId();
                    }
                    // a value that will never be < threshold
                    Float evalue = new Float( 1000 );
                    List<TAlignment> alignments = hit.getAlignments().getAlignment();
                    // takes the first of the alignments (highest identity)
                    evalue = alignments.get( 0 ).getExpectation();
                    blastHits.add( new Hit( accession, evalue ) );
                }
                if ( uniprotAc.equals( "" ) && blastHits.size() == 0 && log.isWarnEnabled()) {
                    log.warn( "NO BlastResult was found, see file " + xmlFile.getPath() );
                }
                return new BlastResult( uniprotAc, blastHits );
            } else {
                return new BlastResult( uniprotAc, new ArrayList<Hit>( 0 ) );
            }
        } catch ( BlastMappingException e ) {
            throw new WuBlastClientException( e );
        }
    }
}
