package uk.ac.ebi.intact.sanity.check.range;

import org.biojava.bio.BioException;
import org.biojava.bio.alignment.NeedlemanWunsch;
import org.biojava.bio.alignment.SequenceAlignment;
import org.biojava.bio.alignment.SmithWaterman;
import org.biojava.bio.alignment.SubstitutionMatrix;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.FiniteAlphabet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to run sequence alignements.
 *
 * @author Samuel Kerrien
 * @version $Id$
 * @since 2.0
 */
public class SequenceAlignementUtils {

    private static SubstitutionMatrix getBlosum62() throws AlignementException {
        FiniteAlphabet alphabet = ProteinTools.getTAlphabet();
        String blosumFile = SequenceAlignementUtils.class.getResource( "/BLOSUM62.txt" ).getFile();
        if ( blosumFile == null ) {
            throw new IllegalStateException( "Could not find /BLOSUM62.txt in the classpath" );
        }

        SubstitutionMatrix matrix = null;
        try {
            matrix = new SubstitutionMatrix( alphabet, new File( blosumFile ) );
        } catch ( IOException e ) {
            throw new AlignementException( e );
        } catch ( BioException e ) {
            throw new AlignementException( e );
        }
        return matrix;
    }

    public static String alignNeedlemanWunsch( String querySeq, String queryName, String targetSeq, String targetName ) throws AlignementException {

        try {
            Sequence query = ProteinTools.createProteinSequence( querySeq, queryName );
            Sequence target = ProteinTools.createProteinSequence( targetSeq, targetName );

            SequenceAlignment aligner = new NeedlemanWunsch(
                    0,      // match
                    3,      // replace
                    2,      // insert
                    2,      // delete
                    1,      // gapExtend
                    getBlosum62()  // SubstitutionMatrix
            );

            aligner.pairwiseAlignment( query, target );

            // Print the alignment to the screen
            return aligner.getAlignmentString();
        } catch ( Exception e ) {
            throw new AlignementException( e.getMessage(), e );
        }
    }

    public static String alignSmithWaterman( String querySeq, String queryName, String targetSeq, String targetName ) throws AlignementException {

        try {
            Sequence query = ProteinTools.createProteinSequence( querySeq, queryName );
            Sequence target = ProteinTools.createProteinSequence( targetSeq, targetName );

            SequenceAlignment aligner = new SmithWaterman(
                    -1,     // match
                    3,      // replace
                    2,      // insert
                    2,      // delete
                    1,      // gapExtend
                    getBlosum62()  // SubstitutionMatrix
            );

            aligner.pairwiseAlignment( query, target );

            // Print the alignment to the screen
            return aligner.getAlignmentString();
        } catch ( Exception e ) {
            throw new AlignementException( e );
        }
    }


    public static void sequenceAlignement( String seq1, String seq2 ) throws Exception {
        Sequence query = ProteinTools.createProteinSequence( "SSWWAHVEMGPPADPILGVTEAYKRDTNSKKSSWWAHVEMGPPADPILGVTEAYKRDTNSKK", "query" );
        Sequence target = ProteinTools.createProteinSequence( "PPDPILGVTE", "target" );

        FiniteAlphabet alphabet = ProteinTools.getTAlphabet();
        SubstitutionMatrix matrix = new SubstitutionMatrix( alphabet, new File( "C:\\BLOSUM62.txt" ) );
        SequenceAlignment aligner = new NeedlemanWunsch(
                0,      // match
                3,      // replace
                2,      // insert
                2,      // delete
                1,      // gapExtend
                matrix  // SubstitutionMatrix
        );

        aligner.pairwiseAlignment( query, target );

        // Print the alignment to the screen
        System.out.println( "Global alignment with Needleman-Wunsch:\n" + aligner.getAlignmentString() );

        aligner = new SmithWaterman(
                -1,     // match
                3,      // replace
                2,      // insert
                2,      // delete
                1,      // gapExtend
                matrix  // SubstitutionMatrix
        );

        aligner.pairwiseAlignment( query, target );
        System.out.println( "\nlocal alignment with Smith-Waterman:\n" + aligner.getAlignmentString() );
    }

    /////////////////////////////////////
    // Home made exact string matching

    private static String generateString( int length, char c ) {
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < length; i++ ) {
            sb.append( c );
        }
        return sb.toString();
    }

    public static List<Integer> findExactMatches( String query, String target ) {
        List<Integer> matches = new ArrayList<Integer>();

        // check if query contains the target
        int idx = 0;

        boolean couldHaveMoreMatches = true;
        do {
            final int pos = query.indexOf( target, idx );
            if ( pos != -1 ) {
                idx = pos + 1;
                matches.add( idx );
            } else {
                couldHaveMoreMatches = false;
            }
        } while ( couldHaveMoreMatches );

        return matches;
    }

    public static String buildMatch( int match, String query, String target ) {
        StringBuilder sb = new StringBuilder( query.length() * 3 + 2 ); // 3 lines + 2 carriage return

        match = match - 1;

        final int startMatch = match;
        final int endMatch = match + target.length();

        sb.append( '\n' );
        sb.append( "1 " ).append( query ).append( ' ' ).append( query.length() ).append( '\n' );
        sb.append( "  " ).append( generateString( startMatch, ' ' ) ).append( generateString( target.length(), '|' ) ).append( '\n' );
        sb.append( "1 " ).append( generateString( startMatch, '~' ) ).append( target ).append( generateString( query.length() - endMatch, '-' ) ).append( ' ' ).append( target.length() );
        sb.append( '\n' );

        return sb.toString();
    }

    

    public static void main( String[] args ) throws Exception {
        String seq1 = "SSWWAHVEMGPPDPILGVTEAYKRDTNSKKSSWWAHVEMGPPDPILGVTEAYKRDTNSKK";
        String seq2 = "PPDPILGVTE";
        sequenceAlignement( seq1, seq2 );

        findExactMatches( seq1, seq2 );
    }
}
