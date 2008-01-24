/*
 * Copyright 2001-2008 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.kickstart;

import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.converter.xml2tab.Xml2Tab;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.io.File;
import java.util.Collection;

/**
 * Example on how to convert an PSI-MI XML to PSI-MI TAB
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ConvertPsiXmlToTab {

    public static void main(String[] args) throws Exception {
        // file to convert into PSIMITAB
        File fileToConvert = new File(ImportPsiData.class.getResource("/intact_2006-07-19.xml").getFile());

        Xml2Tab xml2Tab = new Xml2Tab();
        Collection<BinaryInteraction> binaryInteractions = xml2Tab.convert(fileToConvert);

        System.out.println("Converted "+binaryInteractions.size()+" from file: "+fileToConvert);

        // We will print the interactions in the console, but you could write the interactions
        // to a file or to a String using the appropriate parameters in the following code.
        System.out.println("\n\nPSI-MITAB2.5 Formatted output:\n\n");

        // We use the PsimiTabWriter to write the binary interactions to the writer.
        // There exists a PsimiTabReader, that reads psimitab files.
        PsimiTabWriter psimitabWriter = new PsimiTabWriter();
        psimitabWriter.write(binaryInteractions, System.out);
    }

}