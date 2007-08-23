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

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;
import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.core.persister.standard.CvObjectPersister;
import uk.ac.ebi.intact.core.persister.standard.ExperimentPersister;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;

import java.io.File;

public class UpdateTargetSpeciesMojoTest extends UpdateAbstractMojoTestCase  {

    @Test
    public void testSimpleGeneration() throws Exception {
        CvDatabase newt = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.NEWT_MI_REF, CvDatabase.NEWT);
        CvXrefQualifier targetSpeciesQual = getMockBuilder().createCvObject(CvXrefQualifier.class, "UNK:0001", CvXrefQualifier.TARGET_SPECIES);

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        CvObjectPersister.getInstance().saveOrUpdate(newt);
        CvObjectPersister.getInstance().saveOrUpdate(targetSpeciesQual);
        CvObjectPersister.getInstance().commit();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ExperimentPersister.getInstance().saveOrUpdate(getMockBuilder().createExperimentRandom(3));
        ExperimentPersister.getInstance().commit();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/target-species-config.xml" );

        UpdateTargetSpeciesMojo mojo = (UpdateTargetSpeciesMojo) lookupMojo( "target-species", pluginXmlFile );
        mojo.setHibernateConfig(getHibernateConfigFile());
        mojo.executeIntactMojo();
    }
}

