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

import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.converter.tab2xml.Tab2Xml;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.xml.PsimiXmlWriter;
import psidev.psi.mi.xml.model.EntrySet;

import java.io.File;
import java.util.Collection;

/**
 * Example on how to convert an PSI-MI TAB to PSI-MI XML
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ConvertPsiMiTabToXml {

    public static void main(String[] args) throws Exception {
        // file to convert into PSIMITAB
        File fileToConvert = new File(ImportPsiData.class.getResource("/16469705.txt").getFile());

        // create the PSIMITAB reader
        PsimiTabReader reader = new PsimiTabReader(true);

        // read the binary interactions in the file
        Collection<BinaryInteraction> binaryInteractions = reader.read(fileToConvert);

        System.out.println(binaryInteractions.size()+" binary interactions were read from file: "+fileToConvert);
        
        // create the converter from TAB to XML
        Tab2Xml tab2xml = new Tab2Xml();
        EntrySet entrySet = tab2xml.convert(binaryInteractions);
        
        // instantiate the writer to write XML
        PsimiXmlWriter writer = new PsimiXmlWriter();

        // print the entrySet in System.out (it could be written to a file, for instance as well)
        writer.write(entrySet, System.out);

    }

}