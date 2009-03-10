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
import psidev.psi.mi.tab.expansion.SpokeExpansion;
import psidev.psi.mi.tab.converter.xml2tab.Xml2Tab;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.xml.model.EntrySet;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.dataexchange.psimi.xml.exchange.PsiExchange;
import uk.ac.ebi.intact.model.IntactEntry;
import uk.ac.ebi.intact.persistence.dao.entry.IntactEntryFactory;
import uk.ac.ebi.intact.psimitab.converters.util.DatabaseSimpleMitabExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * Creates an IntAct-extended PSI-MITAB file using all the data from the database.
 *
 * @version $Id$
 */
public class ExportDbToIntactPsiMiTab {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, with the postgres hibernate file (change it accordingly to your custom info).
        // Initialization has to be always the first statement of your application and needs to be invoked only once.
        File pgConfigFile = new File(ImportPsiData.class.getResource("/postgres-hibernate.cfg.xml").getFile());
        IntactContext.initStandaloneContext(pgConfigFile);

        // the writer use to write the MITAB
        Writer writer = new FileWriter("/tmp/alldb.mitab.txt");

        // we use a database exporter, to put all the database data into the file
        DatabaseSimpleMitabExporter exporter = new DatabaseSimpleMitabExporter();

        // this is the method used to export everything
        exporter.exportAllInteractions(writer);

        // closing the writer, enjoy!
        writer.close();
    }
}