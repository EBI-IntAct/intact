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
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;
import uk.ac.ebi.intact.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Example mojo. This mojo is executed when the goal "mygoal" is called.
 *
 * @goal export
 * @phase process-resources
 */
public class FastaExportMojo extends IntactHibernateMojo {
    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${project.build.directory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    private File hibernateConfig;

    /**
     * Name of the fasta file to be created
     *
     * @parameter default-value="${project.build.directory}/intact.fasta"
     * @required
     */
    private File exportedFile;

    /**
     * Gzip the fasta file before finishing
     *
     * @property default-value="true"
     */
    private boolean gzip;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo() throws MojoExecutionException,
                                           MojoFailureException,
                                           IOException {

        if ( IntactContext.getCurrentInstance().getDataContext().isTransactionActive() ) {
            try {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            } catch ( IntactTransactionException e ) {
                throw new MojoExecutionException( "Problem commiting transaction", e );
            }
        }

        PrintStream ps = new PrintStream( getOutputFile() );

        getLog().info( "Starting export" );
        getLog().info( "   Exporting to file: " + exportedFile );
        try {
            MojoUtils.prepareFile( exportedFile );
            FastaExporter.exportToFastaFile( ps, exportedFile );
        } catch ( IntactTransactionException e ) {
            throw new MojoExecutionException( "Exception exporting to fasta file", e );
        }

        if ( gzip ) {
            File gzippedFile = new File( exportedFile + ".gz" );

            getLog().info( "Gzipping file to: " + gzippedFile );

            Utilities.gzip( exportedFile, gzippedFile, true );
        }
    }

    public MavenProject getProject() {
        return project;
    }

    public File getHibernateConfig() {
        return hibernateConfig;
    }

    public void setHibernateConfig( File hibernateConfig ) {
        this.hibernateConfig = hibernateConfig;
    }

    public File getExportedFile() {
        return exportedFile;
    }

    public void setExportedFile( File exportedFile ) {
        this.exportedFile = exportedFile;
    }

    public boolean isGzip() {
        return gzip;
    }

    public void setGzip( boolean gzip ) {
        this.gzip = gzip;
    }
}