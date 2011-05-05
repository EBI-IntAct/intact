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

package intact.exercise.psimitab;

import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;
import psidev.psi.mi.tab.model.Interactor;
import psidev.psi.mi.xml.converter.ConverterException;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Read a MITAB file using the method of your choice and count all interactions involving
 * the interactor having the given uniprot AC.
 *
 * @see psidev.psi.mi.tab.PsimiTabReader
 * @see psidev.psi.mi.tab.model.BinaryInteraction
 * @see psidev.psi.mi.tab.model.BinaryInteraction#getInteractorA()
 * @see psidev.psi.mi.tab.model.BinaryInteraction#getInteractorB()
 * @see psidev.psi.mi.tab.model.Interactor
 * @see psidev.psi.mi.tab.model.Interactor#getIdentifiers()
 */
public class FilterByUniprotAc {
    
    private static final String UNIPROT = "uniprotkb";

    public static void main( String[] args ) throws ConverterException, IOException {

        final String uniprotAc = "Q9M3A3";

        // Prepare the input MITAB file
        File intputFile = new File( ReadWholeFile.class.getResource( "/samples/mitab/17267444.txt" ).getFile() );

        System.out.println( "Iterating over MITAB data from: " + intputFile.getAbsolutePath() );

        // Instanciate the reader class that supports the Standard MITAB data format
        PsimiTabReader reader = new PsimiTabReader( true );

        // Prepare for iterating over the file.
        final Iterator<BinaryInteraction> interactions = reader.iterate( intputFile );

        int count = 0;
        int interactionMatch = 0;
        while ( interactions.hasNext() ) {
            BinaryInteraction binaryInteraction = interactions.next();

            if( hasUniprotAc( binaryInteraction.getInteractorA(), uniprotAc )
                || hasUniprotAc( binaryInteraction.getInteractorB(), uniprotAc ) ) {
                interactionMatch++;
            }

            count++;
        }

        System.out.println( "Read " + count + " binary interactions" );
        System.out.println( "Of which " + interactionMatch + " " +
                            "involve an interaction with the UniProt AC: '"+ uniprotAc +"'" );
    }

    private static boolean hasUniprotAc( Interactor interactor, String ac ) {
        for ( CrossReference ref : interactor.getIdentifiers() ) {
            if( UNIPROT.equals( ref.getDatabase() ) && ac.equals( ref.getIdentifier() ) ) {
                return true;
            }
        }
        return false;
    }
}