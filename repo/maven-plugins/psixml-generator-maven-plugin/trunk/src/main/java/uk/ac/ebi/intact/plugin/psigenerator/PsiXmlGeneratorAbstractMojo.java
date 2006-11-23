/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.application.dataConversion.ExperimentListGenerator;
import uk.ac.ebi.intact.application.dataConversion.ExperimentListItem;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.plugin.MojoUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:PsiXmlGeneratorAbstractMojo.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @since <pre>04/08/2006</pre>
 */
public abstract class PsiXmlGeneratorAbstractMojo extends IntactHibernateMojo
{

    protected static final String NEW_LINE = System.getProperty( "line.separator" );

    /**
     * Project instance
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * File containing the species
     *
     * @parameter default-value="target/psixml"
     * @required
     */
    protected File targetPath;

    /**
     * File containing the species
     *
     * @parameter default-value="classification_by_species.txt"
     */
    protected String speciesFilename;

    /**
     * File containing the publications
     *
     * @parameter default-value="classification_by_publications.txt"
     */
    protected String publicationsFilename;

    /**
     * File containing the datasets
     *
     * @parameter default-value="classification_by_datasets.txt"
     */
    protected String datasetsFilename;

    /**
     * File containing the errors
     *
     * @parameter default-value="target/psixml/experiment-error.log"
     */
    protected File experimentErrorFile;

    /**
     * File containing the negative experiments
     *
     * @parameter default-value="target/psixml/negative-experiments.log"
     */
    protected File negativeExperimentsFile;

    /**
     * File containing the publications
     *
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
     * Create zip files, clustering the XMLs
     *
     * @parameter expression="${zipXml}" default-value="true"
     */
    protected boolean zipXml;

    /**
     * @parameter default-value="${project.build.outputDirectory}/hibernate/config/hibernate.cfg.xml"
     * @required
     */
    protected File hibernateConfig;

    private boolean initialized;
    private boolean reportsWritten;
    protected ExperimentListGenerator experimentListGenerator;

    public PsiXmlGeneratorAbstractMojo() {
    }

    protected File getSpeciesFile() {
        return new File( targetPath, speciesFilename );
    }

    protected File getPublicationsFile() {
        return new File( targetPath, publicationsFilename );
    }

    protected File getDatasetsFile() {
        return new File( targetPath, datasetsFilename );
    }

    protected void initialize() throws MojoExecutionException {
        if ( initialized ) {
            return;
        }

        if ( !targetPath.exists() ) {
            targetPath.mkdirs();
        }

        File speciesFile = getSpeciesFile();
        File publicationsFile = getPublicationsFile();
        File datasetsFile = getDatasetsFile();

        if ( speciesFile.exists() && !overwrite ) {
            throw new MojoExecutionException( "Target species file already exist and overwrite is set to false: " + speciesFile );
        }

        if ( publicationsFile.exists() && !overwrite ) {
            throw new MojoExecutionException( "Target publications file already exist and overwrite is set to false: " + publicationsFile );
        }

        if ( datasetsFile.exists() && !overwrite ) {
            throw new MojoExecutionException( "Target datasets file already exist and overwrite is set to false: " + datasetsFile );
        }

        experimentListGenerator = new ExperimentListGenerator( searchPattern );
        experimentListGenerator.setOnlyWithPmid( onlyWithPmid );
        initialized = true;
    }

    protected Collection<ExperimentListItem> generateSpeciesListItems() throws MojoExecutionException {
        initialize();
        return experimentListGenerator.generateClassificationBySpecies();
    }

    protected Collection<ExperimentListItem> generatePublicationsListItems() throws MojoExecutionException {
        initialize();
        return experimentListGenerator.generateClassificationByPublications();
    }

    protected Collection<ExperimentListItem> generateDatasetsListItems() throws MojoExecutionException {
        initialize();
        return experimentListGenerator.generateClassificationByDatasets();
    }

    protected Collection<ExperimentListItem> generateAllClassifications() throws MojoExecutionException {
        initialize();
        return experimentListGenerator.generateAllClassifications();
    }

