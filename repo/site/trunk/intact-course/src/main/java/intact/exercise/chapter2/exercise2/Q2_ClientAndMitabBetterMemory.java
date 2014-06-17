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

package intact.exercise.chapter2.exercise2;

import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.io.File;

/**
 * Question 2:  Should you attempt to load the whole content of a data file/stream into memory could cause problems
 * if the volume of data is large. To facilitate this the class psidev.psi.mi.tab.PsimiTabReader also allows
 * developers to iterate over the data.
 *
 * Now write an other program (similar to question 2) that implements this more efficient memory management.
 *
 */
public class Q2_ClientAndMitabBetterMemory {

    public static void main(String[] args) throws Exception {

        // Prepare the input MITAB file
        File intputFile = new File( Q1_ReadWholeFile.class.getResource( "/samples/mitab/18189341.txt" ).getFile() );

        System.out.println( "Reading MITAB data from: " + intputFile.getAbsolutePath() );

        // Instantiate the reader class that supports the Standard MITAB data format
        PsimiTabReader reader = new PsimiTabReader( );

        // TODO start here - iterate through the interactions in the file using the reader
    }

    private static void printBinaryInteraction(BinaryInteraction<?> binaryInteraction) {
        // print first ids for interactors and interaction
        CrossReference idA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next();
        CrossReference idB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next();
        CrossReference interactionAc = binaryInteraction.getInteractionAcs().iterator().next();

        System.out.println("Interaction "+interactionAc.getIdentifier()+": "+idA.getIdentifier()+" - "+idB.getIdentifier());
    }
}
