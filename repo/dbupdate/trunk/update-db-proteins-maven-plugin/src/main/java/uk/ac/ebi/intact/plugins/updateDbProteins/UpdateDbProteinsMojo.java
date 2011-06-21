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
package uk.ac.ebi.intact.plugins.updateDbProteins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.core.util.DebugUtil;
import uk.ac.ebi.intact.dbupdate.prot.ProteinUpdateContext;
import uk.ac.ebi.intact.dbupdate.prot.ProteinUpdateProcessor;
import uk.ac.ebi.intact.dbupdate.prot.ProteinUpdateProcessorConfig;
import uk.ac.ebi.intact.dbupdate.prot.report.FileReportHandler;
import uk.ac.ebi.intact.plugin.IntactJpaMojo;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Mojo that execute a global protein update on a given intact database.
 *
 * @goal update-proteins
 *
 * @phase process-resources
 */
public class UpdateDbProteinsMojo extends IntactJpaMojo {

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

     /**
     * A spring config file (JPA, ...).
     * @parameter
     * @required
     */
    private String springConfig;

    /**
     * @parameter expression="${project.build.directory}/protein-update"
     * @required
     */
    private File reportsDir;

    /**
     * @parameter
     */
    private boolean fixDuplicates = true;

    /**
     * @parameter
     */
    private boolean deleteSpliceVarsWithoutInteractions = true;

    /**
     * @parameter
     */
    public int batchSize = 100;

    /**
     * @parameter
     */
    public int stepSize = 50;

    public UpdateDbProteinsMojo() {
        super();
    }

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException {

        System.out.println( "Protein Update in Action..." );

        System.out.println( "springConfig:"+getSpringConfig() );
        System.out.println( "fixDuplicates:"+fixDuplicates );
        System.out.println( "deleteSpliceVarsWithoutInteractions:"+deleteSpliceVarsWithoutInteractions );
        System.out.println( "batchSize:"+getBatchSize() );
        System.out.println( "stepSize:"+stepSize );

        //configUpdate.setProcessBatchSize(batchSize);
        //configUpdate.setProcessStepSize(stepSize);
        ProteinUpdateProcessorConfig config = ProteinUpdateContext.getInstance().getConfig();
        config.setDeleteProteinTranscriptWithoutInteractions(deleteSpliceVarsWithoutInteractions);
        config.setDeleteProtsWithoutInteractions(true);
        config.setGlobalProteinUpdate(true);
        config.setFixDuplicates(fixDuplicates);
        config.setProcessProteinNotFoundInUniprot(true);
        config.setBlastEnabled(false);

        PrintStream ps = new PrintStream(new File(reportsDir, "counts.txt"));
        ps.println("Counts before update");
        ps.println("--------------------");
        DebugUtil.printDatabaseCounts(ps);
        ps.flush();

        try {
            config.setReportHandler(new FileReportHandler(reportsDir));

            ProteinUpdateProcessor updateProcessor = new ProteinUpdateProcessor();
            System.out.println("Starting the global update");
            updateProcessor.updateAll();
            //List<Protein> proteins = updateProcessor.retrieveAndUpdateProteinFromUniprot("Q9XYZ4");
            //List<String> acs = new ArrayList<String>();
            //acs.add("EBI-3044019");
            //updateProcessor.updateByACs(acs);

        } catch (IOException e) {
            System.err.println("The repository " + reportsDir.getName() + " cannot be found. We cannot write log files and so we cannot run a global protein update.");
            e.printStackTrace();
        }

        ps.println("\nCounts after update");
        ps.println("---------------------");
        DebugUtil.printDatabaseCounts(ps);
        ps.flush();
    }

    public String getSpringConfig() {
        return springConfig;
    }

    public void setSpringConfig( String springConfig ) {
        this.springConfig = springConfig;
    }

    public MavenProject getProject() {
        return project;
    }

    public File getReportsDir() {
        return reportsDir;
    }

    public void setReportsDir(File reportsDir) {
        this.reportsDir = reportsDir;
    }

    public boolean isFixDuplicates() {
        return fixDuplicates;
    }

    public void setFixDuplicates(boolean fixDuplicates) {
        this.fixDuplicates = fixDuplicates;
    }

    public boolean isDeleteSpliceVarsWithoutInteractions() {
        return deleteSpliceVarsWithoutInteractions;
    }

    public void setDeleteSpliceVarsWithoutInteractions(boolean deleteSpliceVarsWithoutInteractions) {
        this.deleteSpliceVarsWithoutInteractions = deleteSpliceVarsWithoutInteractions;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}