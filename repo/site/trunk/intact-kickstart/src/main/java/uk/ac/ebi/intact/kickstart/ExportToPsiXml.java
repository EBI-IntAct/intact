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
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchange;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchangeFactory;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Publication;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Example of how to export data to PSI-MI XML 2.5 files from the database
 *
 * @version $Id$
 */
public class ExportToPsiXml {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, using the default configuration found in the file hsql.spring.xml..
        IntactContext.initContext(new String[] {"/META-INF/kickstart.spring.xml"});

        // Once an IntactContext has been initialized, we can access to it by getting the current instance
        IntactContext intactContext = IntactContext.getCurrentInstance();

        DataContext dataContext = intactContext.getDataContext();

        final TransactionStatus transactionStatus = dataContext.beginTransaction();

        // The DaoFactory is the central access point to all the DAOs (Data Access Objects)
        DaoFactory daoFactory = dataContext.getDaoFactory();

        // We want to export the interactions from a publication (e.g. 16469704)
        Publication publication = daoFactory.getPublicationDao().getByShortLabel("16469704");

        // We can get the experiments a publication is refering to
        Collection<Experiment> experiments = publication.getExperiments();

        // We will iterate through all the experiments and store the interactions in this list
        List<Interaction> interactions = new ArrayList<Interaction>();

        for (Experiment experiment : experiments) {
            interactions.addAll(experiment.getInteractions());
        }

        // When exporting, we need to create an IntactEntry object, which contains the interactions
        // and all the related information (e.g. interactors, experiments, features...) that we want
        // to export
        IntactEntry intactEntry = new IntactEntry(interactions);

        // NOTE: as an alternative to create the IntactEntry object, the following commented statement could be used:

        //IntactEntry intactEntry = IntactEntryFactory.createIntactEntry(intactContext).addPublicationId("16469704");

        // In this example we will export using a StringWriter, but you could provide other writers here
        Writer writer = new StringWriter();

        // This is the main method to export data. You need to provide the IntactEntry and a writer/file.
        final PsiExchange psiExchange = PsiExchangeFactory.createPsiExchange(intactContext);
        psiExchange.exportToPsiXml(writer, intactEntry);

        dataContext.commitTransaction(transactionStatus);
        
        // We print what has been writen in the console
        System.out.println("\n\nPSI-XML Formatted output:\n\n");
        System.out.println(writer);

    }
}