/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexSearcher;
import uk.ac.ebi.intact.bridges.ontologies.util.OntologyUtils;
import uk.ac.ebi.intact.plugin.IntactHibernateMojo;
import uk.ac.ebi.intact.psimitab.converters.util.DatabaseMitabExporter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Goal which which exports database interactions into MITAB file and Lucene indexes.
 *
 * @goal db-mitab-export
 * @phase process-sources
 * @since 1.9.0
 */
public class DatabaseMitabExporterMojo extends IntactHibernateMojo {

    private static final Log log = LogFactory.getLog( DatabaseMitabExporterMojo.class );

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
     * All ontologies used to build the ontology index.
     *
     * @parameter
     * @required
     */
    private List<OntologyMapping> ontologyMappings;

    /**
     * Where the ontology index is stored.
     *
     * @parameter
     * @required
     */
    private String ontologyIndexPath;

    /**
     * Where are the MITAB data to be written.
     *
     * @parameter
     * @required
     */
    private String mitabFilePath;

    /**
     * Should we overwrite existing data.
     *
     * @parameter
     * @required
     */
    private boolean overwrite;

    /**
     * Where is the interaction index to be created.
     *
     * @parameter
     */
    private String interactionIndexPath;

    /**
     * Where is the interactor index to be created.
     *
     * @parameter
     */
    private String interactorIndexPath;

    /**
     * Log file for reporting potential problems.
     *
     * @parameter
     * @required
     */
    private String logFilePath;

    ///////////////////////////
    // Getters and Setters

    public List<OntologyMapping> getOntologyMappings() {
        return ontologyMappings;
    }

    public void setOntologyMappings( List<OntologyMapping> ontologies ) {
        this.ontologyMappings = ontologies;
    }

    public String getOntologyIndexPath() {
        return ontologyIndexPath;
    }

    public void setOntologyIndexPath( String ontologyIndexPath ) {
        this.ontologyIndexPath = ontologyIndexPath;
    }

    public String getMitabFilePath() {
        return mitabFilePath;
    }

    public void setMitabFilePath( String mitabFilePath ) {
        this.mitabFilePath = mitabFilePath;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite( boolean overwrite ) {
        this.overwrite = overwrite;
    }

    public String getInteractionIndexPath() {
        return interactionIndexPath;
    }

    public void setInteractionIndexPath( String interactionIndexPath ) {
        this.interactionIndexPath = interactionIndexPath;
    }

    public String getInteractorIndexPath() {
        return interactorIndexPath;
    }

    public void setInteractorIndexPath( String interactorIndexPath ) {
        this.interactorIndexPath = interactorIndexPath;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath( String logFilePath ) {
        this.logFilePath = logFilePath;
    }

    ////////////////
    // Mojo

    public void executeIntactMojo() throws MojoExecutionException,
                                           MojoFailureException,
                                           IOException {

        System.out.println( "parameter 'ontologyMappings' = " + ontologyMappings );
        System.out.println( "parameter 'ontologyIndexPath' = " + ontologyIndexPath );
        System.out.println( "parameter 'overwrite' = " + overwrite );
        System.out.println( "parameter 'mitabFilePath' = " + mitabFilePath );
        System.out.println( "parameter 'interactionIndexPath' = " + interactionIndexPath );
        System.out.println( "parameter 'interactorIndexPath' = " + interactorIndexPath );
        System.out.println( "parameter 'logFilePath' = " + logFilePath );

        File logFile = null;
        Writer logWriter = null;
        if ( logFilePath != null ) {
            logFile = new File( logFilePath );

            // initialize the writer
            try {
                logWriter = new BufferedWriter( new FileWriter( logFile ) );
            } catch ( IOException e ) {
                e.printStackTrace();
                // We keep going, logs are not critical
            }
        }

        File mitabFile = new File( mitabFilePath );
        if ( mitabFile.exists() && !overwrite ) {
            throw new MojoExecutionException( "MITAB file exist and overwrite parameter set to true: " + mitabFilePath );
        }
        Writer mitabWriter = null;
        try {
            mitabWriter = new BufferedWriter( new FileWriter( mitabFile ) );
        } catch ( IOException e ) {
            log.error(e);
            throw new MojoExecutionException( "Failed to prepare a MITAB writer", e );
        }

        Directory interactionDirectory = null;
        if ( interactionIndexPath != null ) {
            try {
                interactionDirectory = FSDirectory.getDirectory( interactionIndexPath );
            } catch ( IOException e ) {
                log.error(e);
                throw new MojoExecutionException( "Failed to build the interaction Directory", e );
            }
        }

        Directory interactorDirectory = null;
        if ( interactorIndexPath != null ) {
            try {
                interactorDirectory = FSDirectory.getDirectory( interactorIndexPath );
            } catch ( IOException e ) {
                log.error(e);
                throw new MojoExecutionException( "Failed to build the interactor Directory", e );
            }
        }

        // Action !

        Directory ontologyIndex = null;
        try {
            if (logWriter != null) logWriter.append( "Starting to index ontologies..." );
            ontologyIndex = FSDirectory.getDirectory( ontologyIndexPath );
            OntologyUtils.buildIndexFromObo( ontologyIndex, ontologyMappings.toArray( new OntologyMapping[ontologyMappings.size( )] ), true );
        } catch ( Throwable e ) {
            log.error(e);
            throw new MojoExecutionException( "Error while building ontology index.", e );
        }

        Collection<String> ontologyNames = new ArrayList<String>( ontologyMappings.size() );
        for ( OntologyMapping ontology : ontologyMappings ) {
            ontologyNames.add( ontology.getName() );
        }

        OntologyIndexSearcher ontologyIndexSearcher = new OntologyIndexSearcher(ontologyIndex);

        DatabaseMitabExporter exporter = new DatabaseMitabExporter(ontologyIndexSearcher, ontologyNames.toArray( new String[ontologyNames.size( )] ) );
        try {
            if (logWriter != null) logWriter.append( "Starting to export MITAB..." );
            exporter.exportAllInteractors( mitabWriter, interactionDirectory, interactorDirectory );
            if (logWriter != null) logWriter.append( "Completed MITAB export..." );
        } catch ( Throwable e ) {
            log.error(e);
            if (logWriter != null) logWriter.append(ExceptionUtils.getFullStackTrace(e));
            throw new MojoExecutionException( "Error while exporting MITAB data.", e );
        }

        ontologyIndexSearcher.close();

        if ( logWriter != null ) {
            try {
                logWriter.flush();
                logWriter.close();
            } catch ( IOException e ) {
                e.printStackTrace();
                // no need to crash here, logs are not critical.
            }
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
}