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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.dbutil.update.UpdateTargetSpecies;
import uk.ac.ebi.intact.dbutil.update.UpdateTargetSpeciesReport;
import uk.ac.ebi.intact.plugin.MojoUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Example mojo. This mojo is executed when the goal "target-species" is called.
 *
 * @goal target-species
 *
 * @phase process-resources
 */
public class UpdateTargetSpeciesMojo
        extends UpdateAbstractMojo
{

    /**
     * @parameter default-value="${project.build.directory}/target-species.stat"
     * @required
     */
    private File statsFile;

    /**
     * Only experiments with this label-like pattern will be included (in SQL format,
     * e.g. if the labelPattern go% is used, only those experiments starting with 'go' will be
     * selected to update)
     *
     * @parameter
     */
    private String labelPattern;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        MojoUtils.prepareFile(statsFile, true);

        PrintStream ps = new PrintStream(statsFile);

        UpdateTargetSpeciesReport report = UpdateTargetSpecies.update(ps, isDryRun(), labelPattern);

    }

    public File getStatsFile()
    {
        return statsFile;
    }

    public void setStatsFile(File statsFile)
    {
        this.statsFile = statsFile;
    }

    public String getLabelPattern()
    {
        return labelPattern;
    }

    public void setLabelPattern(String labelPattern)
    {
        this.labelPattern = labelPattern;
    }
}
