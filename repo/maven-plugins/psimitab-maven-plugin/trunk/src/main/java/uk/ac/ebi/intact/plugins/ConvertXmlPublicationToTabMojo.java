/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import psidev.psi.mi.tab.expansion.SpokeWithoutBaitExpansion;
import uk.ac.ebi.intact.psimitab.ConvertXml2Tab;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Goal which converts each publications files (PSI XML 2.5) into a single PSIMITAB file in a given directory.
 *
 * @goal pub2tab
 * @phase process-sources
 */
public class ConvertXmlPublicationToTabMojo extends AbstractMojo {

    public static final String EXCEL_FILE_EXTENSION = ".xls";

    /**
     * Source directory to be processed.
     *
     * @parameter
     * @required
     */
    private String sourceDirectoryPath;

    /**
     * Target directory to be filled.
     *
     * @parameter
     * @required
     */
    private String targetDirectoryPath;

    public void execute() throws MojoExecutionException {

        File srcDir = new File( sourceDirectoryPath );
        if( ! srcDir.exists() ) {
            throw new MojoExecutionException( "Source directory does not exist: " + sourceDirectoryPath );
        }
        if( ! srcDir.canRead() ) {
            throw new MojoExecutionException( "Source directory cannot be read: " + sourceDirectoryPath );
        }

        File trgDir = new File( targetDirectoryPath );
        if( trgDir.exists() && ! trgDir.canWrite() ) {
            throw new MojoExecutionException( "Target directory exists but cannot be written: " + targetDirectoryPath );
        }

        // Build the target directory
        trgDir.mkdirs();

        System.out.println( "parameter 'sourceDirectoryPath' = " + sourceDirectoryPath );
        System.out.println( "parameter 'targetDirectoryPath' = " + targetDirectoryPath );

        // Prepare publication clustering
        PublicationClusterBuilder builder = new PublicationClusterBuilder( new File( sourceDirectoryPath ) );
        Map<Integer, Collection<File>> map = builder.build();

        // Prepare XML to TAB converter
        ConvertXml2Tab converter = new ConvertXml2Tab();
        converter.setOverwriteOutputFile( true );
        converter.setExpansionStrategy( new SpokeWithoutBaitExpansion() );
        converter.setInteractorPairCluctering( true );

        // Process datasets
        for ( Map.Entry<Integer, Collection<File>> entry : map.entrySet() ) {
            Integer pmid = entry.getKey();
            Collection<File> xmlFiles = entry.getValue();

            // all files should be from a same directory, pick the first one and get the parent src directory.
            File file = xmlFiles.iterator().next();
            File fileRootDir = file.getParentFile();

//            System.out.println( "file = " + file.getAbsolutePath() );

            // extract sub structure
            int idx = fileRootDir.getAbsolutePath().indexOf( srcDir.getAbsolutePath() );
            if( idx == -1 ) {
                throw new IllegalStateException( "\""+ fileRootDir.getAbsolutePath() + "\".indexOf(\""+ srcDir.getAbsolutePath() +"\") returned -1 !!" );
            }
            idx += sourceDirectoryPath.length();
//            System.out.println( "idx = " + idx );
            int len = fileRootDir.getAbsolutePath().length();
//            System.out.println( "len = " + len );
            File targetDir = new File( trgDir, fileRootDir.getAbsolutePath().substring( idx, len ) );
//            System.out.println( "targetDir = " + targetDir.getAbsolutePath() );

            // build potential missing sub-directory
            targetDir.mkdirs();

            File outputFile = new File( targetDir, pmid + EXCEL_FILE_EXTENSION );
            System.out.println( "Creating " + outputFile.getAbsolutePath() + " ...");

            // local config
            converter.setXmlFilesToConvert( xmlFiles );
            converter.setOutputFile( outputFile );

            // TODO do not write empty files on disk.

            // run it
            try {
                converter.convert();
            } catch ( Exception e ) {
                throw new MojoExecutionException( "Error while converting files to PSIMITAB... see nested exception.", e );
            }
        }
    }
}