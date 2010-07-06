package uk.ac.ebi.intact.util.uniprotExport;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 2010-07-06 Uniprot reported that some CC lines get duplicated. This script removed that redundancy and merges the
 * lines together.
 */
public class FixDuplicatedCCLines {

    public static final Pattern PARTNER_LINE = Pattern.compile( "^CC\\s{7}(.{4,6})(?::.*){0,1}; NbExp=\\d+;.*" );
    
    public static final Pattern NB_EXP = Pattern.compile( "^CC.*NbExp=(\\d+);.*" );

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    public static void main( String[] args ) throws IOException {

        // current directory
        File root = new File( "" );

        final File inputCCexport = new File( root, "uniprotcomments.dat" );
        if( ! inputCCexport.exists() ) {
            System.err.println( "Could not find input file: " + inputCCexport.getAbsolutePath() );
            System.exit( 1 );
        }

        BufferedReader in = new BufferedReader( new FileReader( inputCCexport ) );
        BufferedWriter out = new BufferedWriter( new FileWriter( new File( "uniprotcomments.2.dat" ) ) );

        String current = null;
        String currentAc = null;
        String previous = null;
        String previousAc = null;

        int replacementCount = 0;
        int mergeCount = 0;
        List<Integer> cases = new ArrayList<Integer>();

        int lineCount = 0;
        
        while ( ( current = in.readLine() ) != null ) {

            lineCount++;

            if( current.equals( "//" ) ) {

                if( previous != null ) {
                    out.write( previous + NEW_LINE );

                    cases.add( mergeCount );
                    mergeCount=0;
                }

                previous = null;
                previousAc = null;

                out.write( current + NEW_LINE );

            } else {

                if( current.startsWith( "AC" ) ) {


                    out.write( current + NEW_LINE );
                    System.out.println( "> "+current );
                    // start of a block
                    previous = null;

                } else if( current.startsWith( "CC   -!-" )){

                    out.write( current + NEW_LINE );
                    // nothing

                } else {
                    
                    // reading partner line
                    currentAc = extractUniprotAc( current );
                    System.out.println( "> " + current + "\t\t\t["+currentAc+"]" );

                    if( currentAc == null ) {
                        throw new IllegalStateException( "line " + lineCount + ": current AC is null for line '"+current+"'" );
                    }

                    if( previousAc != null ) {

                        if( currentAc.equals( previousAc ) ) {
                            // found duplication !

                            int ld = StringUtils.getLevenshteinDistance( current.replaceAll( "NbExp=\\d+;","NbExp=X;" ),
                                                                         previous.replaceAll( "NbExp=\\d+;","NbExp=X;" ));
                            if( ld!= 0 ) {
                                throw new IllegalStateException(  );
                            }

                            mergeCount++;
                            replacementCount++;

                            int currentNbExp = extractNbExp( current );
                            int previousNbExp = extractNbExp( previous );

                            System.out.println( "============================================================================" );
                            System.out.println( "= Replacing:\n= "+ previous );
                            previous = previous.replaceFirst( "NbExp=\\d+;", "NbExp="+ (currentNbExp + previousNbExp) +";" );
                            System.out.println( "= By:\n= "+ previous );

                        } else {

                            if( previous != null ) {
                                out.write( previous + NEW_LINE );

                                cases.add( mergeCount );
                                mergeCount=0;
                            }

                            previousAc = currentAc;
                            previous = current;
                        }

                    } else {
                        previousAc = currentAc;
                        previous = current;
                    }

                } // partner line
            } // not end of CC block

        } // lines

        in.close();

        out.flush();
        out.close();

        System.out.println( "Replacements count: " + replacementCount );

        // Compute and display how many lines were duplicated, triplicated, ...
        int[] array = new int[]{0,0,0,0,0,0,0,0,0,0};
        for ( Integer i : cases ) {
            array[i] = array[i] + 1;
        }

        for ( int i = 0; i < array.length; i++ ) {
            int val = array[i];
            System.out.println( i+") " + val );
        }
    }

    private static String extractUniprotAc( String current ) {

        final Matcher matcher = PARTNER_LINE.matcher( current );
        if( matcher.matches() ) {

            return matcher.group( 1 );
        }

        return null;
    }

    private static int extractNbExp( String current ) {

        final Matcher matcher = NB_EXP.matcher( current );
        if( matcher.matches() ) {

            return Integer.parseInt( matcher.group( 1 ) );
        }

        return -1;
    }
}
