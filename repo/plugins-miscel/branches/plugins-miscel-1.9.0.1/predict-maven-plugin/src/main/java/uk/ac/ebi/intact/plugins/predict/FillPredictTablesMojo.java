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
package uk.ac.ebi.intact.plugins.predict;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.dbutil.predict.FillPredictTables;

import java.io.File;
import java.io.IOException;

/**
 * Fills the Predict tables
 *
 * @goal fill-predict
 *
 * @phase process-resources
 */
public class FillPredictTablesMojo
        extends IntactHibernateMojo
{

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="${project.build.directory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    protected File hibernateConfig;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        try {
            FillPredictTables.runTask(getOutputPrintStream());
        } catch (Exception e) {
            throw new MojoExecutionException("Problem filling predict tables", e);
        }
    }

    /**
     * Implementation of abstract method from superclass
     */
    public MavenProject getProject()
    {
        return project;
    }

    /**
     * Implementation of abstract method from superclass
     */
    public File getHibernateConfig()
    {
        return hibernateConfig;
    }
}
