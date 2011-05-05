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
import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.xml.converter.ConverterException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Read a Standard MITAB file and write it back to an other file.
 *
 * @see psidev.psi.mi.tab.PsimiTabReader
 * @see psidev.psi.mi.tab.model.BinaryInteraction
 * @see psidev.psi.mi.tab.PsimiTabWriter
 * @see psidev.psi.mi.tab.PsimiTabWriter#write(java.util.Collection, java.io.Writer)
 */
public class WriteToFile {

    public static void main( String[] args ) throws ConverterException, IOException {
        // Prepare the input MITAB file
        File intputFile = new File( ReadWholeFile.class.getResource( "/samples/mitab/18189341.txt" ).getFile() );

        System.out.println( "Iterating over MITAB data from: " + intputFile.getAbsolutePath() );

        // Instantiate the reader class that supports the Standard MITAB data format
        PsimiTabReader reader = new PsimiTabReader( true );

        // Prepare for iterating over the file.
        final Iterator<BinaryInteraction> interactions = reader.iterate( intputFile );

        PsimiTabWriter tabwriter = new PsimiTabWriter();
        tabwriter.setHeaderEnabled( true );

        File outputFile = new File( "18189341.out.txt" );
        Writer fileWriter = new FileWriter( outputFile );

        int count = 0;
        while ( interactions.hasNext() ) {
            BinaryInteraction binaryInteraction = interactions.next();
            count++;

            tabwriter.write( binaryInteraction, fileWriter );
        }

        System.out.println( "Wrote " + count + " binary interactions to " + outputFile.getAbsolutePath() );
    }
}