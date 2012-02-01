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

import org.obo.datamodel.OBOSession;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.dataexchange.cvutils.CvUpdater;
import uk.ac.ebi.intact.dataexchange.cvutils.CvUpdaterStatistics;
import uk.ac.ebi.intact.dataexchange.cvutils.OboUtils;

/**
 * Example of how to import or update the controlled vocabularies in the database.
 *
 * @version $Id$
 */
public class ImportControlledVocabularies {

    public static void main(String[] args) throws Exception {

        // Initialize the IntactContext, using the default configuration found in the file hsql.spring.xml..
        IntactContext.initContext(new String[] {"/META-INF/kickstart.spring.xml"});

        // Once an IntactContext has been initialized, we can access to it by getting the current instance
        IntactContext intactContext = IntactContext.getCurrentInstance();

        // Import the ontology into the database, using the CvUpdater
        CvUpdater updater = CvUpdater.createInstance(intactContext);

        // define versions of OBO
        OBOSession oboSession = OboUtils.createOBOSessionFromDefault("1.54");

        // this starts the create/update
        CvUpdaterStatistics stats = updater.executeUpdate(oboSession);

        System.out.println("Created terms: "+stats.getCreatedCvs().size());
    }

}