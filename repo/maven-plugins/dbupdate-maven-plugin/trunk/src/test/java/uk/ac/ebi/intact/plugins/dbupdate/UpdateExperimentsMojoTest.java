/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.ac.ebi.intact.plugins.dbupdate;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.standard.ExperimentPersister;
import uk.ac.ebi.intact.core.persister.PersisterHelper;

import java.io.File;

public class UpdateExperimentsMojoTest extends UpdateAbstractMojoTestCase
{

    @Test
    public void testUpdate() throws Exception {
        PersisterHelper.saveOrUpdate(getMockBuilder().createExperimentRandom(3));

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/experiments-config.xml" );

        UpdateExperimentsMojo mojo = (UpdateExperimentsMojo) lookupMojo( "experiments", pluginXmlFile );
        mojo.setHibernateConfig(getHibernateConfigFile());
        mojo.setLog( new SystemStreamLog() );
        mojo.setDryRun(false);

        mojo.execute();
    }
}
