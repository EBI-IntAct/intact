// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.model.test.util;

import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.SequenceChunk;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class TestableProtein extends ProteinImpl {

    public TestableProtein( String ac, Institution owner, BioSource source, String shortLabel, String aSequence ) {
        super( owner, source, shortLabel );

        if( ac == null ) {
            throw new IllegalArgumentException( "You must give a non null AC." );
        }
        this.ac = ac;

        // count of required chunk to fit the sequence
        int chunkCount = aSequence.length() / MAX_SEQUENCE_LENGTH;
        if( aSequence.length() % MAX_SEQUENCE_LENGTH > 0 ) {
            chunkCount++;
        }

        String chunk = null;
        for( int i = 0; i < chunkCount; i++ ) {

            chunk = aSequence.substring( i * MAX_SEQUENCE_LENGTH,
                                         Math.min( ( i + 1 ) * MAX_SEQUENCE_LENGTH,
                                                   aSequence.length() ) );

            // create new chunk
            SequenceChunk s = new SequenceChunk( i, chunk );
            addSequenceChunk( s );
        }
    }
}