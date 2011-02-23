/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins.ebeye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;
import uk.ac.ebi.intact.externalservices.searchengine.*;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.business.IntactTransactionException;

import java.io.File;
import java.io.IOException;

/**
 * Example mojo. This mojo is executed when the goal "ebi-indexes" is called.
 *
 * @goal generate-ebi-indexes
 * @phase process-resources
 */
public class ExportExternalServicesIndexesMojo extends IntactHibernateMojo {

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${project.build.directory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    private File hibernateConfig;

    public MavenProject getProject() {
        return project;
    }

    public File getHibernateConfig() {
        return hibernateConfig;
    }

    /**
     * Output file for interactions. If not specified, no export.
     *
     * @parameter
     */
    private File interactionFile;

    /**
     * Output file for interactors. If not specified, no export.
     *
     * @parameter
     */
    private File interactorFile;

    /**
     * Output file for experiments. If not specified, no export.
     *
     * @parameter
     */
    private File experimentFile;

    /**
     * @parameter default-value="unspecified"
     * @required
     */
    private String releaseVersion;

    /**
     * Main execution method, which is called after hibernate has been initialized
     */
    public void executeIntactMojo() throws MojoExecutionException, MojoFailureException, IOException {

        IndexExporter exporter = null;

        // export experiments
        if ( experimentFile != null ) {
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            MojoUtils.prepareFile( experimentFile, true, true );
            System.out.println( "Start exporting experiments..." );
            exporter = new ExperimentIndexExporter( experimentFile );
            try {
                exporter.buildIndex();
            } catch ( IndexerException e ) {
                throw new MojoExecutionException( "Error while exporting experiments.", e );
            }
            try {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            } catch ( IntactTransactionException e ) {
                throw new MojoExecutionException( "trsaction error, see nested messages", e );
            }
        } else {
            System.out.println( "No export of experiment requested." );
        }


        // export interactions
        if ( interactionFile != null ) {
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            MojoUtils.prepareFile( interactionFile, true, true );
            System.out.println( "Start exporting interactions..." );
            exporter = new InteractionIndexExporter( interactionFile );
            try {
                exporter.buildIndex();
            } catch ( IndexerException e ) {
                throw new MojoExecutionException( "Error while exporting interactions.", e );
            }
            try {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            } catch ( IntactTransactionException e ) {
                throw new MojoExecutionException( "trsaction error, see nested messages", e );
            }
        } else {
            System.out.println( "No export of interaction requested." );
        }

        // export interactors
        if ( interactorFile != null ) {
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            MojoUtils.prepareFile( interactorFile, true, true );
            System.out.println( "Start exporting interactors..." );
            exporter = new InteractorIndexExporter( interactorFile );
            try {
                exporter.buildIndex();
            } catch ( IndexerException e ) {
                throw new MojoExecutionException( "Error while exporting interactors.", e );
            }
            try {
                IntactContext.getCurrentInstance().getDataContext().commitTransaction();
            } catch ( IntactTransactionException e ) {
                throw new MojoExecutionException( "trsaction error, see nested messages", e );
            }
        } else {
            System.out.println( "No export of interactor requested." );
        }
    }
}