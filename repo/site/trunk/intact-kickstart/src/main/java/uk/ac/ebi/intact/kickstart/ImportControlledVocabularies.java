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
import uk.ac.ebi.intact.dataexchange.cvutils.CvUpdater;
import uk.ac.ebi.intact.dataexchange.cvutils.CvUpdaterStatistics;
import uk.ac.ebi.intact.dataexchange.cvutils.OboUtils;
import uk.ac.ebi.intact.dataexchange.cvutils.model.IntactOntology;

import java.io.File;

/**
 * Example of how to import or update the controlled vocabularies in the database.
 *
 * @version $Id$
 */
public class ImportControlledVocabularies {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, with the postgres hibernate file (change it accordingly to your custom info).
        // Initialization has to be always the first statement of your application and needs to be invoked only once.
        File pgConfigFile = new File(ImportPsiData.class.getResource("/postgres-hibernate.cfg.xml").getFile());
        IntactContext.initStandaloneContext(pgConfigFile);

        // load the latest ontology from internet
        IntactOntology ontology = OboUtils.createOntologyFromOboLatestPsiMi();

        // Import the ontology into the database, using the CvUpdater
        CvUpdater updater = new CvUpdater();

        // this starts the create/update
        CvUpdaterStatistics stats = updater.createOrUpdateCVs(ontology);

        System.out.println("Created terms: "+stats.getCreatedCvs().size());
    }

}