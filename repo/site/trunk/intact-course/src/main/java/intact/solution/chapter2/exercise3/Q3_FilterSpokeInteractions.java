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
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.xml.converter.ConverterException;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Question 3:  Like MITAB2.6 and higher, the IntAct extended MITAB format does have a column that describe potential
 * complex expansion that may have been applied to generate binary interactions.
 * Write a program that reads an IntAct MITAB file and  print the following:
 *
 * total count of interactions;
 * count of spoke expanded interaction;
 * count of experimentaly identified binary interaction (i.e. not expanded).
 *
 */
public class Q3_FilterSpokeInteractions {

    private static final String SPOKE = "spoke expansion";

    public static void main( String[] args ) throws ConverterException, IOException {

        // Prepare the input MITAB file
        File intputFile = new File( Q3_FilterSpokeInteractions.class.getResource( "/samples/mitab/17129783.txt" ).getFile() );

        System.out.println( "Iterating over extended MITAB data from: " + intputFile.getAbsolutePath() );

        // Instanciate the reader class that supports the IntAct extended MITAB data format
        PsimiTabReader reader = new PsimiTabReader( );

        // Prepare for iterating over the file.
        final Iterator<BinaryInteraction> interactionIterator = reader.iterate( intputFile );

        int count = 0;
        int countExperimentalInteractions = 0;
        int countSpokeInteractions = 0;

        while ( interactionIterator.hasNext() ) {
            // Here we get an IntAct specific BinaryInteraction that gives access to the additional fields
            BinaryInteraction bi = ( BinaryInteraction ) interactionIterator.next();

            final List<CrossReference> expansionMethods = bi.getComplexExpansion();
            if ( !expansionMethods.isEmpty() ) {
                for (CrossReference ref : expansionMethods){
                    if (ref.getText() != null && ref.getText().equals(SPOKE)){
                        countSpokeInteractions++;
                    }
                }
            } else {
                countExperimentalInteractions++;
            }
            count++;
        }

        System.out.println( "Read " + count + " IntAct binary interactionIterator" );
        System.out.println( "\t Of which " + countExperimentalInteractions + " were experimentaly reported binary interactionIterator" );
        System.out.println( "\t Of which " + countSpokeInteractions + " were expanded using the spoke model" );
    }
}