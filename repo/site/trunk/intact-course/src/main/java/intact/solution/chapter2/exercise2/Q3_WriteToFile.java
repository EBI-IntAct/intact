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

package intact.solution.chapter2.exercise2;

import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Question 3:  Now that we have read the content of a MITAB file/stream, we can attempt to write this content back to
 * a file. Write a program that writes the MITAB content read into a file.
 *
 * @see psidev.psi.mi.tab.PsimiTabReader
 * @see psidev.psi.mi.tab.model.BinaryInteraction
 * @see psidev.psi.mi.tab.PsimiTabWriter
 * @see psidev.psi.mi.tab.PsimiTabWriter#write(java.util.Collection, java.io.Writer)
 */
public class Q3_WriteToFile {

    public static void main( String[] args ) throws IOException, PsimiTabException {
        // Prepare the input MITAB file
        File intputFile = new File( Q1_ReadWholeFile.class.getResource( "/samples/mitab/18189341.txt" ).getFile() );

        System.out.println( "Iterating over MITAB data from: " + intputFile.getAbsolutePath() );

        // Instantiate the reader class that supports the Standard MITAB data format
        PsimiTabReader reader = new PsimiTabReader( );

        // Read the collections from the file
        final Collection<BinaryInteraction> interactions = reader.read(intputFile);

        File outputFile = new File( "18189341.out.txt" );

        PsimiTabWriter tabwriter = new PsimiTabWriter();

        tabwriter.write(interactions, outputFile);

        System.out.println(interactions.size()+"interactions written to file: "+outputFile);
    }
}