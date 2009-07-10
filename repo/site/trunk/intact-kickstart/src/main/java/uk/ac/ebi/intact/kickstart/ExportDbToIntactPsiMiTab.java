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

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.psimitab.converters.util.DatabaseSimpleMitabExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/**
 * Creates an IntAct-extended PSI-MITAB file using all the data from the database.
 *
 * @version $Id$
 */
public class ExportDbToIntactPsiMiTab {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, using the default configuration found in the file h2-hibernate.cfg.xml..
        // Initialization has to be always the first statement of your application and needs to be invoked only once.
        File pgConfigFile = new File(ImportPsiData.class.getResource("/h2-hibernate.cfg.xml").getFile());
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