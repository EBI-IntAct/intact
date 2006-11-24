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
package uk.ac.ebi.intact.plugin.cv.obo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.dbutil.cv.UpdateCVs;
import uk.ac.ebi.intact.dbutil.cv.PsiLoaderException;

/**
 * Export an OBO file from the provided database in the hibernateConfig file
 *
 * @goal obo-imp
 *
 * @phase process-resources
 */
public class OboImportMojo
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
     * @parameter default-value="${project.build.outputDirectory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    private File hibernateConfig;

    /**
     * @parameter expression="intact.obo"
     * @required
     */
    private File importedOboFile;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        if (!importedOboFile.exists())
        {
            throw new MojoExecutionException("OBO file to import does not exist: "+importedOboFile);
        }

        try
        {
            UpdateCVs.load(importedOboFile);
        }
        catch (PsiLoaderException e)
        {
            throw new MojoExecutionException("Problem importing OBO file", e);
        }

    }


    public File getImportedOboFile()
    {
        return importedOboFile;
    }

    public void setImportedOboFile(File importedOboFile)
    {
        this.importedOboFile = importedOboFile;
    }

    public MavenProject getProject()
    {
        return project;
    }

    public File getHibernateConfig()
    {
        return hibernateConfig;
    }
}
