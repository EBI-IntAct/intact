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
package uk.ac.ebi.intact.plugin;

import org.apache.maven.plugin.MojoExecutionException;

import java.sql.SQLException;
import java.io.File;

import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.context.IntactEnvironment;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactHibernateMojo extends IntactAbstractMojo
{
    /**
     * @parameter default-value="target/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    protected File hibernateConfig;

    private boolean initialized;

    protected void initializeHibernate() throws MojoExecutionException
    {
        if (initialized)
        {
            return;
        }

        getLog().info("Using hibernate cfg file: "+hibernateConfig);

        if (!hibernateConfig.exists())
        {
            throw new MojoExecutionException("No hibernate config file found: "+hibernateConfig);
        }

        // configure the context
        IntactSession session = new StandaloneSession();

        if (System.getProperty("institution") == null)
        {
            session.setInitParam(IntactEnvironment.INSTITUTION_LABEL, "ebi");
        }

        CustomCoreDataConfig testConfig = new CustomCoreDataConfig("PsiXmlGeneratorTest", hibernateConfig, session);
        testConfig.initialize();
        IntactContext.initContext(testConfig, session);

        try
        {
            getLog().info( "Database instance: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbName() );
        }
        catch (SQLException e)
        {
            throw new MojoExecutionException("Error loading database name", e);
        }

        getLog().info( "User: " + IntactContext.getCurrentInstance().getUserContext().getUserId() );

        initialized = true;
    }
}
