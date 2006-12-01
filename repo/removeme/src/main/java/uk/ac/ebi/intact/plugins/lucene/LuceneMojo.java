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
package uk.ac.ebi.intact.plugins.lucene;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.searchengine.lucene.Indexer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Example mojo. This mojo is executed when the goal "mygoal" is called.
 *
 * @goal build-index
 *
 * @phase process-resources
 */
public class LuceneMojo
        extends IntactHibernateMojo
{

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.build.outputDirectory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    private File hibernateConfig;

     /**
     * @property default-value="${maven.build.directory}/lucene-index"
     */
    private File indexFile;
    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        OutputStream logOut = Indexer.index(indexFile);

        super.getOutputWriter().write(logOut.toString());
    }

    public MavenProject getProject()
    {
        return project;
    }

    public File getHibernateConfig()
    {
        return hibernateConfig;
    }

    public File getIndexFile() {
        return indexFile;
    }

    public void setIndexFile(File indexFile) {
        this.indexFile = indexFile;
    }
}
