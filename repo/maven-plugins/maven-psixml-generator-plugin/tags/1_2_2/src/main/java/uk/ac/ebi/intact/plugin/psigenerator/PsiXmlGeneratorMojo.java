/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.psigenerator;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.application.dataConversion.*;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.CvMapping;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.util.Chrono;
import uk.ac.ebi.intact.util.MemoryMonitor;

import java.io.*;
import java.util.Collection;
import java.util.List;

/**
 * Creates PSI XML files from the database
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id:PsiXmlGeneratorMojo.java 5772 2006-08-11 16:08:37 +0100 (Fri, 11 Aug 2006) baranda $
 * @goal psi
 * @phase process-resources
 * @since <pre>04/08/2006</pre>
 */
public class PsiXmlGeneratorMojo extends PsiXmlGeneratorAbstractMojo {

    /**
     * File containing the Controlled Vocabularies reverse mapping
     *
     * @parameter default-value="reverseMapping.txt"
     */
    protected File reverseMappingFile;

    /**
     * Name for the invalid files. The version will be put as a suffix
     *
     * @parameter default-value="invalidXml"
     */
    protected String invalidFilePrefix;

    /**
     * Psi Versions.
     *
     * @parameter
     * @required
     */
    protected List<Version> psiVersions;

    /**
     * Classification to process.
     *
     * @parameter default-value="true"
     */
    protected List<Classification> classifications;

    private boolean classificationEnabled( String name ) {

        if ( name == null ) {
            throw new IllegalArgumentException();
        }

        name = name.trim();

        for ( Classification classification : classifications ) {
            if ( name.equalsIgnoreCase( classification.getName() ) ) {
                return classification.enabled;
            }
        }

        // not defined by user.
        return true; // default behaviour is to run the classification
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info( "PsiXmlGeneratorMojo in action" );

        boolean speciesEnabled = classificationEnabled( "species" );
        getLog().debug( "Species classification requested: " + speciesEnabled );

        boolean publicationsEnabled = classificationEnabled( "publications" );
        getLog().debug( "Publications classification requested: " + publicationsEnabled );

        boolean datasetsEnabled = classificationEnabled( "datasets" );
        getLog().debug( "Datasets classification requested: " + datasetsEnabled );

        getLog().debug( "searchPattern: " + searchPattern );


        if ( ! speciesEnabled && ! publicationsEnabled && ! datasetsEnabled ) {
            throw new MojoExecutionException( "User requested not to produce any classification. One should be at least enabled. abort." );
        }

        new MemoryMonitor();

        getLog().debug( "Reverse mapping file: " + getReverseMapping() );

        if ( speciesEnabled ) {
            if ( !getSpeciesFile().exists() ) {
                getLog().info( "Classifying and writing classification by species" );
                writeClassificationBySpeciesToFile();
                IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
            } else {
                getLog().info( "Using existing classification by species: " + getSpeciesFile() );
            }
        } else {
            getLog().info( "Skip species classification at user request." );
        }

        if ( publicationsEnabled ) {
            if ( !getPublicationsFile().exists() ) {
                getLog().info( "Writing classifications by publications" );
                writeClassificationByPublicationsToFile();
                IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
            } else {
                getLog().info( "Using existing classification by publications: " + getPublicationsFile() );
            }
        } else {
            getLog().info( "Skip publication classification at user request." );
        }

        if ( datasetsEnabled ) {
            if ( !getDatasetsFile().exists() ) {
                getLog().info( "Writing classifications by datasets" );
                writeClassificationByDatasetToFile();
                IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
            } else {
                getLog().info( "Using existing classification by datasets: " + getDatasetsFile() );
            }
        } else {
            getLog().info( "Skip dataset classification at user request." );
        }

        CvMapping mapping = null;
        if ( hasPsi1( psiVersions ) ) {
            // only load CV mapping if we do want to generate PSI 1.0
            mapping = new CvMapping();
            mapping.loadFile( getReverseMapping() );
        }

        if ( getSpeciesFile().exists() && getPublicationsFile().exists() && getDatasetsFile().exists() ) {
            Collection<ExperimentListItem> items = generateAllClassifications();
            getLog().info( "Going to generate " + items.size() + " PSI-MI xml files for each of this versions: " + psiVersions );

            items.clear();
            items = null;

            IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
        }

        try {
            if ( speciesEnabled ) {
                getLog().info( "Exporting XML files classified by species" );
                readClassificationAndWritePsiXmls( getSpeciesFile(), mapping );
            }

            if ( publicationsEnabled ) {
                getLog().info( "Exporting XML files classified by publications" );
                readClassificationAndWritePsiXmls( getPublicationsFile(), mapping );
            }

            if ( datasetsEnabled ) {
                getLog().info( "Exporting XML files classified by datasets" );
                readClassificationAndWritePsiXmls( getDatasetsFile(), mapping );
            }
        }
        catch ( IOException e ) {
            throw new MojoExecutionException( "Error creating Xml files: ", e );
        }

        if ( zipXml ) {
            getLog().info( "Clustering and zipping files recursively from folder: " + targetPath );
            ZipFileGenerator.clusterAllXmlFilesFromDirectory( targetPath, true );
        }
    }

