/**
 * Copyright 2011 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intact.solution.chapter2.exercise3;

import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.xml.converter.ConverterException;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntactPsimiTabReader;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Read a MITAB file using the method of your choice and count how many interaction are original experimental
 * binary interaction and how many have been generated using the spoke model.
 *
 * @see uk.ac.ebi.intact.psimitab.IntactPsimiTabReader
 * @see uk.ac.ebi.intact.psimitab.IntactPsimiTabReader#iterate(java.io.File)
 * @see uk.ac.ebi.intact.psimitab.IntactBinaryInteraction
 * @see uk.ac.ebi.intact.psimitab.IntactBinaryInteraction#getExpansionMethods()
 */
public class Q3_FilterSpokeInteractions {

    private static final String SPOKE = "Spoke";

    public static void main( String[] args ) throws ConverterException, IOException {

        // Prepare the input MITAB file
        File intputFile = new File( Q3_FilterSpokeInteractions.class.getResource( "/samples/mitab/17129783.txt" ).getFile() );

        System.out.println( "Iterating over extended MITAB data from: " + intputFile.getAbsolutePath() );

        // Instanciate the reader class that supports the IntAct extended MITAB data format
        PsimiTabReader reader = new IntactPsimiTabReader( true );

        // Prepare for iterating over the file.
        final Iterator<BinaryInteraction> interactions = reader.iterate( intputFile );

        int count = 0;
        int countExperimentalInteractions = 0;
        int countSpokeInteractions = 0;

        while ( interactions.hasNext() ) {
            // Here we get an IntAct specific BinaryInteraction that gives access to the additional fields
            IntactBinaryInteraction bi = ( IntactBinaryInteraction ) interactions.next();

            final List<String> expansionMethods = bi.getExpansionMethods();
            if ( expansionMethods.contains( SPOKE ) ) {
                countSpokeInteractions++;
            } else {
                countExperimentalInteractions++;
            }
            count++;
        }

        System.out.println( "Read " + count + " IntAct binary interactions" );
        System.out.println( "\t Of which " + countExperimentalInteractions + " were experimentaly reported binary interactions" );
        System.out.println( "\t Of which " + countSpokeInteractions + " were expanded using the spoke model" );
    }
}