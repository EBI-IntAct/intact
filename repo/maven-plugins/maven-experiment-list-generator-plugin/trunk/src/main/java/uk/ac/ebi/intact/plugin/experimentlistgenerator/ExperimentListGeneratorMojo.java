/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.experimentlistgenerator;

import org.apache.maven.plugin.MojoExecutionException;
import uk.ac.ebi.intact.application.dataConversion.ExperimentListGenerator;
import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;
import uk.ac.ebi.intact.context.IntactContext;

import java.io.File;

/**
 * Generates list of experiments
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 *
 * @goal generate-list
 * @phase process-resources
 */
public class ExperimentListGeneratorMojo extends ExperimentListGeneratorAbstractMojo
{

    /**
    * File containing the publications
    * @parameter default-value="%"
    */
    protected String searchPattern;

    /**
     * If true, all experiment without a PubMed ID (primary-reference) will be filtered out.
     *
     * @parameter default-value="true"
     */
    protected boolean onlyWithPmid;

    /**
     * Whether to update the existing project files or overwrite them.
     *
     * @parameter expression="${overwrite}" default-value="false"
     */
    protected boolean overwrite;

    /**
     * @parameter default-value="${project.build.outputDirectory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    private File hibernateConfig;
    

    public void execute() throws MojoExecutionException
    {
        getLog().info("ExperimentListGeneratorMojo in action");

        getLog().debug("Using hibernate cfg file: "+hibernateConfig);

        if (!hibernateConfig.exists())
        {
            throw new MojoExecutionException("No hibernate config file found: "+hibernateConfig);
        }

        if (!targetPath.exists())
        {
            targetPath.mkdirs();
        }

        File speciesFile = getSpeciesFile();
        File publicationsFile = getPublicationsFile();

        if (speciesFile.exists() && !overwrite)
        {
            throw new MojoExecutionException("Target species file already exist and overwrite is set to false: "+speciesFile);
        }

        if (publicationsFile.exists() && !overwrite)
        {
            throw new MojoExecutionException("Target publications file already exist and overwrite is set to false: "+speciesFile);
        }

        getLog().debug("Species filename: "+speciesFile);
        getLog().debug("Publications filename: "+publicationsFile);


        // configure the context
        CustomCoreDataConfig testConfig = new CustomCoreDataConfig("ExperimentListGeneratorTest", hibernateConfig);
        testConfig.initialize();
        IntactContext.getCurrentInstance().getConfig().addDataConfig(testConfig, true);

        ExperimentListGenerator gen = new ExperimentListGenerator();
        gen.setSpeciesFile(speciesFile);
        gen.setPublicationsFile(publicationsFile);
        gen.setSearchPattern(searchPattern);
        gen.setOverwrite(overwrite);
        gen.setOnlyWithPmid(onlyWithPmid);
        
        gen.execute();

    }
}