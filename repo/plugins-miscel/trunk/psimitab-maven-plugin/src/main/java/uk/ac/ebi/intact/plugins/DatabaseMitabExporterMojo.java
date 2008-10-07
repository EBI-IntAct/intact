/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.maven.plugin.MojoExecutionException;
import uk.ac.ebi.intact.bridges.ontologies.util.OntologyUtils;
import uk.ac.ebi.intact.psimitab.converters.util.DatabaseMitabExporter;
// import uk.ac.ebi.intact.bridges.ontologies.OntologyMapping;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Goal which which exports database interactions into MITAB file and Lucene indexes.
 *
 * @goal db-mitab-export
 * @phase process-sources
 * @since 1.9.0
 */
public class DatabaseMitabExporterMojo extends AbstractPsimitabConverterMojo {

    /**
     * All ontologies used to build the ontology index.
     *
     * @parameter
     * @required
     */
    private Collection<OntologyMapping> ontologyMappings;

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

    public Collection<OntologyMapping> getOntologyMappings() {
        return ontologyMappings;
    }

    public void setOntologyMappings( Collection<OntologyMapping> ontologies ) {
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

    public void execute() throws MojoExecutionException {

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
            throw new MojoExecutionException( "Failed to prepare a MITAB writer", e );
        }

        Directory interactionDirectory = null;
        if ( interactionIndexPath != null ) {
            try {
                interactionDirectory = FSDirectory.getDirectory( interactionIndexPath );
            } catch ( IOException e ) {
                throw new MojoExecutionException( "Failed to build the interaction Directory", e );
            }
        }

        Directory interactorDirectory = null;
        if ( interactorIndexPath != null ) {
            try {
                interactorDirectory = FSDirectory.getDirectory( interactorIndexPath );
            } catch ( IOException e ) {
                throw new MojoExecutionException( "Failed to build the interactor Directory", e );
            }
        }

        // Action !

        Directory ontologyIndex = null;
        try {
            if (logWriter != null) logWriter.append( "Starting to index ontologies..." );
            ontologyIndex = FSDirectory.getDirectory( ontologyIndexPath );
            OntologyUtils.buildIndexFromObo( ontologyIndex, ontologyMappings.toArray( new OntologyMapping[ontologyMappings.size( )] ), true );
        } catch ( Exception e ) {
            throw new MojoExecutionException( "Error while building ontology index.", e );
        }

        Collection<String> ontologyNames = new ArrayList<String>( ontologyMappings.size() );
        for ( OntologyMapping ontology : ontologyMappings ) {
            ontologyNames.add( ontology.getName() );
        }

        DatabaseMitabExporter exporter = new DatabaseMitabExporter(ontologyIndex, ontologyNames.toArray( new String[ontologyNames.size( )] ) );
        try {
            logWriter.append( "Starting to export MITAB..." );
            exporter.exportAllInteractors( mitabWriter, interactionDirectory, interactorDirectory );
            logWriter.append( "Completed MITAB export..." );
        } catch ( Exception e ) {
            throw new MojoExecutionException( "Error while exporting MITAB data.", e );
        }

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
}