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
            MojoUtils.prepareFile( experimentFile, true, true );
            System.out.println( "Start exporting experiments..." );
            exporter = new ExperimentIndexExporter( experimentFile, releaseVersion );
            try {
                exporter.buildIndex();
            } catch ( IndexerException e ) {
                throw new MojoExecutionException( "Error while exporting experiments.", e );
            }
        } else {
            System.out.println( "No export of experiment required." );
        }

        // export interactions
        if ( interactionFile != null ) {
            MojoUtils.prepareFile( interactionFile, true, true );
            System.out.println( "Start exporting interactions..." );
            exporter = new InteractionIndexExporter( interactionFile, releaseVersion );
            try {
                exporter.buildIndex();
            } catch ( IndexerException e ) {
                throw new MojoExecutionException( "Error while exporting interactions.", e );
            }
        } else {
            System.out.println( "No export of interaction required." );
        }

        // export interactors
        if ( interactorFile != null ) {
            MojoUtils.prepareFile( interactorFile, true, true );
            System.out.println( "Start exporting interactors..." );
            exporter = new InteractorIndexExporter( interactorFile, releaseVersion );
            try {
                exporter.buildIndex();
            } catch ( IndexerException e ) {
                throw new MojoExecutionException( "Error while exporting interactors.", e );
            }
        } else {
            System.out.println( "No export of interactor required." );
        }
    }
}