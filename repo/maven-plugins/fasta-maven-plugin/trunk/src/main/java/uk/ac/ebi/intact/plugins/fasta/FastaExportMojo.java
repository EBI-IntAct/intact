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
package uk.ac.ebi.intact.plugins.fasta;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.dbutil.fasta.FastaExporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Example mojo. This mojo is executed when the goal "mygoal" is called.
 *
 * @goal export
 *
 * @phase process-resources
 */
public class FastaExportMojo
        extends IntactHibernateMojo
{

    /**
     * @property default-value="${maven.build.directory}/intact.fasta"
     */
    private File exportedFile;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        PrintStream ps = new PrintStream(getOutputFile());

        FastaExporter.exportToFastaFile(ps, exportedFile);
    }

    public File getExportedFile()
    {
        return exportedFile;
    }

    public void setExportedFile(File exportedFile)
    {
        this.exportedFile = exportedFile;
    }
}
