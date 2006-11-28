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
     * @parameter default-value="${project.build.directory}/interactions.xml"
     * @required
     */
    private File interactionFile;

    /**
     * @parameter default-value="${project.build.directory}/interactors.xml"
     * @required
     */
    private File interactorFile;

    /**
     * @parameter default-value="${project.build.directory}/experiments.xml"
     * @required
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

        // prepare output files
        MojoUtils.prepareFile(experimentFile, true, true);
        MojoUtils.prepareFile(interactionFile, true, true);
        MojoUtils.prepareFile(interactorFile, true, true);


        // export experiments
        IndexExporter exporter = new ExperimentIndexExporter( experimentFile, releaseVersion );
        exporter.buildIndex();

        // export interactions
        exporter = new InteractionIndexExporter( interactionFile, releaseVersion );
        exporter.buildIndex();

        // export interactors
        exporter = new InteractorIndexExporter( interactorFile, releaseVersion );
        exporter.buildIndex();
    }
}