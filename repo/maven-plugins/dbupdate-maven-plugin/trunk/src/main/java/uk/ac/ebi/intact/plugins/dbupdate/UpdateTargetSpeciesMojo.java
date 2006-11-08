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
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;
import uk.ac.ebi.intact.dbutil.update.UpdateTargetSpecies;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Example mojo. This mojo is executed when the goal "mygoal" is called.
 *
 * @goal target-species
 *
 * @phase process-resources
 */
public class UpdateTargetSpeciesMojo
        extends IntactHibernateMojo
{

    /**
     * @parameter default-value="${project.build.directory}/target-species.stat"
     * @required
     */
    private File statsFile;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        MojoUtils.prepareFile(statsFile, true);

        List<String> stats = UpdateTargetSpecies.update();

        // write stats in a file.
        BufferedWriter out = new BufferedWriter(new FileWriter(statsFile));
        for (String line : stats)
        {
            out.write(line);
            out.write(NEW_LINE);
        }

        out.close();

        getLog().info("Closed " + statsFile);
    }
}
