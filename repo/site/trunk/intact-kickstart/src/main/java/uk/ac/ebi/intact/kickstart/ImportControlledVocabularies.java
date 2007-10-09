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

import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.dataexchange.cvutils.UpdateCVs;
import uk.ac.ebi.intact.dataexchange.cvutils.UpdateCVsConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;

/**
 * Example of how to import or update the controlled vocabularies in the database
 *
 * @version $Id$
 */
public class ImportControlledVocabularies {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, with the postgres hibernate file (change it accordingly to your custom info).
        // Initialization has to be always the first statement of your application and needs to be invoked only once.
        File pgConfigFile = new File(ImportPsiData.class.getResource("/postgres-hibernate.cfg.xml").getFile());
        IntactContext.initStandaloneContext(pgConfigFile);

        File oboFile = createFileFromURL(new URL("http://intact.svn.sourceforge.net/viewvc/*checkout*/intact/repo/utils/data/controlledVocab/psi-mi25-4intact.obo"));

        // Once an IntactContext has been initialized, we can access to it by getting the current instance
        IntactContext intactContext = IntactContext.getCurrentInstance();

        DataContext dataContext = intactContext.getDataContext();

        // We need to begin a transaction
        dataContext.beginTransaction();

        // To import controlled vocabularies we need to create a configuration object,
        // which can be used to tweak the import
        UpdateCVsConfig config = new UpdateCVsConfig();

        // This is the main method to import the OBO file into the database, printing some debug
        // information in the console
        UpdateCVs.load(oboFile, System.out, config);

        // Don't forget to commit the transaction
        dataContext.commitTransaction();

    }

    private static File createFileFromURL(URL url) throws Exception {
        File tempFile = File.createTempFile("oboFile", ".obo");

        PrintStream ps = new PrintStream(tempFile);

        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            ps.println(line);
        }

        ps.close();

        return tempFile;
    }
}