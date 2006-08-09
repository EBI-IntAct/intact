/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.experimentlistgenerator;

import org.apache.maven.plugin.MojoExecutionException;
import uk.ac.ebi.intact.application.dataConversion.ExperimentListGenerator;
import uk.ac.ebi.intact.application.dataConversion.ExperimentListItem;
import uk.ac.ebi.intact.config.impl.CustomCoreDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final String NEW_LINE = System.getProperty("line.separator");

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
        gen.setOnlyWithPmid(onlyWithPmid);

        getLog().info("Starting to classify by species and publications");

        List<ExperimentListItem> eliSpecies = gen.generateClassificationBySpecies();
        getLog().info("Files by species: "+eliSpecies.size());

        try
        {
            writeItems(speciesFile, eliSpecies);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Problem creating the species file", e);
        }

        List<ExperimentListItem> eliPublications = gen.generateClassificationByPublications();
        getLog().info("Files by publications: "+eliPublications.size());

        try
        {
            writeItems(publicationsFile, eliPublications);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Problem creating the publications file", e);
        }

        Set<Experiment> negativeExps = gen.getNegativeExperiments();
        getLog().info("Negative experiments: "+negativeExps.size());

        Map<String,String> experimentsWithErrors = gen.getExperimentWithErrors();
        getLog().info("Experiments with errors: "+experimentsWithErrors.size());

        try
        {
            writeErrorFile(experimentErrorFile, experimentsWithErrors);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Problem creating the experiment with errors file", e);
        }

    }

    private static void writeItems(File itemsFile, List<ExperimentListItem> items) throws IOException
    {
        Writer writer = new FileWriter(itemsFile);

        for (ExperimentListItem item : items)
        {
            writer.write(item.getFilename()+" "+item.getPattern()+NEW_LINE);
        }

        writer.close();
    }

    private static void writeErrorFile(File errorFile, Map<String,String> experimentsWithErrors) throws IOException
    {
        Writer writer = new FileWriter(errorFile);

        for (Map.Entry<String,String> error : experimentsWithErrors.entrySet())
        {
            writer.write(error.getKey()+" "+error.getValue()+NEW_LINE);
        }

        writer.close();
    }
}