    /**
     * Checks if any of the version is PSI MI 1.0.
     *
     * @param psiVersions the list of loaded versions.
     *
     * @return true if found, false otherwise.
     */
    private boolean hasPsi1( List<Version> psiVersions ) {
        for ( Version version : psiVersions ) {
            if ( version.getNumber().equals( "1.0" ) ) {
                return true;
            }
        }
        return false;
    }

    private void readClassificationAndWritePsiXmls( File classificationFile, CvMapping mapping ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader( classificationFile ) );
        String line;

        int count = 1;

        while ( ( line = reader.readLine() ) != null ) {
            ExperimentListItem item = ExperimentListItem.parseString( line.trim() );

            getLog().debug( "Exporting item " + count + ": " + item );

            writePsiDataFile( item, mapping );

            count++;
        }
    }

    private void writePsiDataFile( ExperimentListItem item, CvMapping mapping ) throws IOException {
        getLog().debug( "\tLoading interactions" );
        Collection<Interaction> interactions = PsiFileGenerator.getInteractionsForExperimentListItem( item );

        for ( Version version : psiVersions ) {
            File targetFile = new File( targetPath, version.getFolderName() + "/" + item.getFilename() );

            long start = System.currentTimeMillis();

            PsiValidatorReport validationReport = PsiFileGenerator.writePsiData( interactions,
                                                                                 PsiVersion.valueOf( version.getNumber() ),
                                                                                 mapping,
                                                                                 targetFile,
                                                                                 version.isValidate() );

            if ( !validationReport.isValid() ) {
                writeToInvalidFile( item, version, validationReport );
            }

            long elapsed = System.currentTimeMillis() - start;

            getLog().debug( "\tTime to export to version " + version.getNumber() + ": " + new Chrono().printTime( elapsed ) );

        }

        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
    }

    private File getReverseMapping() {
        // if the reverseMappingFile does not exist, use the internal one
        if ( reverseMappingFile != null && reverseMappingFile.exists() ) {
            return reverseMappingFile;
        }

        return new File( PsiXmlGeneratorMojo.class.getResource( "/reverseMapping.txt" ).getFile() );
    }

    private void writeToInvalidFile( ExperimentListItem item, Version version, PsiValidatorReport report ) throws IOException {
        File filename = new File( targetPath, invalidFilePrefix + "_" + version.getNumber() + ".log" );

        FileWriter writer = new FileWriter( filename, true );
        writer.write( item.getFilename() + NEW_LINE );
        writer.write( report.toString() );
        writer.write( "---------------------------------------------------------------------" + NEW_LINE );
        writer.close();
    }
}