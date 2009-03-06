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
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.InteractorXref;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.business.IntactTransactionException;

import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;

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
public class DrLineInteractionCountUpdatetMojo extends UniprotExportAbstractMojo {

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
     * @parameter default-value=""
     * @required
     */
    protected String targetPath;

    /**
     * The path to the Lucene index to be used to query the count of interactions per proteins.
     * 
     * @parameter
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
    private static final Pattern DRPATTERN = Pattern.compile("DR\\s+IntAct;\\s+(.*?);.*");

//    public String getTargetPath() {
//        return targetPath;
//    }

    public void setTargetPath( String targetPath ) {
        this.targetPath = targetPath;
    }

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

    public void executeIntactMojo() throws MojoExecutionException, MojoFailureException {

        getLog().info("DrLineInteractionCountUpdatetMojo in action");

        if ( uniprotLinksFilename == null ) {
            throw new MojoExecutionException( "You must give a non null uniprotLinks" );
        }
        File uniprotLinks = new File( uniprotLinksFilename );
        isReadableFile( uniprotLinks );

        if ( indexPath == null ) {
            throw new MojoExecutionException( "You must give a non null indexPath" );
        }
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
        File outputFile = new File( targetPath, updatedUniprotLinksFilename );
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
                String uniprotAc = extractUniprotAcFromDrLine(drLine);

                if (uniprotAc == null) {
                    throw new MojoExecutionException("Could not parse DR line at line " + lineCount + ": " + drLine);
                }

                // query count of binary interactions
                SearchResult<IntactBinaryInteraction> interactions = engine.search( uniprotAc, 0, 0 );
                int interactionCount = interactions.getTotalCount();
                getLog().info( uniprotAc + " has " + interactionCount + " interactions..." );

                // Check if out protein has splice variants and add their interaction count

                DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
                IntactContext.getCurrentInstance().getDataContext().beginTransaction();

                final ProteinDao proteinDao = daoFactory.getProteinDao();
                final List<ProteinImpl> proteins = proteinDao.getByUniprotId( uniprotAc );
                Protein protein = null;
                switch( proteins.size() ) {
                    case 0:
                        getLog().error( "Could not find protein for AC: " + uniprotAc );
                        break;
                    case 1:
                        protein = proteins.iterator().next();
                        break;
                    default:
                        getLog().error( "Found more than one protein ("+ proteins.size() +") for AC: " + uniprotAc );
                        protein = proteins.iterator().next();
                }

                if( protein != null ) {
                    // search for splice variants
                    final List<ProteinImpl> isoforms = proteinDao.getSpliceVariants( protein );
                    getLog().info( uniprotAc + " has " + isoforms.size() + " splice variants" );
                    for ( ProteinImpl isoform : isoforms ) {
                        final InteractorXref xref = ProteinUtils.getUniprotXref( isoform );
                        if( xref != null ) {
                            interactions = engine.search( xref.getPrimaryId(), 0, 0 );
                            interactionCount += interactions.getTotalCount();

                            getLog().info( uniprotAc + " has a splice variant ("+ xref.getPrimaryId() +") with " +
                                           interactions.getTotalCount() + " interaction(s)" );
                        } else {
                            getLog().error( "Could not find a UniProt AC for splice variant: " + isoform.getAc() );
                        }
                    }
                }

                try {
                    IntactContext.getCurrentInstance().getDataContext().commitTransaction();
                } catch ( IntactTransactionException e ) {
                    e.printStackTrace();
                    throw new MojoExecutionException("An error occured while updating the DR line interaction counts.", e);
                }
                // write to output file
                String updateDrLine = drLine.replace( "-", String.valueOf( interactionCount ) );
                getLog().info( drLine + "  ---("+ interactionCount +")-->  " + updateDrLine );

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

    private String extractUniprotAcFromDrLine(String drLine) {

        Matcher matcher = DRPATTERN.matcher(drLine);

        if (matcher.matches()) {
            return matcher.group(1).trim();
        }
        return null;
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