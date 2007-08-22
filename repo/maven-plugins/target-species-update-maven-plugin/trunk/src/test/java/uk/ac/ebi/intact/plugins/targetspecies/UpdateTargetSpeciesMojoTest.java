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
package uk.ac.ebi.intact.plugins.targetspecies;

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

public class UpdateTargetSpeciesMojoTest extends AbstractMojoTestCase  {

    @Test
    public void testSimpleGeneration() throws Exception {
        File hibernateConfig = new File(UpdateTargetSpeciesMojoTest.class.getResource("/test-hibernate.cfg.xml").getFile());

        IntactSession session = new StandaloneSession();
        CustomCoreDataConfig dataConfig = new CustomCoreDataConfig("custom", hibernateConfig, session);
        IntactContext.initContext(dataConfig, session);

        IntactUnit iu = new IntactUnit();
        iu.createSchema(true);

        IntactMockBuilder mockBuilder = new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution());

        CvDatabase newt = mockBuilder.createCvObject(CvDatabase.class, CvDatabase.NEWT_MI_REF, CvDatabase.NEWT);
        CvXrefQualifier targetSpeciesQual = mockBuilder.createCvObject(CvXrefQualifier.class, "UNK:0001", CvXrefQualifier.TARGET_SPECIES);

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        CvObjectPersister.getInstance().saveOrUpdate(newt);
        CvObjectPersister.getInstance().saveOrUpdate(targetSpeciesQual);
        CvObjectPersister.getInstance().commit();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        ExperimentPersister.getInstance().saveOrUpdate(mockBuilder.createExperimentRandom(3));
        ExperimentPersister.getInstance().commit();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/target-species-config.xml" );

        UpdateTargetSpeciesMojo mojo = (UpdateTargetSpeciesMojo) lookupMojo( "target-species", pluginXmlFile );
        mojo.setHibernateConfig(hibernateConfig);
        mojo.executeIntactMojo();
    }
}

