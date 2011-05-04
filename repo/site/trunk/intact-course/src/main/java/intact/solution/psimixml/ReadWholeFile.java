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

package intact.solution.psimixml;

import psidev.psi.mi.xml.PsimiXmlReader;
import psidev.psi.mi.xml.PsimiXmlReaderException;
import psidev.psi.mi.xml.model.Entry;
import psidev.psi.mi.xml.model.EntrySet;
import psidev.psi.mi.xml.model.Interaction;

import java.io.File;

/**
 * Read a entire PSI-MI XML file in memory using the Java API.
 */
public class ReadWholeFile {
    public static void main( String[] args ) throws PsimiXmlReaderException {

        // The data file to be parsed by the API
        File inputFile = new File( ReadWholeFile.class.getResource( "/samples/psixml25/16705748.xml" ).getFile() );

        // Create a reader
        PsimiXmlReader reader = new PsimiXmlReader();

        // Read the whole file into an EntrySet
        final EntrySet entrySet = reader.read( inputFile );

        // Show all interactions, their respective id and label
        for ( Entry entry : entrySet.getEntries() ) {
            for ( Interaction interaction : entry.getInteractions() ) {
                final String label = interaction.getNames().getShortLabel();
                final int id = interaction.getId();

                System.out.println( "Interaction " + id + ": " + label );
            }
        }
    }
}
