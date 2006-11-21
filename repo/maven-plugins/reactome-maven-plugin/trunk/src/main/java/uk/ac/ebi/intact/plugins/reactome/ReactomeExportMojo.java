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
package uk.ac.ebi.intact.plugins.reactome;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;
import uk.ac.ebi.intact.dbutil.reactome.ReactomeBean;
import uk.ac.ebi.intact.dbutil.reactome.ReactomeExport;
import uk.ac.ebi.intact.dbutil.reactome.ReactomeValidationReport;
import uk.ac.ebi.intact.dbutil.reactome.ReactomeException;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.Collection;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * Example mojo. This mojo is executed when the goal "mygoal" is called.
 *
 * @goal reactome-exp
 *
 * @phase process-resources
 */
public class ReactomeExportMojo
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
     * @parameter default-value="${project.build.directory}/reactome.dat"
     * @required
     */
    private File reactomeExportedFile;

    /**
     * @parameter
     */
    private URL reactomeUrl;

    /**
     * @parameter default-value="${project.build.directory}/reactome-invalid_intact_in_reactome.txt"
     */
    private File invalidIntactAcsInReactomeFile;

    /**
     * @parameter default-value="${project.build.directory}/reactome-invalid_reactome_in_intact.txt"
     */
    private File invalidReactomeIdsInIntactFile;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {

        MojoUtils.prepareFile(reactomeExportedFile, true);

        if (reactomeUrl == null)
        {
            getLog().info("Validation is disabled because the 'reactomeUrl' property was not supplied");
        }

        try
        {
            List<ReactomeBean> reactomeBeans = ReactomeExport.createReactomXrefsFromIntactList();
            ReactomeExport.exportToReactomeFile(reactomeBeans, reactomeExportedFile);

            // validate the exported file if a url is supplied
            if (reactomeUrl != null)
            {
               ReactomeValidationReport report = ReactomeExport.areXrefsFromIntactValid(reactomeBeans,reactomeUrl);

                if (report.isValid())
                {
                    getLog().info("Reactome is valid.");
                }
                else
                {
                    getLog().error("Reactome is invalid!");
                }

                // reactome IDs used in intcat, but not exist in reactome anymore
                Collection<String> reactomeIds = report.getNonExistingReactomeIdsInIntact();

                if (!reactomeIds.isEmpty())
                {
                    MojoUtils.prepareFile(invalidReactomeIdsInIntactFile, true);
                    MojoUtils.writeStandardHeaderToFile("Invalid Reactome IDs in IntAct",
                                                        "Reactome ID that are used in IntAct but not existing in Reactome anymore",
                                                        getProject(), invalidReactomeIdsInIntactFile);

                    FileWriter writer = new FileWriter(invalidReactomeIdsInIntactFile);
                    for (String id : reactomeIds)
                    {
                        writer.write(id+NEW_LINE);
                    }
                    writer.close();
                }

                // intact ACs used in reactome, but not exist in intact anymore
                Collection<String> intactAcs = report.getNonExistingIntactAcsInReactome();

                if (!intactAcs.isEmpty())
                {
                    MojoUtils.prepareFile(invalidIntactAcsInReactomeFile, true);
                    MojoUtils.writeStandardHeaderToFile("Invalid IntAct ACs in Reactome",
                                                        "Intact Interaction AC that are used in Reactome but not existing in IntAct anymore",
                                                        getProject(), invalidIntactAcsInReactomeFile);

                    FileWriter writer = new FileWriter(invalidIntactAcsInReactomeFile);
                    for (String ac : intactAcs)
                    {
                        writer.write(ac+NEW_LINE);
                    }
                    writer.close();
                }
            }
        }
        catch (ReactomeException e)
        {
            throw new MojoExecutionException("Problem creating reactome file", e);
        }

    }

    public MavenProject getProject()
    {
        return project;
    }
}
