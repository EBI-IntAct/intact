/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import psidev.psi.mi.tab.expansion.SpokeExpansion;
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
     * Directory to be processed.
     *
     * @parameter
     * @required
     */
    private String directoryPath;

    public void execute() throws MojoExecutionException {

        System.out.println( "parameter 'directoryPath' = " + directoryPath );

        // Prepare publication clustering
        PublicationClusterBuilder builder = new PublicationClusterBuilder( new File( directoryPath ) );
        Map<Integer, Collection<File>> map = builder.build();

        // Prepare XML to TAB converter
        ConvertXml2Tab converter = new ConvertXml2Tab();
        converter.setOverwriteOutputFile( true );
        converter.setExpansionStrategy( new SpokeExpansion() );
        converter.setInteractorPairCluctering( true );

        // Process datasets
        for ( Map.Entry<Integer, Collection<File>> entry : map.entrySet() ) {
            Integer pmid = entry.getKey();
            Collection<File> xmlFiles = entry.getValue();

            // all files should be from a same directory, pick the first one and get the parent directory.
            File file = xmlFiles.iterator().next();
            File rootDirectory = file.getParentFile();

            File outputFile = new File( rootDirectory.getAbsolutePath() + File.separator + pmid + EXCEL_FILE_EXTENSION );

            // local config
            converter.setXmlFilesToConvert( xmlFiles );
            converter.setOutputFile( outputFile );

            // run it
            try {
                converter.convert();
            } catch ( Exception e ) {
                throw new MojoExecutionException( "Error while converting files to PSIMITAB... see nested exception.", e );
            }
        }
    }
}