    protected void writeClassificationBySpeciesToFile() throws MojoExecutionException {
        getLog().debug( "Species filename: " + getSpeciesFile() );

        try {
            writeItems( getSpeciesFile(), generateSpeciesListItems() );
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Problem creating the species file", e );
        }

        writeReports();
    }

    protected void writeClassificationByPublicationsToFile() throws MojoExecutionException {
        getLog().debug( "Publications filename: " + getPublicationsFile() );

        try {
            writeItems( getPublicationsFile(), generatePublicationsListItems() );
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Problem creating the publications file", e );
        }

        writeReports();
    }

    protected void writeClassificationByDatasetToFile() throws MojoExecutionException {
        getLog().debug( "Datasets filename: " + getDatasetsFile() );

        try {
            writeItems( getDatasetsFile(), generateDatasetsListItems() );
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Problem creating the datasets file", e );
        }

        writeReports();
    }

    private void writeReports() throws MojoExecutionException {
        if ( reportsWritten ) {
            return;
        }
        initialize();
        writeNegativeExperiments();
        writeErrorFile();

        reportsWritten = true;
    }

    private static void writeItems( File itemsFile, Collection<ExperimentListItem> items ) throws IOException {
        Writer writer = new FileWriter( itemsFile );

        for ( ExperimentListItem item : items ) {
            writer.write( item.toString() + NEW_LINE );
        }

        writer.close();
    }

    private void writeNegativeExperiments() throws MojoExecutionException {
        Collection<Experiment> negativeExperiments = experimentListGenerator.getNegativeExperiments();
        getLog().info( "Negative experiments: " + negativeExperiments.size() );

        try {
            MojoUtils.writeStandardHeaderToFile("Negative experiments", "Processed experiments declared as negative", getProject(), negativeExperimentsFile);

            Writer writer = new FileWriter( negativeExperimentsFile, true );

            if ( negativeExperiments.isEmpty() ) {
                writer.write( "# No negative experiments found! " + NEW_LINE );
            }

            for ( Experiment negExp : negativeExperiments ) {
                writer.write( negExp.getAc() + "\t" + negExp.getShortLabel() + NEW_LINE );
            }

            writer.close();
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Problem writing negative experiments file", e );
        }
    }

    private void writeErrorFile() throws MojoExecutionException {
        Map<String, String> experimentsWithErrors = experimentListGenerator.getExperimentWithErrors();
        getLog().info( "Experiments with errors: " + experimentsWithErrors.size() );


        try {
            MojoUtils.writeStandardHeaderToFile("Experiments with errors", "Errors have occurred while processing these experiments", getProject(), experimentErrorFile);

            Writer writer = new FileWriter( experimentErrorFile, true );
            if ( experimentsWithErrors.isEmpty() ) {
                writer.write( "# No errors found! " + NEW_LINE );
            }

            for ( Map.Entry<String, String> error : experimentsWithErrors.entrySet() ) {
                writer.write( error.getKey() + " " + error.getValue() + NEW_LINE );
            }

            writer.close();
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Problem writing error file", e );
        }
    }

    public MavenProject getProject()
    {
        return project;
    }

    public File getTargetPath()
    {
        return targetPath;
    }

    public String getSpeciesFilename()
    {
        return speciesFilename;
    }

    public String getPublicationsFilename()
    {
        return publicationsFilename;
    }

    public String getDatasetsFilename()
    {
        return datasetsFilename;
    }

    public File getExperimentErrorFile()
    {
        return experimentErrorFile;
    }

    public File getNegativeExperimentsFile()
    {
        return negativeExperimentsFile;
    }

    public String getSearchPattern()
    {
        return searchPattern;
    }

    public boolean isOnlyWithPmid()
    {
        return onlyWithPmid;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public boolean isZipXml()
    {
        return zipXml;
    }

    public File getHibernateConfig()
    {
        return hibernateConfig;
    }

    public ExperimentListGenerator getExperimentListGenerator()
    {
        return experimentListGenerator;
    }
}