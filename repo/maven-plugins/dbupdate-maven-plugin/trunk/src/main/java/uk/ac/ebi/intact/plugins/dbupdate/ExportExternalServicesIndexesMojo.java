/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins.dbupdate;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.externalservices.searchengine.ExperimentIndexExporter;
import uk.ac.ebi.intact.externalservices.searchengine.IndexExporter;
import uk.ac.ebi.intact.externalservices.searchengine.InteractionIndexExporter;
import uk.ac.ebi.intact.externalservices.searchengine.InteractorIndexExporter;
import uk.ac.ebi.intact.plugin.MojoUtils;

import java.io.File;
import java.io.IOException;

/**
 * Example mojo. This mojo is executed when the goal "ebi-indexes" is called.
 *
 * @goal generate-ebi-indexes
 * @phase process-resources
 */
public class ExportExternalServicesIndexesMojo extends UpdateAbstractMojo {

    /**
     * Output file for interactions. If not specified, no export.
     * @parameter
     */
    private File interactionFile;

    /**
     * Output file for interactors. If not specified, no export.
     * @parameter
     */
    private File interactorFile;

    /**
     * Output file for experiments. If not specified, no export.
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
            exporter.buildIndex();
        } else {
            System.out.println( "No export of experiment required." );
        }

        // export interactions
        if ( interactionFile != null ) {
            MojoUtils.prepareFile( interactionFile, true, true );
            System.out.println( "Start exporting interactions..." );
            exporter = new InteractionIndexExporter( interactionFile, releaseVersion );
            exporter.buildIndex();
        } else {
            System.out.println( "No export of interaction required." );
        }

        // export interactors
        if ( interactorFile != null ) {
            MojoUtils.prepareFile( interactorFile, true, true );
            System.out.println( "Start exporting interactors..." );
            exporter = new InteractorIndexExporter( interactorFile, releaseVersion );
            exporter.buildIndex();
        } else {
            System.out.println( "No export of interactor required." );
        }
    }
}