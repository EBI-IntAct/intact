/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugin.uniprotexport;

import org.apache.lucene.store.FSDirectory;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import uk.ac.ebi.intact.plugin.IntactAbstractMojo;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.search.IntactSearchEngine;

import java.io.*;

import psidev.psi.mi.search.SearchResult;

/**
 * Creates a file containing the DR lines updated with their respective interaction count coming from a given Lucene index.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @goal dr-update
 * @phase process-resources
 * @since 1.9.0
 */
public class DrLineInteractionCountUpdatetMojo extends IntactAbstractMojo {

    /**
     * Project instance
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * The path to the Lucene index to be used to query the count of interactions per proteins.
     *
     * @required
     */
    private String indexPath;

    /**
     * @parameter default-value="uniprotlinks.dat"
     * @required
     */
    private String uniprotLinksFilename;

    /**
     * @parameter default-value="target/uniprotlinks.dat"
     * @required
     */
    private String updatedUniprotLinksFilename;

    /**
     * @parameter default-value="true"
     * @required
     */
    private boolean overwrite;

    public String getIndexPath() {
        return indexPath;
    }

    public void setIndexPath( String indexPath ) {
        this.indexPath = indexPath;
    }

    public String getUniprotLinksFilename() {
        return uniprotLinksFilename;
    }

    public void setUniprotLinksFilename( String uniprotLinksFilename ) {
        this.uniprotLinksFilename = uniprotLinksFilename;
    }

    public String getUpdatedUniprotLinksFilename() {
        return updatedUniprotLinksFilename;
    }

    public void setUpdatedUniprotLinksFilename( String updatedUniprotLinksFilename ) {
        this.updatedUniprotLinksFilename = updatedUniprotLinksFilename;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite( boolean overwrite ) {
        this.overwrite = overwrite;
    }

    ///////////////////
    // Plugin

    public MavenProject getProject() {
        return project;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("DrLineInteractionCountUpdatetMojo in action");

        File uniprotLinks = new File( uniprotLinksFilename );
        isReadableFile( uniprotLinks );

        File luceneDirectory = new File( indexPath );
        isReadableDirectory( luceneDirectory );

        // setup the lucene index
        IntactSearchEngine engine;
        try {
            final FSDirectory fsDirectory = FSDirectory.getDirectory( luceneDirectory );
            engine = new IntactSearchEngine( fsDirectory );
        } catch ( IOException e ) {
            throw new MojoExecutionException( "An error occured while opening the Lucene index: " + luceneDirectory.getAbsolutePath() );
        }

        // prepare the output file
        File outputFile = new File( updatedUniprotLinksFilename );
        isWritableFile( outputFile );

        try {
            BufferedWriter out = new BufferedWriter( new FileWriter( outputFile ) );

            // read the imput file and for every line, run a lucene query and write the result out
            BufferedReader in = new BufferedReader( new FileReader( uniprotLinks ) );
            String drLine;
            int lineCount = 0;
            while ( ( drLine = in.readLine() ) != null ) {
                lineCount++;
                // extract uniprot AC
                final int idx = drLine.indexOf( ' ' );
                String uniprotAc = null;
                if( idx != -1 ) {
                    uniprotAc = drLine.substring( 0, idx );
                } else {
                    throw new MojoExecutionException("Could not parse DR line at line "+ lineCount +": " + drLine );
                }

                // query count of binary interactions
                SearchResult<IntactBinaryInteraction> interactions = engine.search( uniprotAc, 0, Integer.MAX_VALUE );

                // write to output file
                String count = String.valueOf( interactions.getTotalCount() );
                String updateDrLine = drLine.replace( "-", count );
                out.write( updateDrLine + "\n" );
            }
            in.close();
            out.flush();
            out.close();
        } catch ( IOException e ) {
            throw new MojoExecutionException("An error occured while updating the DR line interaction counts.", e);
        }

        getLog().info("Update completed, output file: "+ outputFile.getAbsolutePath() +".");
    }

    /////////////
    // Utils

    private void isWritableFile( File outputFile ) throws MojoExecutionException {
        if ( outputFile.exists() && !overwrite ) {
            throw new IllegalArgumentException( "Could not overwrite output file: " + updatedUniprotLinksFilename );
        } else if ( !outputFile.exists() ) {
            try {
                outputFile.createNewFile();
            } catch ( IOException e ) {
                throw new MojoExecutionException( "Failed to create a new file: " + outputFile.getAbsolutePath() );
            }
        }
        if ( !outputFile.canWrite() ) {
            throw new IllegalArgumentException( "You must give a writable output file" + updatedUniprotLinksFilename );
        }
    }

    private void isReadableDirectory( File luceneDirectory ) {
        if ( !luceneDirectory.exists() ) {
            throw new IllegalArgumentException( "You must give a existing uniprotLinks file: " + indexPath );
        }
        if ( !luceneDirectory.isDirectory() ) {
            throw new IllegalArgumentException( "You must give a existing directory: " + indexPath );
        }
        if ( !luceneDirectory.canRead() ) {
            throw new IllegalArgumentException( "You must give a readable uniprotLinks" + indexPath );
        }
    }

    private void isReadableFile( File uniprotLinks ) {
        if ( !uniprotLinks.exists() ) {
            throw new IllegalArgumentException( "You must give a existing uniprotLinks file: " + uniprotLinksFilename );
        }
        if ( !uniprotLinks.isFile() ) {
            throw new IllegalArgumentException( "You must give a file: " + indexPath );
        }
        if ( !uniprotLinks.canRead() ) {
            throw new IllegalArgumentException( "You must give a readable uniprotLinks" + uniprotLinksFilename );
        }
    }
}