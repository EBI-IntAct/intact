// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.model.test.util;

import uk.ac.ebi.intact.model.*;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class TestableProtein extends ProteinImpl {

    public TestableProtein( String ac, Institution owner, BioSource source, String shortLabel, CvInteractorType type, String aSequence ) {
        super( owner, source, shortLabel, type );

        if ( ac == null ) {
            throw new IllegalArgumentException( "You must give a non null AC." );
        }
        this.ac = ac;

        // count of required chunk to fit the sequence
        int maxSeqLength = getMaxSequenceLength();
        if ( aSequence != null ) {
            int chunkCount = aSequence.length() / maxSeqLength;
            if ( aSequence.length() % maxSeqLength > 0 ) {
                chunkCount++;
            }

            String chunk = null;
            for ( int i = 0; i < chunkCount; i++ ) {

                chunk = aSequence.substring( i * maxSeqLength,
                                             Math.min( ( i + 1 ) * maxSeqLength,
                                                       aSequence.length() ) );

                // create new chunk
                SequenceChunk s = new SequenceChunk( i, chunk );
                addSequenceChunk( s );
            }
        }
    }
}