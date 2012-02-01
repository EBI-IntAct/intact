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

import org.springframework.transaction.TransactionStatus;
import psidev.psi.mi.tab.PsimiTabWriter;
import psidev.psi.mi.tab.converter.xml2tab.Xml2Tab;
import psidev.psi.mi.tab.expansion.SpokeExpansion;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.xml.model.EntrySet;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.entry.IntactEntryFactory;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchange;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchangeFactory;
import uk.ac.ebi.intact.model.IntactEntry;

import java.util.Collection;

/**
 * Example that shows how to export interactions to PSI-MITAB2.5 format.
 *
 * @version $Id$
 */
public class ExportToPsiMiTab {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, using the default configuration found in the file hsql.spring.xml..
        IntactContext.initContext(new String[] {"/META-INF/kickstart.spring.xml"});

        // Once an IntactContext has been initialized, we can access to it by getting the current instance
        IntactContext intactContext = IntactContext.getCurrentInstance();

        // start a transaction
        DataContext dataContext = intactContext.getDataContext();
        TransactionStatus transactionStatus = dataContext.beginTransaction();


        // When exporting, we need to create an IntactEntry object, which contains the interactions
        // and all the related information (e.g. interactors, experiments, features...) that we want
        // to export
        IntactEntry intactEntry = IntactEntryFactory.createIntactEntry(intactContext).addPublicationId("16469704");

        // This is the main method to export data. An EntrySet is the equivalent to the IntactEntry object
        // but is a member of the PSI-MI model (IntactEntry is for the Intact model)
        PsiExchange psiExchange = PsiExchangeFactory.createPsiExchange(intactContext);
        EntrySet entrySet = psiExchange.exportToEntrySet(intactEntry);

        dataContext.commitTransaction(transactionStatus);

        // Setup a interaction expansion strategy that is going to transform n-ary interactions into binaries using
        // the spoke expansion algorithm
        Xml2Tab xml2tab = new Xml2Tab();
        xml2tab.setExpansionStrategy( new SpokeExpansion() );

        // Perform the conversion and collect binary interactions
        Collection<BinaryInteraction> binaryInteractions = xml2tab.convert(entrySet);

        // We will print the interactions in the console, but you could write the interactions
        // to a file or to a String using the appropriate parameters in the following code.
        System.out.println("\n\nPSI-MITAB2.5 Formatted output:\n\n");

        // We use the PsimiTabWriter to write the binary interactions to the writer.
        // There exists a PsimiTabReader, that reads psimitab files.
        PsimiTabWriter psimitabWriter = new PsimiTabWriter();
        psimitabWriter.write(binaryInteractions, System.out);
    }
}