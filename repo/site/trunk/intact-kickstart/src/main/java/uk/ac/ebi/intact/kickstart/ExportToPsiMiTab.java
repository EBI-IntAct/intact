/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
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
import psidev.psi.mi.xml.model.EntrySet;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchange;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.persistence.dao.entry.IntactEntryFactory;

import java.io.File;
import java.util.Collection;

/**
 * Example that shows how to export interacting polymer sequences to Fasta format.
 *
 * @version $Id$
 */
public class ExportToPsiMiTab {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, with the postgres hibernate file (change it accordingly to your custom info).
        // Initialization has to be always the first statement of your application and needs to be invoked only once.
        File pgConfigFile = new File(ImportPsiData.class.getResource("/postgres-hibernate.cfg.xml").getFile());
        IntactContext.initStandaloneContext(pgConfigFile);

        // Once an IntactContext has been initialized, we can access to it by getting the current instance
        IntactContext intactContext = IntactContext.getCurrentInstance();

        DataContext dataContext = intactContext.getDataContext();

        // We need to begin a transaction
        dataContext.beginTransaction();

        // When exporting, we need to create an IntactEntry object, which contains the interactions
        // and all the related information (e.g. interactors, experiments, features...) that we want
        // to export
        IntactEntry intactEntry = IntactEntryFactory.createIntactEntry(intactContext).addPublicationId("16469705");

        // This is the main method to export data. An EntrySet is the equivalent to the IntactEntry object
        // but is a member of the PSI-MI model (IntactEntry is for the Intact model)
        EntrySet entrySet = PsiExchange.exportToEntrySet(intactEntry);

        // And don't forget to commit the transaction
        dataContext.commitTransaction();

        Xml2Tab xml2tab = new Xml2Tab();
        Collection<BinaryInteraction> binaryInteractions = xml2tab.convert(entrySet);

        // We will print the interactions in the console, but you could write the interactions
        // to a file or to a String using the appropriate parameters in the following code.
        System.out.println("\n\nPSI-MI Formatted output:\n\n");

        // We use the PsimiTabWriter to write the binary interactions to the writer.
        // There exists a PsimiTabReader, that reads psimitab files.
        PsimiTabWriter psimitabWriter = new PsimiTabWriter();
        psimitabWriter.write(binaryInteractions, System.out);



    }
}