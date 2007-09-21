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
package uk.ac.ebi.intact.plugins.dbupdate;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;

import java.io.File;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class UpdateAbstractMojoTestCase extends AbstractMojoTestCase {

    private IntactMockBuilder mockBuilder;
    private File hibernateConfigFile;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        hibernateConfigFile = new File(UpdateTargetSpeciesMojoTest.class.getResource("/test-hibernate.cfg.xml").getFile());

        IntactSession session = new StandaloneSession();
        CustomCoreDataConfig dataConfig = new CustomCoreDataConfig("custom", hibernateConfigFile, session);
        IntactContext.initContext(dataConfig, session);

        mockBuilder = new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution());
    }

    @After
    public void close() throws Exception {
        IntactContext.getCurrentInstance().close();
    }

    protected IntactMockBuilder getMockBuilder() {
        return mockBuilder;
    }

    protected File getHibernateConfigFile() {
        return hibernateConfigFile;
    }
}