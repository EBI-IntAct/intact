/*
 * Copyright 2006 The European Bioinformatics Institute.
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
package uk.ac.ebi.intact.plugin.cv.obo;

import org.apache.log4j.Priority;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.bbop.dataadapter.DataAdapterException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.util.LogUtils;
import uk.ac.ebi.intact.dataexchange.cvutils.CvExporter;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Export an OBO file from the provided database in the hibernateConfig file
 *
 * @goal obo-exp
 * 
 * @phase process-resources
 */
public class OboExportMojo
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
     * @parameter expression="${project.build.directory}/intact-exported.obo"
     * @required
     */
    private File exportedOboFile;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        if (isDryRun())
        {
            getLog().info("Running in dry-run mode");
        }

        MojoUtils.prepareFile(exportedOboFile, true);

        BufferedWriter out = new BufferedWriter(new FileWriter(exportedOboFile));

        CvExporter exporter = new CvExporter();
        final List<CvObject> allCvs = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao().getAll();
        try {
            exporter.exportToFile(allCvs, exportedOboFile);
        } catch (DataAdapterException e) {
            throw new MojoExecutionException("Problem exporting file: "+exportedOboFile, e);
        }

        getLog().info("Closed " + exportedOboFile);
    }

    public MavenProject getProject()
    {
        return project;
    }

    public File getHibernateConfig()
    {
        return hibernateConfig;
    }

    public File getExportedOboFile()
    {
        return exportedOboFile;
    }

    @Override
    protected Priority getLogPriority()
    {
        LogUtils.setPrintSql(false);
        return Priority.DEBUG;
    }
}
