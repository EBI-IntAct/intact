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

package intact.solution.chapter7.exercise2;

import psidev.psi.mi.xml.PsimiXmlReader;
import psidev.psi.mi.xml.PsimiXmlReaderException;
import psidev.psi.mi.xml.PsimiXmlWriter;
import psidev.psi.mi.xml.PsimiXmlWriterException;
import psidev.psi.mi.xml.model.EntrySet;

import java.io.File;

/**
 * Question 1: Could you write an EntrySet to a file by using the  psidev.psi.mi.xml.PsimiXmlWriter?
 */
public class Q1_WriteToFile {
    public static void main( String[] args ) throws PsimiXmlReaderException, PsimiXmlWriterException {

        // The data file to be parsed by the API
        File inputFile = new File( Q1_WriteToFile.class.getResource( "/samples/psixml25/16705748.xml" ).getFile() );

        // Create a reader
        PsimiXmlReader reader = new PsimiXmlReader();

        // Read the whole file into an EntrySet
        final EntrySet entrySet = reader.read( inputFile );

        // Do some update on the data (eg. expand all n-ary interaction using the spoke model)

        // Instanciate the PsimiXmlWriter that is responsible for writing the data into a file
        PsimiXmlWriter writer = new PsimiXmlWriter();

        // Preparing the output file
        final File outputFile = new File( "16705748.updated.xml" );
        System.out.println( "Writing the data to: " + outputFile.getAbsolutePath() );

        // Write the data to the output file
        writer.write( entrySet, outputFile );
    }
}
